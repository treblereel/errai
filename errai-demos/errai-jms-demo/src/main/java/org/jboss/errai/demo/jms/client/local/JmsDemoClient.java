package org.jboss.errai.demo.jms.client.local;

import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.slf4j.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

@EntryPoint
public class JmsDemoClient extends Composite {
  @Inject
  private Logger logger;
  
  public void showIncomeMessage(String message){
    
    RootPanel.get().add(new Label(message));
  }

}
