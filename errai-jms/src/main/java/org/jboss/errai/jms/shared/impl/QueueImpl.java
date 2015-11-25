package org.jboss.errai.jms.shared.impl;

import javax.jms.Queue;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class QueueImpl implements Queue {
  private String queueName;

  public QueueImpl() {

  }

  public QueueImpl(String queueName) {
    setQueueName(queueName);
  }

  @Override
  public String getQueueName() {
    return queueName;
  }

  private void setQueueName(String queueName) {
    this.queueName = queueName;
  }
}
