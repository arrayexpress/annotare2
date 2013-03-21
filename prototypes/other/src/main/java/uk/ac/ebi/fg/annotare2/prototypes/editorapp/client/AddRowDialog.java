package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AddRowDialog extends DialogBox implements HasSelectionHandlers<List<String>> {

    @UiField(provided = true)
    ListBox listBox;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField
    HTML label;

    interface Binder extends UiBinder<Widget, AddRowDialog> {
    }

    public AddRowDialog(SdrfSection sourceSec, SdrfSection targetSec) {
        setText("Add Row");
        setModal(true);
        setGlassEnabled(true);

        listBox = new ListBox(true);
        for (String value : sourceSec.getRowNames()) {
            listBox.addItem(value);
        }
        Binder uiBinder = GWT.create(Binder.class);
        setWidget(uiBinder.createAndBindUi(this));
        center();

        label.setHTML("Select <b>" + single(sourceSec.getTitle()) +
                "(s)</b> to create a new <b>" + single(targetSec.getTitle()) + "</b> from:");
    }

    private static String single(String str) {
        return str.substring(0, str.length() - 1);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<String>> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @UiHandler("okButton")
    public void okClick(ClickEvent event) {
        List<String> selection = new ArrayList<String>();
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (listBox.isItemSelected(i)) {
                selection.add(listBox.getValue(i));
            }
        }
        hide();
        SelectionEvent.fire(this, selection);
    }

    @UiHandler("cancelButton")
    public void cancelClick(ClickEvent event) {
        hide();
    }
}
