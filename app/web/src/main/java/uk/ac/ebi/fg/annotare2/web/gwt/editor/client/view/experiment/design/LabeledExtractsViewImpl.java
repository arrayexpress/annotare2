package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.LabeledExtractRow;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractsViewImpl extends Composite implements LabeledExtractsView {

    private final GridView<LabeledExtractRow> gridView;

    public LabeledExtractsViewImpl() {
        gridView = new GridView<LabeledExtractRow>();
        initWidget(gridView);
    }
}
