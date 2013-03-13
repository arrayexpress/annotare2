package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfColumnsDialogContent extends Composite implements IsWidget, HasCloseHandlers<SdrfColumnsDialogContent> {

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @UiField
    ListBox columnTypes;

    @UiField
    ListBox currentColumns;

    @UiField
    Button moveColumnDown;

    @UiField
    Button moveColumnUp;

    interface Binder extends UiBinder<Widget, SdrfColumnsDialogContent> {
    }

    public SdrfColumnsDialogContent() {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
            }
        });

        for(SdrfColumn.Type type : SdrfColumn.Type.values()) {
            columnTypes.addItem(type.getTitle());
        }
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<SdrfColumnsDialogContent> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    private void close() {
        CloseEvent.fire(this, this);
    }
}
