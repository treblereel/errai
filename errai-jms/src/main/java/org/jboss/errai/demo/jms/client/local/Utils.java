package org.jboss.errai.demo.jms.client.local;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;

import javax.ejb.EJBException;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.jboss.errai.demo.jms.client.shared.User;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */

public class Utils {

  public static String extractStringFromMessage(Message message) {
    String result = "";

    try {
      Byte type = Byte.valueOf(message.getJMSType());
      if (type.equals(org.jboss.errai.jms.shared.impl.Type.TEXT_TYPE)) {
        TextMessage textMessage = (TextMessage) message;
        result = textMessage.getText();

      }
      else if (type.equals(org.jboss.errai.jms.shared.impl.Type.OBJECT_TYPE)) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        User user = (User) objectMessage.getObject();
        result = user.toString();
      }
      else if (type.equals(org.jboss.errai.jms.shared.impl.Type.BYTES_TYPE)) {
        BytesMessage objectMessage = (BytesMessage) message;

        byte[] bytes = extractByteArrayFromMessage(objectMessage);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
          sb.append(bytes[i]);
        }
        result = sb.toString();
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
        result = sb.toString();
      }
    } catch (JMSException e) {
      throw new EJBException(e);
    }

    return result;

  }

  private static byte[] extractByteArrayFromMessage(BytesMessage message) throws JMSException {
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
