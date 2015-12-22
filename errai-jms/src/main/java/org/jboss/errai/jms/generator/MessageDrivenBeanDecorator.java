package org.jboss.errai.jms.generator;

import java.util.Collections;
import javax.ejb.MessageDriven;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.jms.client.local.MessageDrivenBeanReceiver;

/**
 * Main generator, response for client and server bean generation.
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@CodeDecorator
public class MessageDrivenBeanDecorator extends
        IOCDecoratorExtension<MessageDriven> {

  public MessageDrivenBeanDecorator(Class<MessageDriven> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(Decorable decorable,
          FactoryController controller) {
    if (decorable.getDecorableDeclaringType().isAnnotationPresent(
            MessageDriven.class)) {
      MessageDrivenBeanConfiguration config = new MessageDrivenBeanConfiguration(
              decorable);
      generate(decorable, controller, config);
    }
  }

  /**
   *  Construct server and client MDBeans
   * 
   * @param decorable
   * @param controller
   * @param config
   */
  private void generate(Decorable decorable, FactoryController controller,
          MessageDrivenBeanConfiguration config) {
    try {
      generateClientSideCode(decorable, controller, config);
      generateServerSideCode(decorable, config);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  
  /**
   * Creates and configure an instance of MessageDrivenBeanReceiver to became a bridge
   * between server MDBean and client MDBean.
   * 
   * @param decorable
   * @param controller
   * @param config current MDBean configuration
   */
  private void generateClientSideCode(Decorable decorable,
          FactoryController controller, MessageDrivenBeanConfiguration config) {

    final Statement callbackStmt = Stmt
            .newObject(Runnable.class)
            .extend()
            .publicOverridesMethod("run")
            .append(Stmt.declareFinalVariable(
                    "temp",
                    MessageDrivenBeanReceiver.class,
                    Stmt.create(Context.create().autoImport()).nestedCall(
                            Stmt.newObject(MessageDrivenBeanReceiver.class))))
            .append(Stmt.loadVariable("temp").invoke("setDestinationLookup",
                    config.getDestinationLookup()))
            .append(Stmt.loadVariable("temp").invoke("setDestinationType",
                    config.getDestinationType()))
            .append(Stmt.loadVariable("temp").invoke("setAcknowledgeMode",
                    config.getAcknowledgeMode()))
            .append(Stmt.loadVariable("temp").invoke("setName",
                    config.getMappedName()))
            .append(Stmt.loadVariable("temp").invoke("setMessageListener",
                    Stmt.loadVariable(Refs.get("instance"))))
            .append(Stmt.loadVariable("temp").invoke("finish")).finish()
            .finish();

    controller.addInitializationStatements(Collections
            .<Statement> singletonList(Stmt.invokeStatic(InitVotes.class,
                    "registerOneTimeInitCallback", callbackStmt)));
  }

  /* 
   * Generate server side MDBean
   */
  private void generateServerSideCode(Decorable decorable,
          MessageDrivenBeanConfiguration config) {
    new MessageDrivenBeanReceiverGenerator(decorable, config);
  }
}
