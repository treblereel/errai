package org.jboss.errai.demo.jms.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;

import org.jboss.errai.demo.jms.client.shared.Commands;
import org.jboss.errai.demo.jms.client.shared.User;
import org.slf4j.Logger;

@JMSDestinationDefinitions(value = {
        @JMSDestinationDefinition(name = "java:/queue/HelloWorldQueueMDB",
                interfaceName = "javax.jms.Queue",
                destinationName = "HelloWorldQueueMDB"),
        @JMSDestinationDefinition(name = "java:/topic/HelloWorldTopicMDB",
                interfaceName = "javax.jms.Topic",
                destinationName = "HelloWorldTopicMDB") })
@Stateless
public class JmsService {

    @Inject
    private Logger logger;

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/queue/HelloWorldQueueMDB")
    private Queue queue;

    @Resource(lookup = "java:/topic/HelloWorldTopicMDB")
    private Topic topic;

    private byte[] bytes = "This is an example".getBytes();

    public void send(String messageType) {
        if (messageType.equals(Commands.BYTE_MESSAGE_QUEUE)) {
            sendByteMessageQueue();
        } else if (messageType.equals(Commands.TEXT_MESSAGE_QUEUE)) {
            sendTextMessangeQueue();
        } else if (messageType.equals(Commands.MAP_MESSAGE_QUEUE)) {
            sendMapMessageQueue();
        } else if (messageType.equals(Commands.OBJECT_MESSAGE_QUEUE)) {
            sendObjectMessageQueue();
        } else if (messageType.equals(Commands.TEXT_MESSAGE_TOPIC)) {
            sendTextMessangeTopic();
        }
    }

    public void sendTextMessangeQueue() {
        javax.jms.JMSProducer jMSProducer = context.createProducer();
        jMSProducer.setProperty("boolean", false);
        jMSProducer.setProperty("string", "string");
        jMSProducer.send(queue, "This is an example");
        logger.info(" sendTextMessangeQueue : This is an example");
    }

    public void sendTextMessangeTopic() {
        javax.jms.JMSProducer jMSProducer = context.createProducer();
        jMSProducer.setProperty("boolean", false);
        jMSProducer.setProperty("string", "string");
        jMSProducer.send(topic, "The sky above the port was the color of television, tuned to a dead channel");
        logger.info(" sendTextMessangeTopic: The sky above the port was the color of television, tuned to a dead channel");
    }

    public void sendObjectMessageQueue() {
        javax.jms.JMSProducer jMSProducer = context.createProducer();
        jMSProducer.setProperty("boolean", false);
        jMSProducer.setProperty("string", "string");
        ObjectMessage message = context.createObjectMessage(new User("Bilbo Baggins", new Date(), true));
        jMSProducer.send(queue, message);
        logger.info(" sendObjectMessageQueue ");
    }

    public void sendByteMessageQueue() {
        javax.jms.JMSProducer jMSProducer = context.createProducer();
        jMSProducer.setProperty("boolean", false);
        jMSProducer.setProperty("string", "string");
        jMSProducer.send(queue, bytes);
        logger.info(" sendByteMessageQueue ");
    }

    public void sendMapMessageQueue() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alive?", true);
        map.put("message", "you shall not pass");
        map.put("customer", "Gandalf the Grey");

        javax.jms.JMSProducer jMSProducer = context.createProducer();
        jMSProducer.setProperty("boolean", false);
        jMSProducer.setProperty("string", "string");
        jMSProducer.send(queue, map);
        logger.info(" sendMapMessageQueue ");
    }
}
