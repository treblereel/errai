package org.jboss.errai.demo.jms.client.local;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 
 * @author Dmitrii Tikhomirov
 */
@MessageDriven(name = "HelloWorldTopicMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/HelloWorldTopicMDB"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class JmsDemoMDBTopic implements MessageListener {

    @Inject
    private JmsDemoClient jmsDemoClient;

    @Override
    public void onMessage(Message message) {
        try {
            jmsDemoClient.showIncomeMessage(Utils.extractStringFromMessage(message),
                    message.getStringProperty("forLabel"));
        } catch (JMSException e) {
            throw new EJBException(e.getMessage());
        }
    }

}
