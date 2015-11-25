package org.jboss.errai.jms.shared.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJBException;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.jboss.errai.bus.client.api.messaging.Message;

public class MapMessageImpl extends MessageImpl implements MapMessage {
  private static final long serialVersionUID = 1L;
  public static final byte TYPE = org.jboss.errai.jms.shared.impl.Type.MAP_TYPE;
  private Map<String, Object> map = new HashMap<String, Object>();

  
  
  public MapMessageImpl(MapMessage mapMessage) {
    super(mapMessage);
    try {
      Enumeration<String> names = mapMessage.getMapNames();
      while (names.hasMoreElements()) {
        String param = (String) names.nextElement();
        map.put(param, mapMessage.getObject(param));
      }
    } catch (JMSException e) {
      throw new EJBException("Can't parse message " + e);
    }
  }

  
  @SuppressWarnings("unchecked")
  public MapMessageImpl(Message message) {
    super(message);
    Map<String, Object> values = message.get(Map.class, "value");
    for (Entry<String, Object> kv : values.entrySet()) {
      map.put(kv.getKey(), kv.getValue());
    }
  }

  @Override
  public boolean getBoolean(String name) throws JMSException {
    return (Boolean) map.get(name);
  }

  @Override
  public byte getByte(String name) throws JMSException {
    return (Byte) map.get(name);
  }

  @Override
  public short getShort(String name) throws JMSException {
    return (Short) map.get(name);

  }

  @Override
  public char getChar(String name) throws JMSException {
    return (Character) map.get(name);
  }

  @Override
  public int getInt(String name) throws JMSException {
    return (Integer) map.get(name);
  }

  @Override
  public long getLong(String name) throws JMSException {
    return (Long) map.get(name);
  }

  @Override
  public float getFloat(String name) throws JMSException {
    return (Float) map.get(name);
  }

  @Override
  public double getDouble(String name) throws JMSException {
    return (Double) map.get(name);
  }

  @Override
  public String getString(String name) throws JMSException {
    return (String) map.get(name);
  }

  @Override
  public byte[] getBytes(String name) throws JMSException {
    return map.get(name).toString().getBytes();
  }

  @Override
  public Object getObject(String name) throws JMSException {
    return map.get(name);
  }

  @Override
  public Enumeration<String> getMapNames() throws JMSException {
   return Collections.enumeration(map.keySet());
  }

  @Override
  public void setBoolean(String name, boolean value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setByte(String name, byte value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setShort(String name, short value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setChar(String name, char value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setInt(String name, int value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setLong(String name, long value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setFloat(String name, float value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setDouble(String name, double value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setString(String name, String value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public void setBytes(String name, byte[] value) throws JMSException {
    map.put(name, value);
  }
  
  //TODO
  @Override
  public void setBytes(String name, byte[] value, int offset, int length)
          throws JMSException {
    if (offset + length > value.length)
    {
       throw new JMSException("Invalid offset/length");
    }
    byte[] newBytes = new byte[length];
    System.arraycopy(value, offset, newBytes, 0, length);
    map.put(name, newBytes);
  }

  @Override
  public void setObject(String name, Object value) throws JMSException {
    map.put(name, value);
  }

  @Override
  public boolean itemExists(String name) throws JMSException {
    return map.containsKey(name);
  }

}
