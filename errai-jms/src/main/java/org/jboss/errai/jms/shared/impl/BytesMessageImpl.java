package org.jboss.errai.jms.shared.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.EJBException;
import javax.jms.BytesMessage;
import javax.jms.JMSException;

import org.jboss.errai.bus.client.api.messaging.Message;
/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */
public class BytesMessageImpl extends MessageImpl implements BytesMessage {
  private static final long serialVersionUID = 1L;
  public static final byte TYPE = org.jboss.errai.jms.shared.impl.Type.BYTES_TYPE;
  private DataOutputStream dataOut;
  private ByteArrayOutputStream bytesOut;
  private DataInputStream dataIn;
  private int length;

  public BytesMessageImpl(Message message) {
    super(message);
    ByteSequence data;
    if (message.get(byte[].class, "value") == null) {
      data = new ByteSequence(new byte[] {}, 0, 0);
    }
    else {
      byte[] bytes = fromString(message.get(String.class, "value"));
      data = new ByteSequence(bytes);
    }
    InputStream is = new ByteArrayInputStream(data.getData(), data.getOffset(),
            data.getLength());
    length = data.getLength();
    dataIn = new DataInputStream(is);

  }

  public BytesMessageImpl(BytesMessage message) {
    super(message);
    try {
      byte[] messageData = message.getBody(byte[].class);
      ByteSequence data;
      if (messageData == null) {
        data = new ByteSequence(new byte[] {}, 0, 0);
      }
      else {
        data = new ByteSequence(messageData);
      }
      InputStream is = new ByteArrayInputStream(data.getData(),
              data.getOffset(), data.getLength());
      length = data.getLength();
      dataIn = new DataInputStream(is);
    } catch (JMSException e) {
      throw new EJBException("Can't parse message " + e);
    }
  }

  @Override
  public long getBodyLength() throws JMSException {
    return length;
  }

  @Override
  public boolean readBoolean() throws JMSException {
    try {
      return this.dataIn.readBoolean();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public byte readByte() throws JMSException {
    try {
      return this.dataIn.readByte();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public int readUnsignedByte() throws JMSException {
    try {
      return this.dataIn.readUnsignedByte();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public short readShort() throws JMSException {
    try {
      return this.dataIn.readShort();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public int readUnsignedShort() throws JMSException {
    try {
      return this.dataIn.readUnsignedShort();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public char readChar() throws JMSException {
    try {
      return this.dataIn.readChar();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public int readInt() throws JMSException {
    try {
      return this.dataIn.readInt();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public long readLong() throws JMSException {
    try {
      return this.dataIn.readLong();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public float readFloat() throws JMSException {
    try {
      return this.dataIn.readFloat();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public double readDouble() throws JMSException {
    try {
      return this.dataIn.readDouble();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public String readUTF() throws JMSException {
    try {
      return this.dataIn.readUTF();
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public int readBytes(byte[] value) throws JMSException {
    return readBytes(value, value.length);
  }

  @Override
  public int readBytes(byte[] value, int length) throws JMSException {
    try {
      int n = 0;
      while (n < length) {
        int count = this.dataIn.read(value, n, length - n);
        if (count < 0) {
          break;
        }
        n += count;
      }
      if (n == 0 && length > 0) {
        n = -1;
      }
      return n;
    } catch (EOFException e) {
      throw new JMSException(e.getMessage());
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeBoolean(boolean value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeBoolean(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeByte(byte value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeByte(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeShort(short value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeShort(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeChar(char value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeChar(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeInt(int value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeInt(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeLong(long value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeLong(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeFloat(float value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeFloat(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeDouble(double value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeDouble(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeUTF(String value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.writeUTF(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeBytes(byte[] value) throws JMSException {
    initializeWriting();
    try {
      this.dataOut.write(value);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeBytes(byte[] value, int offset, int length)
          throws JMSException {
    initializeWriting();
    try {
      this.dataOut.write(value, offset, length);
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  @Override
  public void writeObject(Object value) throws JMSException {
    if (value == null) {
      throw new NullPointerException();
    }
    initializeWriting();
    if (value instanceof Boolean) {
      writeBoolean(((Boolean) value).booleanValue());
    }
    else if (value instanceof Character) {
      writeChar(((Character) value).charValue());
    }
    else if (value instanceof Byte) {
      writeByte(((Byte) value).byteValue());
    }
    else if (value instanceof Short) {
      writeShort(((Short) value).shortValue());
    }
    else if (value instanceof Integer) {
      writeInt(((Integer) value).intValue());
    }
    else if (value instanceof Long) {
      writeLong(((Long) value).longValue());
    }
    else if (value instanceof Float) {
      writeFloat(((Float) value).floatValue());
    }
    else if (value instanceof Double) {
      writeDouble(((Double) value).doubleValue());
    }
    else if (value instanceof String) {
      writeUTF(value.toString());
    }
    else if (value instanceof byte[]) {
      writeBytes((byte[]) value);
    }
    else {
      throw new JMSException(
              "Cannot write non-primitive type:" + value.getClass());
    }
  }

  @Override
  public void reset() throws JMSException {
    try {
      if (bytesOut != null) {
        bytesOut.close();
        bytesOut = null;
      }
      if (dataIn != null) {
        dataIn.close();
        dataIn = null;
      }
      if (dataOut != null) {
        // dataOut.close();
        dataOut = null;
      }
    } catch (IOException e) {
      throw new JMSException(e.getMessage());
    }
  }

  private void initializeWriting() throws JMSException {
    if (this.dataOut == null) {
      this.bytesOut = new ByteArrayOutputStream();
      OutputStream os = bytesOut;
      this.dataOut = new DataOutputStream(os);
    }
  }

  public static byte[] toBytes(char[] inputChars) {
    byte[] out = new byte[inputChars.length];
    int[] delta = new int[inputChars.length];
    for (int i = 0; i < inputChars.length; i++) {
      out[i] = (byte) inputChars[i];
      delta[i] = (int) inputChars[i] - out[i];
    }
    return out;
  }

  public static byte[] fromString(String in) {
    return toBytes(in.toCharArray());
  }

}
