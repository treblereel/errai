package org.jboss.errai.jms.server;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */
public interface ClientReceiver {
  /**
   * Send message to Errai Bus
   */ 
  public void processToMessageBus(Message message) throws JMSException;
}
