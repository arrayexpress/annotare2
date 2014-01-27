package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
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

    private Map<String, FileRef> labeledExtractId2FileRef;

    @SuppressWarnings("unused")
    DataAssignmentColumn() {
        /* used by GWT serialization */
    }

    public DataAssignmentColumn(int index, FileType type) {
        this.index = index;
        this.type = type;
        this.labeledExtractId2FileRef = new HashMap<String, FileRef>();
    }

    public int getIndex() {
        return index;
    }

    public FileType getType() {
        return type;
    }

    public FileRef getFileRef(DataAssignmentRow row) {
        return getFileRef(row.getLabeledExtractId());
    }

    public FileRef getFileRef(String labeledExtractId) {
        return labeledExtractId2FileRef.get(labeledExtractId);
    }

    public void setFileRef(DataAssignmentRow row, FileRef fileRef) {
        setFileRef(row.getLabeledExtractId(), fileRef);
    }

    public void setFileRef(String assayId, FileRef fileRef) {
        if (null == fileRef) {
            labeledExtractId2FileRef.remove(assayId);
        } else {
            labeledExtractId2FileRef.put(assayId, fileRef);
        }
    }


    public Collection<FileRef> getFileRefs() {
        return labeledExtractId2FileRef.values();
    }

    public Collection<String> getLabeledExtractIds() {
        return labeledExtractId2FileRef.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataAssignmentColumn column = (DataAssignmentColumn) o;

        return (index == column.index);
    }

    @Override
    public int hashCode() {
        return index;
    }
}
