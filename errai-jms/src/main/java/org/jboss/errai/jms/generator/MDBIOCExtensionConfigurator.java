package org.jboss.errai.jms.generator;

import javax.ejb.MessageDriven;
import org.jboss.errai.ioc.client.api.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;


/**
 * Used on generation stage, we want MDBeans to be singletons.
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@IOCExtension
public class MDBIOCExtensionConfigurator implements IOCExtensionConfigurator {

  @Override
  public void configure(IOCProcessingContext context,
          InjectionContext injectionContext) {
    injectionContext.mapElementType(WiringElementType.SingletonBean,
            MessageDriven.class);
  }

  @Override
  public void afterInitialization(IOCProcessingContext context,
          InjectionContext injectionContext) {
  }
}
