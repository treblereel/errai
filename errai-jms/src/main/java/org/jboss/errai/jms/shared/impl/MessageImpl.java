package org.jboss.errai.jms.shared.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.ejb.EJBException;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */
public class MessageImpl implements Serializable, Message {

    private static final long serialVersionUID = 1L;
    public static final byte TYPE = org.jboss.errai.jms.shared.impl.Type.DEFAULT_TYPE;

    private Object body;
    private String correlationID;
    private int deliveryMode;
    private long deliveryTime;
    private Destination destination;
    private Boolean durable;
    private long expiration;
    private String jMSMessageID;
    private Boolean jMSRedelivered = false;
    /**
     * cannot support private field marshalling of long type (not supported by JSNI) for field:
     * org.jboss.errai.jms.shared.impl.MessageImpl#jMSTimestamp
     */
    private long jMSTimestamp;
    private int priority;
    /**
     * because of errai
     */
    private Map<String, Object> properties = new HashMap<String, Object>();
    private Boolean readOnly = true;
    private Destination replyToDestination;
    private String type;

    public MessageImpl() {

    }

    @SuppressWarnings("unchecked")
    public MessageImpl(Message message) {
        try {
            this.setJMSCorrelationID(message.getJMSCorrelationID());
            this.setJMSDeliveryMode(message.getJMSDeliveryMode());
            this.setJMSDeliveryTime(message.getJMSDeliveryTime());
            this.setJMSDestination(toDestination(message.getJMSDestination()));
            this.setJMSExpiration(message.getJMSExpiration());
            this.setJMSMessageID(message.getJMSMessageID());
            this.setJMSPriority(message.getJMSPriority());
            this.setJMSReplyTo(toDestination(message.getJMSReplyTo()));
            this.setJMSTimestamp(message.getJMSTimestamp());
            this.setJMSType(message.getJMSType());

            for (Enumeration<String> props = message.getPropertyNames(); props.hasMoreElements();) {
                String name = props.nextElement();
                Object value = message.getObjectProperty(name);
                this.setObjectProperty(name, value);
            }
        } catch (JMSException e) {
            throw new EJBException("Cant parse message " + e);
        }
    }

    /**
     * ErraiMessageBus Message to Jms Message
     * 
     * @param message
     */
    public MessageImpl(org.jboss.errai.bus.client.api.messaging.Message message) {
        try {
            this.setJMSType(message.get(byte.class, "type").toString());
            this.body = message.get(Object.class, "value");
            this.setJMSCorrelationID(message.get(String.class, "JMSCorrelationID"));
            // this.setJMSDestination(new Destination());
            Map<String, Object> map = message.getParts();
            for (Entry<String, Object> kv : map.entrySet()) {
                setObjectProperty(kv.getKey(), kv.getValue());
            }
        } catch (JMSException e) {
            throw new EJBException("Can't parse message " + e);
        }

    }

    @Override
    public void acknowledge() throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearBody() throws JMSException {
        readOnly = false;
    }

    @Override
    public void clearProperties() throws JMSException {
        properties.clear();
    }

    // TODO
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBody(Class<T> c) throws JMSException {
        if (isBodyAssignableTo(c)) {
            return (T) body;
        }
        throw new MessageFormatException("Body not assignable to " + c);
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException {
        return (Boolean) properties.get(name);
    }

    @Override
    public byte getByteProperty(String name) throws JMSException {
        return (Byte) properties.get(name);
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException {
        return (Double) properties.get(name);
    }

    public Boolean getDurable() {
        return durable;
    }

    @Override
    public float getFloatProperty(String name) throws JMSException {
        return (Float) properties.get(name);
    }

    @Override
    public int getIntProperty(String name) throws JMSException {
        return (Integer) properties.get(name);
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        return correlationID;
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        return deliveryMode;
    }

    @Override
    public long getJMSDeliveryTime() throws JMSException {
        return deliveryTime;
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        return destination;
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        return expiration;
    }

    @Override
    public String getJMSMessageID() throws JMSException {
        return jMSMessageID;
    }

    @Override
    public int getJMSPriority() throws JMSException {
        return priority;
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        return jMSRedelivered;
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        return replyToDestination;
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        return jMSTimestamp;
    }

    @Override
    public String getJMSType() throws JMSException {
        return this.type;
    }

    @Override
    public long getLongProperty(String name) throws JMSException {
        return (Long) properties.get(name);
    }

    @Override
    public Object getObjectProperty(String name) throws JMSException {
        return properties.get(name);
    }

    @Override
    public Enumeration<?> getPropertyNames() throws JMSException {
        return Collections.enumeration(properties.keySet());
    }

    public Set<String> getPropertyNamesAsSet() throws JMSException {
        return properties.keySet();
    }

    @Override
    public short getShortProperty(String name) throws JMSException {
        return (Short) properties.get(name);
    }

    @Override
    public String getStringProperty(String name) throws JMSException {
        return properties.get(name).toString();
    }

    @Override
    public boolean isBodyAssignableTo(Class c) throws JMSException {
        /**
         * From the specs:
         * <p>
         * If the message is a {@code Message} (but not one of its subtypes) then this method will return true irrespective of
         * the value of this parameter.
         */
        return true;
    }

    @Override
    public boolean propertyExists(String name) throws JMSException {
        return properties.containsKey(name);
    }

    @Override
    public void setBooleanProperty(String name, boolean value) throws JMSException {
        properties.put(name, value);
    }

    @Override
    public void setByteProperty(String name, byte value) throws JMSException {
        properties.put(name, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setDoubleProperty(String name, double value) throws JMSException {
        properties.put(name, value);
    }

    public void setDurable(Boolean durable) {
        this.durable = durable;
    }

    @Override
    public void setFloatProperty(String name, float value) throws JMSException {
        properties.put(name, value);
    }

    @Override
    public void setIntProperty(String name, int value) throws JMSException {
        properties.put(name, value);
    }

    @Override
    public void setJMSCorrelationID(String correlationID) throws JMSException {
        this.correlationID = correlationID;
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
        this.deliveryMode = deliveryMode;
        if (deliveryMode == DeliveryMode.PERSISTENT) {
            setDurable(true);
        } else if (deliveryMode == DeliveryMode.NON_PERSISTENT) {
            setDurable(false);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void setJMSDeliveryTime(long deliveryTime) throws JMSException {
        this.deliveryTime = deliveryTime;
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException {
        this.destination = destination;
    }

    @Override
    public void setJMSExpiration(long expiration) throws JMSException {
        this.expiration = expiration;
    }

    @Override
    public void setJMSMessageID(String id) throws JMSException {
        this.jMSMessageID = id;
    }

    @Override
    public void setJMSPriority(int priority) throws JMSException {
        this.priority = priority;
    }

    @Override
    public void setJMSRedelivered(boolean redelivered) throws JMSException {
        this.jMSRedelivered = redelivered;
    }

    @Override
    public void setJMSReplyTo(Destination replyTo) throws JMSException {

        this.replyToDestination = replyTo;
    }

    @Override
    public void setJMSTimestamp(long timestamp) throws JMSException {
        this.jMSTimestamp = timestamp;
    }

    @Override
    public void setJMSType(String type) throws JMSException {
        this.type = type;
    }

    @Override
    public void setLongProperty(String name, long value) throws JMSException {
        properties.put(name, value);
    }

    /**
     * @see javax.jms.Message#setObjectProperty(java.lang.String, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setObjectProperty(String name, Object value) throws JMSException {
        properties.put(name, value.toString());
    }

    @Override
    public void setShortProperty(String name, short value) throws JMSException {
        properties.put(name, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setStringProperty(String name, String value) throws JMSException {
        properties.put(name, value);
    }

    /**
     * Pick proper Destination implementation
     * 
     * @return Destination
     * @throws JMSException
     */
    private Destination toDestination(Destination destination) throws JMSException {
        Destination result = null;
        if (destination instanceof Topic) {
            result = new TopicImpl(((Topic) destination).getTopicName());
        } else if (destination instanceof Queue) {
            result = new QueueImpl(((Queue) destination).getQueueName());
        }
        return result;
    }
}
