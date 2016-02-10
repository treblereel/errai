package org.jboss.errai.demo.jms.client.shared;


/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */

public class Commands {
  
  
    public static final String TEXT_MESSAGE_QUEUE    = "sendTextMessageQueue";
    public static final String TEXT_MESSAGE_TOPIC    = "sendTextMessageTopic";
    public static final String OBJECT_MESSAGE_QUEUE  = "sendObjectMessageQueue";
    public static final String BYTE_MESSAGE_QUEUE    = "sendByteMessageQueue";
    public static final String MAP_MESSAGE_QUEUE     = "sendMapMessageQueue";
    
    public static final String[] listOf = {TEXT_MESSAGE_QUEUE,TEXT_MESSAGE_TOPIC,OBJECT_MESSAGE_QUEUE,BYTE_MESSAGE_QUEUE,MAP_MESSAGE_QUEUE};
    
}
