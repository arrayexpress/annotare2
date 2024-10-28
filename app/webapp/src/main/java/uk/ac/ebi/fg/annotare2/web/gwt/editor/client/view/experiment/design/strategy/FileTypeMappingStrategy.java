package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.strategy;

import uk.ac.ebi.fg.annotare2.submission.model.EnumWithHelpText;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;

import java.util.Arrays;
import java.util.List;

public interface FileTypeMappingStrategy {
    default List<EnumWithHelpText> getAllowedFileTypes(List<DataAssignmentColumn> columns){
        return Arrays.asList(FileType.RAW_FILE, FileType.PROCESSED_FILE, FileType.RAW_MATRIX_FILE, FileType.PROCESSED_MATRIX_FILE);
    }
}
