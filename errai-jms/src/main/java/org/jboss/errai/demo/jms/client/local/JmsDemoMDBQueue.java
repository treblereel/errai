package org.jboss.errai.demo.jms.client.local;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;


/**
 * 
 * @author Dmitrii Tikhomirov
 */

@MessageDriven(name = "HelloWorldQueueMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/HelloWorldQueueMDB"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class JmsDemoMDBQueue implements MessageListener {

  @Inject
  private JmsDemoClient jmsDemoClient;
  
  @Override
  public void onMessage(Message message) {
    jmsDemoClient.showIncomeMessage(Utils.extractStringFromMessage(message));
  }
}
