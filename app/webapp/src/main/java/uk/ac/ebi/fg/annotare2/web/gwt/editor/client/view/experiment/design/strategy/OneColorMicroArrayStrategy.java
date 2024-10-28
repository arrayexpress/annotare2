package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.strategy;

import uk.ac.ebi.fg.annotare2.submission.model.EnumWithHelpText;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OneColorMicroArrayStrategy implements FileTypeMappingStrategy {

    @Override
    public List<EnumWithHelpText> getAllowedFileTypes(List<DataAssignmentColumn> columns) {
        List<EnumWithHelpText> allowedFileTypes = new ArrayList<>();
        allowedFileTypes.add(FileType.PROCESSED_FILE);
        Stream.of(FileType.RAW_FILE, FileType.RAW_MATRIX_FILE)
                .filter(type -> columns.stream()
                        .noneMatch(column -> column.getType() == type))
                .forEach(allowedFileTypes::add);
        return allowedFileTypes;
    }
}
