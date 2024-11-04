package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.strategy;

import uk.ac.ebi.fg.annotare2.submission.model.EnumWithHelpText;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;

import java.util.ArrayList;
import java.util.List;

public class TwoColorMicroarrayStrategy implements FileTypeMappingStrategy {

    @Override
    public List<EnumWithHelpText> getAllowedFileTypes(List<DataAssignmentColumn> columns) {
        List<EnumWithHelpText> allowedFileTypes = new ArrayList<>();
        allowedFileTypes.add(FileType.PROCESSED_FILE);
        if(columns.stream().noneMatch(c -> c.getType() == FileType.RAW_FILE)){
            allowedFileTypes.add(FileType.RAW_FILE);
        }
        return allowedFileTypes;
    }
}
