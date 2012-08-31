package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class DisclosurePanel extends Composite {

    interface Binder extends UiBinder<Widget, DisclosurePanel> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    SimplePanel header;

    @UiField
    SimplePanel content;

    private boolean closed = false;

    public DisclosurePanel() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        header.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                toggle();
            }
        }, ClickEvent.getType());

        close();
    }

    public void setHeader(Widget w) {
        header.setWidget(w);
    }

    public void setContent(Widget w) {
        content.setWidget(w);
    }

    private void toggle() {
        if (closed) {
            open();
        } else {
            close();
        }
    }

    public void open() {
        content.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        closed = false;
    }

    public void close() {
        content.getElement().getStyle().setDisplay(Style.Display.NONE);
        closed = true;
    }
}
