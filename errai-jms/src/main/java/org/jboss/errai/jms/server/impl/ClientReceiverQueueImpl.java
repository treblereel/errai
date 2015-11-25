package org.jboss.errai.jms.server.impl;

import java.util.Map.Entry;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;

import org.jboss.errai.bus.client.api.builder.MessageBuildCommand;
import org.jboss.errai.bus.client.api.builder.MessageBuildSendableWithReply;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.jms.server.ClientQueueListener;
import org.jboss.errai.jms.server.ClientReceiver;
import org.jboss.errai.jms.server.QueueDeliveryWatcher;
import org.jboss.errai.jms.util.ErraiJMSMDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Named("Queue")
public class ClientReceiverQueueImpl extends ClientReceiver {
  private static final Logger logger = LoggerFactory.getLogger(ClientReceiverQueueImpl.class);

  @Inject
  private ClientQueueListener clientCache;
  @Inject
  private MessageBus messageBus;
  @Inject
  private QueueDeliveryWatcher queueDeliveryWatcher;

  public ClientReceiverQueueImpl() {

  }

  @Override
  public void processToMessageBus(Message message) throws JMSException {

    String sessionId = clientCache.next(((Queue) message.getJMSDestination()).getQueueName());

    logger.debug("processToMessageBus " + ((Queue) message.getJMSDestination()).getQueueName() + " >" + sessionId + "< "
            + ((Queue) message.getJMSDestination()).getQueueName());

    if (!sessionId.equals("")) {
      MessageBuildCommand<MessageBuildSendableWithReply> messageBuildCommand = ErraiJMSMDBUtil
              .toMessageBusMessage(message);

      addErrorAndDeliveryHandlers(messageBuildCommand);
      org.jboss.errai.bus.client.api.messaging.Message erraiMessage = messageBuildCommand.getMessage();
      sendMessage(sessionId, erraiMessage);
    }
  }

  @Asynchronous
  public void sendMessage(String sessionId, org.jboss.errai.bus.client.api.messaging.Message erraiMessage) {
    erraiMessage.getParts().put("SessionID", sessionId);
    erraiMessage.sendNowWith(messageBus);
    queueDeliveryWatcher.submitMessage(erraiMessage);
  }

  private void addErrorAndDeliveryHandlers(MessageBuildCommand<MessageBuildSendableWithReply> message) {
    message.errorsHandledBy(new ErrorCallback<org.jboss.errai.bus.client.api.messaging.Message>() {
      @Override
      public boolean error(org.jboss.errai.bus.client.api.messaging.Message message, Throwable throwable) {
        onError(message);
        return true;
      }
    }).repliesTo(new MessageCallback() {
      @Override
      public void callback(org.jboss.errai.bus.client.api.messaging.Message message) {
        String result = message.get(String.class, "result");
        logger.info(" delivered: " + result + " messageId:" + message.get(String.class, "JMSID"));
        if (result.equals("success")) {
          queueDeliveryWatcher.messageDelivered(message);
        }
        else {
          onError(message);
        }
      }
    });
  }

  private void onError(org.jboss.errai.bus.client.api.messaging.Message message) {
    String sessionId = clientCache.next(message.getSubject());
    logger.info("resend message to another consumer because of Exception: sessionId : " + sessionId + ", destination :"
            + message.getSubject());
    message.getParts().put("SessionID", sessionId);
    message.sendNowWith(messageBus);
  }
}
