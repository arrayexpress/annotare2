package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.user.client.ui.SimplePanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.List;

public class ImportSubmissionViewImpl extends SimplePanel implements ImportSubmissionView {

    Presenter presenter;

    private final ImportSubmissionDialog dialog;

    public ImportSubmissionViewImpl() {
        dialog = new ImportSubmissionDialog();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        dialog.setPresenter(presenter);
    }

    @Override
    public void startImport() {
        dialog.startImport();
    }

    @Override
    public void setDataFiles(List<DataFileRow> files) {
        dialog.setDataFiles(files);
    }

    @Override
    public void setAeExperimentTypeOptions(List<String> aeExperimentTypeOptions) {
        dialog.setAeExperimentTypeOptions(aeExperimentTypeOptions);
    }
}
