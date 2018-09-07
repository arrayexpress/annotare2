package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SingleCellExtractAttributesRow;

import java.util.ArrayList;
import java.util.List;

public interface SingleCellExtractAttributesView extends IsWidget {

    void setData(List<SingleCellExtractAttributesRow> rows);

    void setAeExperimentType(String experimentType);

    void setPresenter(SingleCellExtractAttributesView.Presenter presenter);

    void setEfoTerms(ArrayList<OntologyTerm> efoTerms);

    public interface Presenter {

        void updateRow(SingleCellExtractAttributesRow row);
    }
}
