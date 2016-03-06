package org.jboss.errai.demo.jms.server;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;

@Singleton
@Startup
public class CommandService {

  @Inject
  private JmsService jmsService;

  @Inject
  private MessageBus bus;

  @PostConstruct
  private void init() {
    bus.subscribe("sendMeMessage", new MessageCallback() {
      public void callback(Message message) {
          String messageType = message.getValue(String.class);
          jmsService.send(messageType);
      }
    });
  }
}
