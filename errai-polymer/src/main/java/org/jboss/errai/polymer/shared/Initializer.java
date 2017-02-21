package org.jboss.errai.polymer.shared;

import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.enterprise.client.cdi.EventProvider;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleEvent;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListener;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by treblereel on 3/7/17.
 */
/*@Startup
@Singleton*/
public class Initializer {
    @Inject
    Logger logger;

    @Inject
    EventProvider eventProvider;

    @PostConstruct
    public void init(){
        IOC.registerLifecycleListener("", new LifecycleListener<String>() {
            @Override
            public void observeEvent(LifecycleEvent<String> lifecycleEvent) {

            }

            @Override
            public boolean isObserveableEventType(Class<? extends LifecycleEvent<String>> aClass) {
                return false;
            }
        });


    }

}
