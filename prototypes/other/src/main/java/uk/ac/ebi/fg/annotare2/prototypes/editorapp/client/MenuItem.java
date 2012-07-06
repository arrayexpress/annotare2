package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class MenuItem extends Composite implements HasText, HasClickHandlers {
    interface Binder extends UiBinder<Widget, MenuItem> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Label title;

    @UiConstructor
    public MenuItem() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    public String getText() {
        return title.getText();
    }

    public void setText(String text) {
        title.setText(text);
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

}
