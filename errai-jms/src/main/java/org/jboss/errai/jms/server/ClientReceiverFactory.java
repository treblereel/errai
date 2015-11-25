package org.jboss.errai.jms.server;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Destination;
import javax.jms.Topic;

@Stateless
public class ClientReceiverFactory {

  @Inject
  @Named("Topic")
  private ClientReceiver clientReceiverTopic;
  @Inject
  @Named("Queue")
  private ClientReceiver clientReceiverQueue;

  public ClientReceiver getClientReceiver(Destination destination) {
    if (destination instanceof Topic) {
      return clientReceiverTopic;
    }
    else {
      return clientReceiverQueue;
    }
  }
}
