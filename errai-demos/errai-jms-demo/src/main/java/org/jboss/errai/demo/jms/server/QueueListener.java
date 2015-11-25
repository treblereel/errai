package org.jboss.errai.demo.jms.server;

import javax.ejb.SessionContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueListener {
  private static final Logger logger = LoggerFactory
          .getLogger(QueueListener.class);

  QueueListener(InitialContext context) {
    logger.warn("\n\n constracted QueueListener");
    
    
    
    try {
      Queue queue = (Queue) context.lookup("java:/queue/HelloWorldQueueMDB");
      QueueConnectionFactory qcf = (QueueConnectionFactory) context
              .lookup("ConnectionFactory");
      QueueConnection con = qcf.createQueueConnection();
      final QueueSession session = con.createQueueSession(false,
              Session.AUTO_ACKNOWLEDGE);
      MessageConsumer messageConsumer = session.createConsumer(queue);
      messageConsumer.setMessageListener(new MessageListener() {
        public void onMessage(Message message) {
          
          logger.warn("\n\n GOT !!!");

          try {
            message.acknowledge();
          } catch (JMSException e) {
            e.printStackTrace();
          }
        }
      });
    } catch (JMSException e1) {
      e1.printStackTrace();
    } catch (NamingException e) {
      e.printStackTrace();
    }
    logger.warn("\n\n constracted Qdone");

  }

}
