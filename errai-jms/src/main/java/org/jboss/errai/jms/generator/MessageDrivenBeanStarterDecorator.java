package org.jboss.errai.jms.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.MessageDriven;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.jms.client.local.MessageDrivenBeanDecorator;

import com.google.gwt.core.ext.GeneratorContext;

/**
 * This decorator will add startup functionality to MessageDrivenBeanStarter 
 * which will bootup all client side MDBeans.
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@CodeDecorator
public class MessageDrivenBeanStarterDecorator extends
        IOCDecoratorExtension<MessageDrivenBeanDecorator> {
  public MessageDrivenBeanStarterDecorator(
          Class<MessageDrivenBeanDecorator> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(Decorable decorable,
          FactoryController controller) {
    GeneratorContext generatorContext = decorable.getInjectionContext()
            .getProcessingContext().getGeneratorContext();
    final Collection<MetaClass> allBindableTypes = getAllBindableTypes(generatorContext);

    BlockBuilder<AnonymousClassStructureBuilder> blockBuilder = Stmt
            .newObject(Runnable.class).extend().publicOverridesMethod("run");

    for (final MetaClass modelBean : allBindableTypes) {
      blockBuilder.append(Stmt.loadVariable(Refs.get("instance")).invoke("addMDBBean",
              modelBean.asClass()));
    }
    blockBuilder.append(Stmt.loadVariable(Refs.get("instance")).invoke("initMDBBeans"));
    final Statement callbackStmt = blockBuilder.finish().finish();

    controller.addInitializationStatements(Collections
            .<Statement> singletonList(Stmt.invokeStatic(InitVotes.class,
                    "registerOneTimeInitCallback", callbackStmt)));
  }

  public static Set<MetaClass> getAllBindableTypes(
          final GeneratorContext context) {
    Collection<MetaClass> annotatedBindableTypes = ClassScanner
            .getTypesAnnotatedWith(MessageDriven.class,
                    RebindUtils.findTranslatablePackages(context), context);

    Set<MetaClass> bindableTypes = new HashSet<MetaClass>(
            annotatedBindableTypes);
    return bindableTypes;
  }
}
