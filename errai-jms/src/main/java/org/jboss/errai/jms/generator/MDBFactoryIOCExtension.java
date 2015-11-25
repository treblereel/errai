package org.jboss.errai.jms.generator;

import javax.ejb.MessageDriven;
import org.jboss.errai.ioc.client.api.IOCExtension;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.IOCProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCExtensionConfigurator;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ioc.rebind.ioc.injector.api.WiringElementType;

@IOCExtension
public class MDBFactoryIOCExtension implements IOCExtensionConfigurator {

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
