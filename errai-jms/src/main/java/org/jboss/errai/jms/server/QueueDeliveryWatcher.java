package org.jboss.errai.jms.server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.jms.server.impl.ClientReceiverQueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@Startup
@Singleton
@DependsOn("ClientQueueListener")
public class QueueDeliveryWatcher {
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  class MessageState {
    private Message message;
    private Boolean delivered;
    private int count = 0;

    public MessageState(Message message, Boolean delivered) {
      setMessage(message);
      setDelivered(delivered);
    }

    public int getCount() {
      return count;
    }

    public Boolean getDelivered() {
      return delivered;
    }

    public Message getMessage() {
      return message;
    }

    public void setCount(int count) {
      this.count = count;
    }

    public void setDelivered(Boolean delivered) {
      this.delivered = delivered;
    }

    public void setMessage(Message message) {
      this.message = message;
    }

  }
  class WatcherTimerTask extends TimerTask {
    private String messageId;

    public WatcherTimerTask(String messageId) {
      this.messageId = messageId;
    }

    @Override
    public void run() {

      if (map.containsKey(messageId) && !map.get(messageId).getDelivered()) {
        int deliveryCount = map.get(messageId).getMessage().get(int.class, "JMSDeliveryCount");
        map.get(messageId).getMessage().set("JMSDeliveryCount", deliveryCount++);
        String sessionId = clientCache.next(map.get(messageId).getMessage().getSubject());
        ((ClientReceiverQueueImpl)clientReceiverQueue).sendMessage(sessionId, map.get(messageId).getMessage());
      }
      else if (map.containsKey(messageId) && map.get(messageId).getDelivered()) {
        map.remove(messageId);
      }
    };

  }

  @Inject
  @Named("Queue")
  private ClientReceiver clientReceiverQueue;

  @Inject
  private ClientQueueListener clientCache;

  private ConcurrentHashMap<String, MessageState> map = new ConcurrentHashMap<String, MessageState>();

  public void messageDelivered(Message message) {
    logger.debug("messageDelivered " + message.get(String.class, "JMSID"));
    map.get(message.get(String.class, "JMSID")).setDelivered(true);
  }

  @Asynchronous
  public void submitMessage(Message message) {

    String messageId = message.get(String.class, "JMSID");
    if (map.containsKey(messageId)) {

      int count = map.get(messageId).getCount();
      count++;
      map.get(messageId).setCount(count);
      if (count >= 10) {
        logger.error("Unable to send a message to " + map.get(messageId).getMessage().getSubject()
                + ", deliveryCount = " + map.get(messageId).getCount());
        map.remove(messageId);
      }
      else {
        map.get(messageId).setCount(count++);
        Timer timer = new Timer();
        timer.schedule(new WatcherTimerTask(messageId), 10 * 1000);
      }

    }
    else {
      map.put(messageId, new MessageState(message, false));
      Timer timer = new Timer();
      timer.schedule(new WatcherTimerTask(messageId), 10 * 1000);
    }

  }

}
