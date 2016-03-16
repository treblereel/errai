package org.jboss.errai.jms.client.local;

import javax.ejb.EJBException;
import javax.jms.MessageListener;
import javax.jms.Queue;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.jms.shared.ErraiJMSMDBClientUtil;
import org.jboss.errai.jms.shared.impl.BytesMessageImpl;
import org.jboss.errai.jms.shared.impl.MapMessageImpl;
import org.jboss.errai.jms.shared.impl.ObjectMessageImpl;
import org.jboss.errai.jms.shared.impl.TextMessageImpl;
import org.jboss.errai.jms.shared.impl.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform a bridge between server and client, it transforms Errai message into JMS message by calling onMessage(... ) method of
 * client MDB bean.
 * 
 * @author Dmitrii Tikhomirov
 *
 */

public class MessageDrivenBeanReceiver {

    private static final Logger logger = LoggerFactory.getLogger(MessageDrivenBeanReceiver.class);

    private String destination;
    private Boolean autoAcknowledgeMode = true; // in general, true by default like all MDBeans.

    private String destinationLookup;
    private String destinationType;
    private MessageBus messageBus = ErraiBus.get();

    private MessageListener messageListener;
    private String name;

    public MessageDrivenBeanReceiver() {

    }

    /**
     * Called when MDB is ready to receive the messages
     */
    public void finish() {

        messageBus.subscribe(destination, new MessageCallback() {
            @Override
            public void callback(Message message) {
                logger.debug("callback " + destination + " message id: " + message.get(String.class, "JMSID") + " toSubject:"
                        + message.getSubject());
                try {
                    javax.jms.Message jmsMessage = convertErraiMessageToJmsMessage(message);
                    messageListener.onMessage(jmsMessage);
                    constractResponse(message, true);
                } catch (Exception e) {
                    constractResponse(message, false);
                }
            }

            /**
             * Construct receiver for Queue messages to inform server that MDB got that message, in other case (like exception )
             * server will try to send that message to another customer;
             * 
             * @param message which has been sent
             * @param result of the call
             */
            private void constractResponse(Message message, Boolean result) {
                logger.debug("DESTINATION : " + message.get(String.class, "JMSDestinationType"));
                if (message.get(String.class, "JMSDestinationType").equals(Queue.class.getSimpleName())) {
                    MessageBuilder.createConversation(message).subjectProvided().signalling()
                            .with("result", result ? "success" : "failed").with("JMSID", message.get(String.class, "JMSID"))
                            .noErrorHandling().reply();
                }
            }
        });
        logger.debug(" name :" + name + " destinationLookup :" + destinationLookup + " destinationType :" + destinationType
                + " subscribe :" + destination);
    }

    public String getDestinationLookup() {
        return destinationLookup;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public String getName() {
        return name;
    }

    public void setAcknowledgeMode(String acknowledgeMode) {
        if (acknowledgeMode.equals("Auto-acknowledge")) {
            this.autoAcknowledgeMode = true;
        } else {
            autoAcknowledgeMode = false;
        }
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDestinationLookup(String destinationLookup) {
        this.destinationLookup = destinationLookup;
        setDestination(ErraiJMSMDBClientUtil.getDestinationFromAnnotatedProperty(getDestinationLookup()));
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public <T extends MessageListener> void setMDBBean(T t) {
        this.messageListener = t;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Construct Jms message from Errai Bus message
     * 
     * @param message
     * @return instance of javax.jms.Message Corresponding to its type
     */
    private javax.jms.Message convertErraiMessageToJmsMessage(Message message) {
        byte type = message.get(byte.class, "type");
        if (type == Type.TEXT_TYPE) {
            return new TextMessageImpl(message);
        } else if (type == Type.OBJECT_TYPE) {
            return new ObjectMessageImpl(message);
        } else if (type == Type.BYTES_TYPE) {
            return new BytesMessageImpl(message);
        } else if (type == Type.MAP_TYPE) {
            return new MapMessageImpl(message);
        } else if (type == Type.STREAM_TYPE) {
            throw new EJBException("STREAM message type is not supported ");
        }
        return null;
    }

}
