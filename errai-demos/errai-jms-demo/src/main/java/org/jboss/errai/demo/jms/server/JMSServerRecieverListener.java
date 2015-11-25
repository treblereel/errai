package org.jboss.errai.demo.jms.server;

import javax.annotation.PostConstruct;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class JMSServerRecieverListener {
  private static final Logger logger = LoggerFactory
          .getLogger(JMSServerRecieverListener.class);

  
  @PostConstruct
  public void init() {
    InitialContext ic;
    try {
      ic = new InitialContext();
      new QueueListener(ic);
      new QueueListener(ic);
      new QueueListener(ic);
      new QueueListener(ic);
      
      
    } catch (NamingException e) {
      e.printStackTrace();
    }
    logger.warn("on init");

  

  }

}
