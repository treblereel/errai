package org.jboss.errai.demo.jms.client.local;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 
 * @author Dmitrii Tikhomirov
 */

@MessageDriven(name = "HelloWorldQueueMDB2", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/HelloWorldQueueMDB2"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class JmsDemoMDB2Queue implements MessageListener {

  @Inject
  private JmsDemoClient jmsDemoClient;
  
  @Override
  public void onMessage(Message message) {
  }
}
