package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentColumn implements IsSerializable {

    private int index;

    private FileType type;

    private Map<String, String> labeledExtractId2FileName;

    DataAssignmentColumn() {
        /* used by GWT serialization */
    }

    public DataAssignmentColumn(int index, FileType type) {
        this.index = index;
        this.type = type;
        this.labeledExtractId2FileName = new HashMap<String, String>();
    }

    public int getIndex() {
        return index;
    }

    public FileType getType() {
        return type;
    }

    public String getFileName(DataAssignmentRow row) {
        return getFileName(row.getLabeledExtractId());
    }

    public String getFileName(String labeledExtractId) {
        return labeledExtractId2FileName.get(labeledExtractId);
    }

    public void setFileName(DataAssignmentRow row, String fileName) {
        setFileName(row.getLabeledExtractId(), fileName);
    }

    public void setFileName(String assayId, String fileName) {
        if (fileName == null) {
            labeledExtractId2FileName.remove(assayId);
        } else {
            labeledExtractId2FileName.put(assayId, fileName);
        }
    }

    public Collection<String> getFileNames() {
        return labeledExtractId2FileName.values();
    }

    public Collection<String> getLabeledExtractIds() {
        return labeledExtractId2FileName.keySet();
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
