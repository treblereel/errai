package org.jboss.errai.jms.client.local;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.MessageListener;

import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * Main purpose of this class is to get all MDB beans and startup them.
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@Startup
@ApplicationScoped
@MessageDrivenBeanDecorator
public class MessageDrivenBeanStarter {

  @Inject
  private SyncBeanManager manager;

  private List<Class<? extends MessageListener>> beans = new ArrayList<Class<? extends MessageListener>>();

  public void addMDBBean(Class<? extends MessageListener> clazz) {
    beans.add(clazz);
  }

  public void initMDBBeans() {
    for (Class<? extends MessageListener> clazz : beans) {
      manager.lookupBean(clazz).getInstance();
    }
  }

}
