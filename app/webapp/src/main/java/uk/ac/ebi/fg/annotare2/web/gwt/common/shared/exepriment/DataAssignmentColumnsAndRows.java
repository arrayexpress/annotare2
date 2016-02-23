package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentColumnsAndRows implements IsSerializable {

    private List<DataAssignmentColumn> columns;
    private List<DataAssignmentRow> rows;

    DataAssignmentColumnsAndRows() {
     /* used by GWT serialization */
    }

    public DataAssignmentColumnsAndRows(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<DataAssignmentColumn> getColumns() {
        return columns;
    }

    public List<DataAssignmentRow> getRows() {
        return rows;
    }
}
