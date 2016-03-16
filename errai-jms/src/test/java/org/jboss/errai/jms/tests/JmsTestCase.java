package org.jboss.errai.jms.tests;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.concurrent.TimeUnit;

public class JmsTestCase extends JmsTest {

  public static final String TEXT_MESSAGE_QUEUE = "sendTextMessageQueue";
  public static final String TEXT_MESSAGE_TOPIC = "sendTextMessageTopic";
  public static final String OBJECT_MESSAGE_QUEUE = "sendObjectMessageQueue";
  public static final String BYTE_MESSAGE_QUEUE = "sendByteMessageQueue";
  public static final String MAP_MESSAGE_QUEUE = "sendMapMessageQueue";

  @FindBy(id = "sendTextMessageQueue")
  WebElement sendTextMessageQueueButton;

  @FindBy(id = "sendTextMessageTopic")
  WebElement sendTextMessageTopicButton;

  @FindBy(id = "sendObjectMessageQueue")
  WebElement sendObjectMessageQueueButton;

  @FindBy(id = "sendByteMessageQueue")
  WebElement sendByteMessageQueueButton;

  @FindBy(id = "sendMapMessageQueue")
  WebElement sendMapMessageQueueButton;

  @FindBy(id = "sendTextMessageQueueLabel")
  WebElement sendTextMessageQueueLabel;

  @FindBy(id = "sendTextMessageTopicLabel")
  WebElement sendTextMessageTopicLabel;

  @FindBy(id = "sendObjectMessageQueueLabel")
  WebElement sendObjectMessageQueueLabel;

  @FindBy(id = "sendByteMessageQueueLabel")
  WebElement sendByteMessageQueueLabel;

  @FindBy(id = "sendMapMessageQueueLabel")
  WebElement sendMapMessageQueueLabel;

  @Test
  @RunAsClient
  public void run() {
    open();

    testSendTextMessageQueue();
    testSendTextMessageTopic();
    testSendObjectMessageQueue();
    testSendByteMessageQueue();
    testSendMapMessageQueue();

  }

  public void testSendTextMessageQueue() {
    Graphene.waitGui().withTimeout(10, TimeUnit.SECONDS).until()
        .element(sendTextMessageQueueButton).is().present();

    sendTextMessageQueueButton.click();
    Graphene.waitGui().withTimeout(50, TimeUnit.SECONDS).until()
        .element(sendTextMessageQueueLabel).is().visible();
    Assert.assertEquals(sendTextMessageQueueLabel.getText(),
        "This is an example");
  }

  public void testSendTextMessageTopic() {
//    try {
//      Thread.sleep(500);
//    } catch (InterruptedException exception) {
//      Assert.fail();
//    }

    sendTextMessageTopicButton.click();
    Graphene.waitGui().withTimeout(50, TimeUnit.SECONDS).until()
        .element(sendTextMessageTopicLabel).is().visible();
    Assert
        .assertEquals(sendTextMessageTopicLabel.getText(),
            "The sky above the port was the color of television, tuned to a dead channel");
  }

  public void testSendObjectMessageQueue() {
//    try {
//      Thread.sleep(500);
//    } catch (InterruptedException exception) {
//      Assert.fail();
//    }

    sendObjectMessageQueueButton.click();
    Graphene.waitGui().withTimeout(50, TimeUnit.SECONDS).until()
        .element(sendObjectMessageQueueLabel).is().visible();
    Assert.assertEquals(sendObjectMessageQueueLabel.getText(),
        "name :Bilbo Baggins alive :true");

  }

  public void testSendByteMessageQueue() {
//    try {
//      Thread.sleep(500);
//    } catch (InterruptedException exception) {
//      Assert.fail();
//    }

    sendByteMessageQueueButton.click();
    Graphene.waitGui().withTimeout(5, TimeUnit.SECONDS).until()
        .element(sendByteMessageQueueLabel).is().visible();
    Assert.assertEquals(sendByteMessageQueueLabel.getText(),
        "841041051153210511532971103210112097109112108101");
  }

  public void testSendMapMessageQueue() {
//    try {
//      Thread.sleep(500);
//    } catch (InterruptedException exception) {
//      Assert.fail();
//    }

    sendMapMessageQueueButton.click();
    Graphene.waitGui().withTimeout(5, TimeUnit.SECONDS).until()
        .element(sendMapMessageQueueLabel).is().visible();
    Assert.assertEquals(sendMapMessageQueueLabel.getText(),
        "param : true: param : you shall not pass: param : Gandalf the Grey:");
  }

}
