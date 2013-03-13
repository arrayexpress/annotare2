package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class IdfView extends Composite implements IsWidget {


    @UiField
    MenuItem contactsItem;

    @UiField
    MenuItem generalInfoItem;

    @UiField
    ScrollPanel content;

    private MenuItem selected;

    interface Binder extends UiBinder<DockLayoutPanel, IdfView> {
    }

    public IdfView() {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        contactsItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                content.setWidget(new ContactListView());
                setSelected(contactsItem);
            }
        });

        generalInfoItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                content.setWidget(new GeneralInfoView());
                setSelected(generalInfoItem);
            }
        });

        DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), generalInfoItem);
    }

    private void setSelected(MenuItem item) {
        if (selected != null) {
            selected.removeStyleName("selected");
        }
        item.addStyleName("selected");
        selected = item;
    }
}
