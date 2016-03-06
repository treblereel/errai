package org.jboss.errai.jms.shared.impl;

import javax.ejb.EJBException;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.jboss.errai.bus.client.api.messaging.Message;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */
public class TextMessageImpl extends MessageImpl implements TextMessage {
    private static final long serialVersionUID = 1L;
    public static final byte TYPE = org.jboss.errai.jms.shared.impl.Type.TEXT_TYPE;
    private String text;

    public TextMessageImpl() {
        super();
    }

    /**
     * ErraiMessageBus Message to Jms Text Message
     * 
     * 
     * @param org .jboss.errai.bus.client.api.messaging.message
     */
    public TextMessageImpl(Message message) {
        super(message);
        try {
            setText(message.get(String.class, "value"));
        } catch (JMSException e) {
            throw new EJBException("Cant parse message " + e);
        }
    }

    public TextMessageImpl(TextMessage message) {
        super(message);
        try {
            setText(message.getText());
        } catch (JMSException e) {
            throw new EJBException("Cant parse message " + e);
        }
    }

    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        text = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBody(Class<T> c) throws JMSException {
        return (T) getText();
    }

    @Override
    public String getText() throws JMSException {
        if (text != null) {
            return text;
        } else {
            return null;
        }
    }

    @Override
    public void setText(String string) throws JMSException {
        this.text = string;
    }
}
