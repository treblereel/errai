package org.jboss.errai.jms.server.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;

import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.jms.server.ClientReceiver;
import org.jboss.errai.jms.util.ErraiJMSMDBUtil;

/**
 * Send message to Errai Bus
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@Stateless
@Named("Topic")
public class ClientReceiverTopicImpl implements ClientReceiver {

  @Inject
  private MessageBus messageBus;

  public ClientReceiverTopicImpl() {

  }

  /**
   * Send message to Errai Bus
   * 
   */ 
  @Override
  public void processToMessageBus(Message message) throws JMSException {
    ErraiJMSMDBUtil.toMessageBusMessage(message).getMessage().sendNowWith(messageBus);
  }

}
