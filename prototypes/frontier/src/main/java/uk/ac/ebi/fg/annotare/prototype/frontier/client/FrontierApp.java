package uk.ac.ebi.fg.annotare.prototype.frontier.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class FrontierApp implements EntryPoint {

    Label helloLabel;

    public FrontierApp() {

    }

    public void onModuleLoad() {
        helloLabel = new Label();
        helloLabel.setText("Hello, world!");
        helloLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NotificationPopupPanel.warning("Hello again!", false);
            }
        });

        RootPanel content = RootPanel.get("content");
        content.add(helloLabel);

	}
}

