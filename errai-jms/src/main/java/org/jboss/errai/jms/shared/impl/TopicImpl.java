package org.jboss.errai.jms.shared.impl;

import javax.jms.Topic;

public class TopicImpl implements Topic {
  private String topicName;

  public TopicImpl() {

  }

  public TopicImpl(String topicName) {
    setTopicName(topicName);
  }

  @Override
  public String getTopicName() {
    return topicName;
  }

  private void setTopicName(String topicName) {
    this.topicName = topicName;
  }
}
