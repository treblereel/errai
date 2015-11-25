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
 * @author chani
 * Session.AUTO_ACKNOWLEDGE/CLIENT_ACKNOWLEDGE:
 */
@MessageDriven(name = "HelloWorldQueueMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/HelloWorldQueueMDB"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class JmsDemoMDBQueue implements MessageListener {

  @Inject
  private JmsDemoClient jmsDemoClient;
  
  @Inject
  private Logger logger;

  @Override
  public void onMessage(Message message) {
    try {
      TextMessage textMessage = (TextMessage) message;
      
      if(jmsDemoClient !=null){
        jmsDemoClient.showIncomeMessage(this.getClass().getSimpleName() + ": " + textMessage.getText());
      }
    } catch (JMSException e) {
      logger.error(e.getMessage());
    }
    //throw new EJBException();
  }
}
