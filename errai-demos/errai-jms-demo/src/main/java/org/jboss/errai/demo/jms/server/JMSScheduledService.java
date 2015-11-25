package org.jboss.errai.demo.jms.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections.map.HashedMap;
import org.jboss.errai.bus.client.api.SubscribeListener;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.framework.SubscriptionEvent;
import org.jboss.errai.demo.jms.shared.User;

@JMSDestinationDefinitions(value = {
    @JMSDestinationDefinition(name = "java:/queue/HelloWorldQueueMDB", interfaceName = "javax.jms.Queue", destinationName = "HelloWorldQueueMDB"),
    @JMSDestinationDefinition(name = "java:/topic/HelloWorldTopicMDB", interfaceName = "javax.jms.Topic", destinationName = "HelloWorldTopicMDB") })
@Startup
@Singleton
public class JMSScheduledService {
  private static final Logger logger = LoggerFactory
          .getLogger(JMSScheduledService.class);
  
  private AtomicInteger counter = new AtomicInteger();
  
  @Inject
  private MessageBus msgBus;

  @Inject
  private JMSContext context;

  @Resource(lookup = "java:/queue/HelloWorldQueueMDB")
  private Queue queue;

  @Resource(lookup = "java:/topic/HelloWorldTopicMDB")
  private Topic topic;
  
  private byte[] bytes = "This is an example".getBytes();

  public JMSScheduledService() {

  }

  @PostConstruct
  private void init() {
  }

  @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  private void sendTextMessangeQueue() {
    String label = new Integer(counter.getAndIncrement()).toString();
    javax.jms.JMSProducer jMSProducer = context.createProducer();
    jMSProducer.setProperty("boolean", false);
    jMSProducer.setProperty("string", "string");
    jMSProducer.send(queue, label);
    logger.info(" sendTextMessangeQueue " + label);
  }

//  @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  private void sendTextMessangeTopic() {
    javax.jms.JMSProducer jMSProducer = context.createProducer();
    jMSProducer.setProperty("boolean", false);
    jMSProducer.setProperty("string", "string");
    jMSProducer.send(topic, "Topic " + new Date().toString());
    logger.info(" sendTextMessangeTopic " + new Date());
  }
  
 // @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  private void sendObjectMessageMessangeQueue() {
    javax.jms.JMSProducer jMSProducer = context.createProducer();
    jMSProducer.setProperty("boolean", false);
    jMSProducer.setProperty("string", "string");
    ObjectMessage message  = context.createObjectMessage(new User("Bilbo Baggins",new Date(),true));
    jMSProducer.send(topic, message);
    logger.info(" sendObjectMessageMessangeQueue " + new Date());
  }
  
  //@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  private void sendByteMessageMessangeQueue() {
    javax.jms.JMSProducer jMSProducer = context.createProducer();
    jMSProducer.setProperty("boolean", false);
    jMSProducer.setProperty("string", "string");
    jMSProducer.send(topic, bytes);
    logger.info(" sendByteMessageMessangeQueue " + new Date());
  }
  
  //@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
  private void sendMapMessageMessangeQueue() {
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("alive?", true);
    map.put("message", "you shall not pass");
    map.put("customer", "Gandalf the Grey");
    
    javax.jms.JMSProducer jMSProducer = context.createProducer();
    jMSProducer.setProperty("boolean", false);
    jMSProducer.setProperty("string", "string");
    jMSProducer.send(topic, map);
    logger.info(" sendMapMessageMessangeQueue " + new Date());
  }
  
}
