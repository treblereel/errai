package org.jboss.errai.jms.server.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;

import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.jms.server.ClientReceiver;
import org.jboss.errai.jms.util.ErraiJMSMDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Named("Topic")
public class ClientReceiverTopicImpl extends ClientReceiver {

  @Inject
  private MessageBus messageBus;

  public ClientReceiverTopicImpl() {

  }

  @Override
  public void processToMessageBus(Message message) throws JMSException {
    ErraiJMSMDBUtil.toMessageBusMessage(message).getMessage().sendNowWith(messageBus);
  }

}
