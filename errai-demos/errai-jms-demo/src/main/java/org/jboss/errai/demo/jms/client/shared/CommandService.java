package org.jboss.errai.demo.jms.client.shared;

import org.jboss.errai.bus.server.annotations.Remote;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@Remote
public interface CommandService {
  void sendMessage(String messageType) throws IllegalArgumentException;
}
