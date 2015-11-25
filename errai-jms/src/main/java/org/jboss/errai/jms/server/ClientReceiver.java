package org.jboss.errai.jms.server;

import javax.jms.JMSException;
import javax.jms.Message;

public abstract class ClientReceiver {
  public abstract void processToMessageBus(Message message) throws JMSException;
}
