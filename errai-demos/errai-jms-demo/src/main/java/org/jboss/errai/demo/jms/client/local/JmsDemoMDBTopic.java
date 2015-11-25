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

import org.jboss.errai.demo.jms.shared.User;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author chani Session.AUTO_ACKNOWLEDGE/CLIENT_ACKNOWLEDGE:
 */
@MessageDriven(name = "HelloWorldTopicMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "topic/HelloWorldTopicMDB"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class JmsDemoMDBTopic implements MessageListener {

  @Inject
  private JmsDemoClient jmsDemoClient;

  @Inject
  private Logger logger;

  @Override
  public void onMessage(Message message) {

    try {
      Byte type = Byte.valueOf(message.getJMSType());
      if (type.equals(org.jboss.errai.jms.shared.impl.Type.TEXT_TYPE)) {
        TextMessage textMessage = (TextMessage) message;
        if (jmsDemoClient != null) {
          jmsDemoClient.showIncomeMessage(this.getClass().getSimpleName()
                  + ": textMessage " + textMessage.getText());
        }
      }
      else if (type.equals(org.jboss.errai.jms.shared.impl.Type.OBJECT_TYPE)) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        User user = (User) objectMessage.getObject();
        jmsDemoClient.showIncomeMessage(this.getClass().getSimpleName()
                + ": objectMessage : " + user.toString());

      }
      else if (type.equals(org.jboss.errai.jms.shared.impl.Type.BYTES_TYPE)) {
        BytesMessage objectMessage = (BytesMessage) message;

        byte[] bytes = extractByteArrayFromMessage(objectMessage);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
          sb.append(bytes[i]);
        }
        jmsDemoClient.showIncomeMessage(this.getClass().getSimpleName()
                + ": byteMessage : " + sb.toString());
      }
      else if (type.equals(org.jboss.errai.jms.shared.impl.Type.MAP_TYPE)) {
        MapMessage objectMessage = (MapMessage) message;
        StringBuffer sb = new StringBuffer();
        try {
          Enumeration<String> names = objectMessage.getMapNames();
          while (names.hasMoreElements()) {
            String param = (String) names.nextElement();
            sb.append("param : ");
            sb.append(objectMessage.getObject(param));
            sb.append(": ");
          }
        } catch (JMSException e) {
          throw new EJBException("Can't parse message " + e);
        }
        jmsDemoClient.showIncomeMessage(this.getClass().getSimpleName()
                + ": MapMessage : " + sb.toString());
      }
    } catch (JMSException e) {
      logger.error(e.getMessage());
    }
  }

  private byte[] extractByteArrayFromMessage(BytesMessage message)
          throws JMSException {
    ByteArrayOutputStream oStream = new ByteArrayOutputStream(1024);
    byte[] buffer = new byte[1024];
    int bufferCount = -1;
    while ((bufferCount = message.readBytes(buffer)) >= 0) {
      oStream.write(buffer, 0, bufferCount);
      if (bufferCount < 1024) {
        break;
      }
    }

    return oStream.toByteArray();
  }

}
