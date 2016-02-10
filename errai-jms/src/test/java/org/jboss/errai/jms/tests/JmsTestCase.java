package org.jboss.errai.jms.tests;

import org.eclipse.jdt.core.dom.ThisExpression;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
        Graphene.waitModel().until().element(sendTextMessageQueueButton).is().visible();
        Graphene.guardAjax(sendTextMessageQueueButton).click();
        Graphene.waitModel().until().element(label).is().visible();
        
        logger.warn( label.getText());        

        
        Assert.assertTrue("TextMessageQueue", label.getText().equals("This is an example"));

    }
    
    @Test
    @RunAsClient
    @InSequence(2)
    public void testSendTextMessageTopic() {
        open();
        Graphene.waitModel().until().element(sendTextMessageTopicButton).is().visible();
        Graphene.guardAjax(sendTextMessageTopicButton).click();
        Graphene.waitModel().until().element(label).is().visible();
        
        Graphene.waitModel().until().element(label).is().not().equals("");
        
        logger.warn( label.getText());        
        Assert.assertTrue("TextMessageTopic", label.getText().equals("Topic Fri Feb 26 18:04:05 CET 2016"));
    }
    
    @Test
    @RunAsClient
    @InSequence(3)
    public void testSendObjectMessageQueue() {
        open();
        Graphene.waitModel().until().element(sendObjectMessageQueueButton).is().visible();
        Graphene.guardAjax(sendObjectMessageQueueButton).click();
        Graphene.waitModel().until().element(label).is().visible();
        
        logger.warn( label.getText());        

        
        Assert.assertTrue("ObjectMessageQueue", label.getText().equals("name :Bilbo Baggins age : Fri Feb 26 18:08:28 GMT+100 2016 alive :true"));
    }
    
    @Test
    @RunAsClient
    @InSequence(4)
    public void testSendByteMessageQueue() {
        open();
        Graphene.waitModel().until().element(sendByteMessageQueueButton).is().visible();
        Graphene.guardAjax(sendByteMessageQueueButton).click();
        Graphene.waitModel().until().element(label).is().visible();
        
        logger.warn( label.getText());        

        
        Assert.assertTrue("ByteMessageQueue", label.getText().equals("841041051153210511532971103210112097109112108101"));
    }
    
    @Test
    @RunAsClient
    @InSequence(5)
    public void testSendMapMessageQueue() {
        open();
        Graphene.waitModel().until().element(sendMapMessageQueueButton).is().visible();
        Graphene.guardAjax(sendMapMessageQueueButton).click();
        Graphene.waitModel().until().element(label).is().visible();
        
        logger.warn( label.getText());        

        
        Assert.assertTrue("MapMessageQueue", label.getText().equals("param : true: param : you shall not pass: param : Gandalf the Grey:"));
    }

}
