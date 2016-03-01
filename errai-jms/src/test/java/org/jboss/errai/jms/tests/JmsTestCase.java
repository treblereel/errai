package org.jboss.errai.jms.tests;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.jboss.arquillian.graphene.Graphene.*;


public class JmsTestCase extends JmsTest {

    public static final String TEXT_MESSAGE_QUEUE    = "sendTextMessageQueue";
    public static final String TEXT_MESSAGE_TOPIC    = "sendTextMessageTopic";
    public static final String OBJECT_MESSAGE_QUEUE  = "sendObjectMessageQueue";
    public static final String BYTE_MESSAGE_QUEUE    = "sendByteMessageQueue";
    public static final String MAP_MESSAGE_QUEUE     = "sendMapMessageQueue";
    
    private static Logger logger = Logger.getLogger(JmsTestCase.class);

    
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
    
    @FindBy(id = "label")
    WebElement label;

    @Test
    @RunAsClient
    @InSequence(1)
    public void testSendTextMessageQueue() {
        open();
        waitModel().until().element(sendTextMessageQueueButton).is().present();
        sendTextMessageQueueButton.click();
        waitModel().until().element(label).is().visible();
        Assert.assertTrue("TextMessageQueue", label.getText().equals("This is an example"));
    }
    
    @Test
    @RunAsClient
    @InSequence(2)
    public void testSendTextMessageTopic() {
        open();
        Graphene.waitModel().until().element(sendTextMessageTopicButton).is().visible();
        sendTextMessageTopicButton.click();
        Graphene.waitModel().until().element(label).is().not().equals("");
        Assert.assertTrue("TextMessageTopic", label.getText().equals("The sky above the port was the color of television, tuned to a dead channel"));
    }
    
    @Test
    @RunAsClient
    @InSequence(3)
    public void testSendObjectMessageQueue() {
        open();
        Graphene.waitModel().until().element(sendObjectMessageQueueButton).is().visible();
        sendObjectMessageQueueButton.click();
        Graphene.waitModel().until().element(label).is().visible();
        Assert.assertTrue("ObjectMessageQueue", label.getText().equals("name :Bilbo Baggins alive :true"));

    }
    
    @Test
    @RunAsClient
    @InSequence(4)
    public void testSendByteMessageQueue() {
        open();
        Graphene.waitModel().until().element(sendByteMessageQueueButton).is().visible();
        sendByteMessageQueueButton.click();
        Graphene.waitModel().until().element(label).is().visible();
        Assert.assertTrue("ByteMessageQueue", label.getText().equals("841041051153210511532971103210112097109112108101"));
    }
    
    @Test
    @RunAsClient
    @InSequence(5)
    public void testSendMapMessageQueue() {
        open();
        Graphene.waitModel().until().element(sendMapMessageQueueButton).is().visible();
        sendMapMessageQueueButton.click();
        Graphene.waitModel().until().element(label).is().visible();
        logger.warn( label.getText());        
        Assert.assertTrue("MapMessageQueue", label.getText().equals("param : true: param : you shall not pass: param : Gandalf the Grey:"));
    }

}
