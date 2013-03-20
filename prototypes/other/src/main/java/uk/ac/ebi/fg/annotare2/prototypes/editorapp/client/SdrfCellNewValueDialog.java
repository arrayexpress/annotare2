package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data.SdrfValue;

/**
 * @author Olga Melnichuk
 */
public class SdrfCellNewValueDialog extends DialogBox {

    public interface Presenter {
        boolean save(SdrfValue value);
    }

    @UiField
    SimplePanel content;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private SdrfCellValueEditor editor;
    private Presenter presenter;

    interface Binder extends UiBinder<Widget, SdrfCellNewValueDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public SdrfCellNewValueDialog(SdrfSection section, SdrfColumn column, String name) {
        setText("New " + column.getTitle() + " Value");
        setModal(true);

        setWidget(Binder.BINDER.createAndBindUi(this));
        editor = column.createEditor(name, section);
        content.setWidget(editor);

        center();
    }

    @UiHandler("cancelButton")
    void cancelClick(ClickEvent event) {
        hide();
        cancel();
    }

    @UiHandler("okButton")
    void okClick(ClickEvent event) {
        SdrfValue value = editor.getValue();
        if (presenter != null) {
            presenter.save(value);
        }
        hide();
        ok(value.getName());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    protected void ok(String result) {
        // override me
    }

    protected void cancel() {
        // override me
    }

}
