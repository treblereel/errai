package javax.jms;

public class MessageNotReadableException extends JMSException {
  public MessageNotReadableException(String reason, String errorCode) {
      super(reason, errorCode);
  }

  public MessageNotReadableException(String reason) {
      this(reason, null);
  }
}