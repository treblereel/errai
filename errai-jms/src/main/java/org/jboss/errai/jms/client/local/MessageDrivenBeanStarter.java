package org.jboss.errai.jms.client.local;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.MessageListener;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.slf4j.Logger;

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
      initMDBBean(clazz);
    }
  }

  private void initMDBBean(Class<? extends MessageListener> clazz) {
    manager.lookupBean(clazz).getInstance();
  }

}
