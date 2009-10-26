package org.jboss.errai.workspaces.client.layout;

import com.google.gwt.user.client.ui.*;

import java.util.Iterator;

public class WorkPanel extends Panel {
    VerticalPanel vPanel = new VerticalPanel();

    private Label titleLabel = new Label("New WorkPanel");
    private HorizontalPanel titleInternal = new HorizontalPanel();
    private FlowPanel mainPanel = new FlowPanel();

    private int h;
    private int w;

    public WorkPanel() {
        setElement(vPanel.getElement());

        vPanel.setWidth("100%");

        SimplePanel title = new SimplePanel();
        vPanel.add(title);
        vPanel.add(mainPanel);

        title.setHeight("25px");
        vPanel.setCellHeight(title, "25px");

        titleLabel.setStyleName("WS-WorkPanel-title-label");
        title.setStyleName("WS-WorkPanel-title");
        vPanel.setStyleName("WS-WorkPanel-area");

        titleInternal.add(titleLabel);
        title.setWidget(titleInternal);

        getElement().getStyle().setProperty("overflow", "scroll");
    }

    @Override
    public void setPixelSize(int width, int height) {
        h = (height - titleInternal.getOffsetHeight());
        w = width;

        vPanel.setCellHeight(mainPanel, h + "px");
        vPanel.setCellWidth(mainPanel, width + "px");

        vPanel.setPixelSize(width, height);
        super.setPixelSize(width, height);
    }

    public void add(Widget w) {
        mainPanel.add(w);
    }

    public Iterator<Widget> iterator() {
        return mainPanel.iterator();
    }

    @Override
    public boolean remove(Widget child) {
        return mainPanel.remove(child);
    }

    public void addToTitlebar(Widget w) {
        titleInternal.add(w);
        titleInternal.setCellHorizontalAlignment(w, HasHorizontalAlignment.ALIGN_LEFT);
    }

    public int getPanelWidth() {
        return w == 0 ? getOffsetWidth() : w;
    }

    public int getPanelHeight() {
        return h == 0 ? getOffsetHeight() - titleInternal.getOffsetHeight() : h;
    }

    @Override
    public void setTitle(String s) {
        titleLabel.setText(s);
    }
}
