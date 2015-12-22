package org.jboss.errai.jms.shared.impl;

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.jboss.errai.bus.client.api.messaging.Message;
/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */
public class ObjectMessageImpl extends MessageImpl implements ObjectMessage {

  private static final long serialVersionUID = 1L;
  public static final byte TYPE = org.jboss.errai.jms.shared.impl.Type.OBJECT_TYPE;
  private Serializable object;

  public ObjectMessageImpl(Message message) {
    super(message);
    try {
      setObject((Serializable)message.get(Object.class, "value"));
    } catch (JMSException e) {
      throw new EJBException("Can't parse message " + e);
    }
  }
  
  public ObjectMessageImpl(ObjectMessage message) {
    super(message);
    try {
      setObject(message.getObject());
    } catch (JMSException e) {
      throw new EJBException("Can't parse message " + e);
    }
  }
  
  @Override
  public void setObject(Serializable object) throws JMSException {
    this.object = object;
    
  }

  @Override
  public Serializable getObject() throws JMSException {
    return this.object;
  }

}
