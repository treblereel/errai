package org.jboss.errai.jms.server;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.jms.server.impl.ClientReceiverQueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
@DependsOn("ClientQueueListener")
public class QueueDeliveryWatcher {
  @Inject
  private ClientReceiverQueueImpl clientReceiverQueue;
  @Inject
  private ClientQueueListener clientCache;

  private static final Logger logger = LoggerFactory.getLogger(QueueDeliveryWatcher.class);
  private ConcurrentHashMap<String, MessageState> map = new ConcurrentHashMap<String, MessageState>();

  @Asynchronous
  public void submitMessage(Message message) {

    String messageId = message.get(String.class, "JMSID");
    if (map.containsKey(messageId)) {
      /*
       * MessageState state = map.get(messageId); int count = state.getCount();
       * state.setCount(count++);
       */

      int count = map.get(messageId).getCount();
 //     count++;
  //    map.get(messageId).setCount(count);
      /*
       * System.out.println("\n\n " +
       * map.get(messageId).getMessage().get(String.class, "value") + " " +
       * map.get(messageId).getCount()); int t = map.get(messageId).getCount();
       * t++; System.out.println(t); map.get(messageId).setCount(t);
       * System.out.println(t + "\n\n");
       */

      logger.info("submitMessage " + map.get(messageId).getMessage().get(String.class, "value") + " " + map.size() + " "
              + map.get(messageId).getCount());

      if (count >= 10) {
        logger.error("Unable to send a message to " + map.get(messageId).getMessage().getSubject()
                + ", deliveryCount = " + map.get(messageId).getCount());
        map.remove(messageId);
      }
      else {
        map.get(messageId).setCount(count++);
        // map.replace(messageId, state);
        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(messageId), 10 * 1000);
      }

    }
    else {
      map.put(messageId, new MessageState(message, false));
      Timer timer = new Timer();
      timer.schedule(new MyTimerTask(messageId), 10 * 1000);
    }

    /*
     * int deliveryCount = message.get(int.class, "JMSDeliveryCount"); if
     * (deliveryCount >= 10) { if (map.containsKey(messageId)) {
     * map.remove(messageId);
     * 
     * logger.error("Unable to send a message to " +
     * map.get(messageId).getMessage().getSubject() + ", deliveryCount = " +
     * deliveryCount); } } else { map.put(messageId, new MessageState(message,
     * false)); Timer timer = new Timer(); timer.schedule(new
     * MyTimerTask(messageId), 10 * 1000); }
     */
  }

  class MyTimerTask extends TimerTask {
    private String messageId;

    public MyTimerTask(String messageId) {
      logger.info("MyTimerTask constructed " + map.get(messageId).getMessage().get(String.class, "value") + " "
              + map.size() + " " + map.get(messageId).getCount());
      this.messageId = messageId;
    }

    @Override
    public void run() {
      logger.info("MyTimerTask run : " + messageId + " value "
              + map.get(messageId).getMessage().get(String.class, "value"));

      if (map.containsKey(messageId) && !map.get(messageId).getDelivered()) {
        logger.info("MyTimerTask not delivered => resend : " + messageId + " "
                + map.get(messageId).getMessage().get(String.class, "value"));
        int deliveryCount = map.get(messageId).getMessage().get(int.class, "JMSDeliveryCount");
        map.get(messageId).getMessage().set("JMSDeliveryCount", deliveryCount++);
        String sessionId = clientCache.next(map.get(messageId).getMessage().getSubject());
        clientReceiverQueue.sendMessage(sessionId, map.get(messageId).getMessage());

      }
      else if (map.containsKey(messageId) && map.get(messageId).getDelivered()) {
        logger.info("MyTimerTask delivered => remove : " + messageId + " value "
                + map.get(messageId).getMessage().get(String.class, "value"));
        map.remove(messageId);
      }
    };

  }

  class MessageState {
    private Message message;
    private Boolean delivered;
    private int count = 0;

    public MessageState(Message message, Boolean delivered) {
      setMessage(message);
      setDelivered(delivered);
    }

    public Boolean getDelivered() {
      return delivered;
    }

    public void setDelivered(Boolean delivered) {
      this.delivered = delivered;
    }

    public Message getMessage() {
      return message;
    }

    public void setMessage(Message message) {
      this.message = message;
    }

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

  }

  public void messageDelivered(Message message) {
    logger.warn("messageDelivered " + message.get(String.class, "JMSID"));
    map.get(message.get(String.class, "JMSID")).setDelivered(true);
  }

}
