package org.jboss.errai.demo.jms.client.local;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.demo.jms.client.shared.Commands;
import org.jboss.errai.ioc.client.api.EntryPoint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

  public JmsDemoClient() {
    layoutVerticalPanel.add(buttonVerticalPanel);
    layoutVerticalPanel.add(messageVerticalPanel);
    RootPanel.get().add(layoutVerticalPanel);

    for (final String command : Commands.listOf) {
      buttonVerticalPanel.add(new Button(command) {
        {
          addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              MessageBuilder.createMessage().toSubject("sendMeMessage").signalling().withValue(command).noErrorHandling()
                      .sendNowWith(bus);
            }
          });
        }
      });
    }
  }

  public void showIncomeMessage(String message) {
    messageVerticalPanel.add(new Label(message));
  }

}
