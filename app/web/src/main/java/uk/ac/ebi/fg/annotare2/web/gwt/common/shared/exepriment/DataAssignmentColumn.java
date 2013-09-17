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

    private Map<String, Long> assayId2FileId;

    DataAssignmentColumn() {
        /* used by GWT serialization */
    }

    public DataAssignmentColumn(int index, FileType type) {
        this.index = index;
        this.type = type;
        this.assayId2FileId = new HashMap<String, Long>();
    }

    public int getIndex() {
        return index;
    }

    public FileType getType() {
        return type;
    }

    public Long getFileId(DataAssignmentRow row) {
        return assayId2FileId.get(row.getAssayId());
    }

    public void setFileId(DataAssignmentRow row, Long fileId) {
        setFileId(row.getAssayId(), fileId);
    }

    public void setFileId(String assayId, Long fileId) {
        if (fileId == null) {
            assayId2FileId.remove(assayId);
        } else {
            assayId2FileId.put(assayId, fileId);
        }
    }

    public Collection<Long> getFileIds() {
        return assayId2FileId.values();
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
