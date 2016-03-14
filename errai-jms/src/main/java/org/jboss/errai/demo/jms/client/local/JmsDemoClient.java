package org.jboss.errai.demo.jms.client.local;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.demo.jms.client.shared.Commands;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.slf4j.Logger;
//import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Dmitrii Tikhomirov
 *
 */

@EntryPoint
public class JmsDemoClient extends Composite {

  @Inject
  private MessageBus bus;

  private VerticalPanel buttonVerticalPanel = new VerticalPanel();
  private VerticalPanel messageVerticalPanel = new VerticalPanel();
  private HorizontalPanel layoutVerticalPanel = new HorizontalPanel();
  private static Logger logger = LoggerFactory.getLogger(JmsDemoClient.class);

  public JmsDemoClient() {
    layoutVerticalPanel.add(buttonVerticalPanel);
    layoutVerticalPanel.add(messageVerticalPanel);

    RootPanel.get().add(layoutVerticalPanel);

    for (final String command : Commands.listOf) {
      Label l = new Label("");
      l.setVisible(false);
      l.getElement().setId(command + "Label");
      messageVerticalPanel.add(l);

      buttonVerticalPanel.add(new Button(command) {
        {
          getElement().setId(command);
          addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              MessageBuilder.createMessage().toSubject("sendMeMessage").signalling().withValue(command)
                  .noErrorHandling().sendNowWith(bus);
            }
          });
        }
      });
    }
  }

  public void showIncomeMessage(String message, String labelName) {
    logger.warn(message + " " + labelName);

    Label l = Label.wrap(DOM.getElementById(labelName + "Label"));
    l.setText(message);
    l.setVisible(true);
  }

}
