package org.jboss.errai.demo.jms.client.local;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.jboss.errai.demo.jms.client.shared.User;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.gwt.user.client.Window;

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
    jmsDemoClient.showIncomeMessage(Utils.extractStringFromMessage(message));
  }

}
