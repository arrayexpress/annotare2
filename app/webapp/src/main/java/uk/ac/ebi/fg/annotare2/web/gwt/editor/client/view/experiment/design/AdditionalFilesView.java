package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.List;

public interface AdditionalFilesView extends IsWidget {

    void loadData();

    void setDataFiles(List<DataFileRow> files);

    void setData(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows);
}
