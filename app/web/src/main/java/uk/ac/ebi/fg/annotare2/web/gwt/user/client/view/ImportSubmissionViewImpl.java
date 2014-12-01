package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.user.client.ui.SimplePanel;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.ImportSubmissionPlace;

public class ImportSubmissionViewImpl extends SimplePanel implements ImportSubmissionView {

    Presenter presenter;

    private final ImportSubmissionDialog dialog;

    public ImportSubmissionViewImpl() {
        dialog = new ImportSubmissionDialog();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setImportStage(ImportSubmissionPlace.ImportStage importStage) {
        dialog.setImportStage(importStage.ordinal());
    }

    @Override
    public void startImport() {
        dialog.startImport(23);
    }
}
