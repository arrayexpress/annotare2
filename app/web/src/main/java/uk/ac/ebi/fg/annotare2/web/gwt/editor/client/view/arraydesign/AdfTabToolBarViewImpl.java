package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.AsyncEventFinishListener;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ImportFileDialog;

/**
 * @author Olga Melnichuk
 */
public class AdfTabToolBarViewImpl extends Composite implements AdfTabToolBarView {

    interface Binder extends UiBinder<HTMLPanel, AdfTabToolBarViewImpl> {
    }

    @UiField
    Button importButton;

    private Presenter presenter;
    private ImportFileDialog importFileDialog;

    public AdfTabToolBarViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        importButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                importFileDialog = new ImportFileDialog("Import Array Design Data...");
                importFileDialog.addImportFileDialogHandler(new ImportFileDialog.Handler() {
                    public void onImport(String fileName, AsyncEventFinishListener listener) {
                        presenter.importFile(listener);
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
}
