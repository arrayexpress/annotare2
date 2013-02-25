package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ImportEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ImportFileDialog;

/**
 * @author Olga Melnichuk
 */
public class AdfTabToolBarViewImpl extends Composite implements AdfTabToolBarView {

    interface Binder extends UiBinder<HTMLPanel, AdfTabToolBarViewImpl> {
    }

    @UiField
    Button importButton;

    @UiField
    Button exportButton;

    private Presenter presenter;
    private ImportFileDialog importFileDialog;

    public AdfTabToolBarViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        importButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                importFileDialog = new ImportFileDialog("Import Array Design Data...");
                importFileDialog.addImportEventHandler(new ImportEventHandler() {
                    @Override
                    public void onImport(AsyncCallback<Void> callback) {
                        presenter.importFile(callback);
                    }
                });
                importFileDialog.show();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void hideImportButtons(boolean hidden) {
        importButton.setVisible(!hidden);
        exportButton.setVisible(!hidden);
    }
}
