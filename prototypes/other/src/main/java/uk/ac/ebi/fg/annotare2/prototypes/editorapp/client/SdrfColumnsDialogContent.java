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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SdrfColumnsDialogContent extends Composite implements IsWidget, HasCloseHandlers<SdrfColumnsDialogContent> {

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @UiField
    ListBox columnTypesBox;

    @UiField
    ListBox currentColumns;

    @UiField
    Button moveDownButton;

    @UiField
    Button moveUpButton;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    private final List<SdrfColumn> columns = new ArrayList<SdrfColumn>();

    interface Binder extends UiBinder<Widget, SdrfColumnsDialogContent> {
    }

    public SdrfColumnsDialogContent(List<SdrfColumn.Type> columnTypes) {
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

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addColumn();
            }
        });

        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeColumn();
            }
        });

        moveUpButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveColumnUp();
            }
        });

        moveDownButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveColumnDown();
            }
        });

        for(SdrfColumn.Type type : columnTypes) {
            columnTypesBox.addItem(type.getTitle());
        }
    }

    private void moveColumnDown() {
    }

    private void moveColumnUp() {
    }

    private void removeColumn() {
    }

    private void addColumn() {

    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<SdrfColumnsDialogContent> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    private void close() {
        CloseEvent.fire(this, this);
    }
}
