package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.configmodel.FileType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentColumn implements IsSerializable {

    private int index;

    private FileType type;

    private Map<String, String> assayId2FileName;

    DataAssignmentColumn() {
        /* used by GWT serialization */
    }

    public DataAssignmentColumn(int index, FileType type) {
        this.index = index;
        this.type = type;
        this.assayId2FileName = new HashMap<String, String>();
    }

    public int getIndex() {
        return index;
    }

    public FileType getType() {
        return type;
    }

    public String getFileName(DataAssignmentRow row) {
        return getFileName(row.getAssayId());
    }

    public String getFileName(String assayId) {
        return assayId2FileName.get(assayId);
    }

    public void setFileName(DataAssignmentRow row, String fileName) {
        setFileName(row.getAssayId(), fileName);
    }

    public void setFileName(String assayId, String fileName) {
        if (fileName == null) {
            assayId2FileName.remove(assayId);
        } else {
            assayId2FileName.put(assayId, fileName);
        }
    }

    public Collection<String> getFileNames() {
        return assayId2FileName.values();
    }

    public Collection<String> getAssayIds() {
        return assayId2FileName.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataAssignmentColumn column = (DataAssignmentColumn) o;

        if (index != column.index) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return index;
    }
}
