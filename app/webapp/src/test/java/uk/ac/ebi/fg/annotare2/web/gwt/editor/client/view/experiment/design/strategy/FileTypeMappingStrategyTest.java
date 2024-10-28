package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.strategy;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.submission.model.EnumWithHelpText;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FileTypeMappingStrategyTest {

    private static List<DataAssignmentColumn> getAddedColumnList() {
        List<DataAssignmentColumn> columns = new ArrayList<>();
        columns.add(new DataAssignmentColumn(0, FileType.PROCESSED_FILE));
        return columns;
    }

    @Test
    public void testSequencingStrategy() {
        List<DataAssignmentColumn> columns = getAddedColumnList();
        SequencingStrategy strategy = new SequencingStrategy();
        List<EnumWithHelpText> allowedTypes = strategy.getAllowedFileTypes(columns);
        assertEquals(2, allowedTypes.size());
        assertTrue(allowedTypes.contains(FileType.RAW_FILE));
        assertTrue(allowedTypes.contains(FileType.PROCESSED_FILE));
    }


    @Test
    public void testTwoColorMicroarrayStrategy_NoRawFile() {
        List<DataAssignmentColumn> columns = getAddedColumnList();
        TwoColorMicroarrayStrategy strategy = new TwoColorMicroarrayStrategy();
        List<EnumWithHelpText> allowedTypes = strategy.getAllowedFileTypes(columns);
        assertEquals(2, allowedTypes.size());
        assertTrue(allowedTypes.contains(FileType.PROCESSED_FILE));
        assertTrue(allowedTypes.contains(FileType.RAW_FILE));
    }

    @Test
    public void testTwoColorMicroarrayStrategy_RawFilePresent() {
        List<DataAssignmentColumn> columns = getAddedColumnList();
        columns.add(new DataAssignmentColumn(1, FileType.RAW_FILE));
        TwoColorMicroarrayStrategy strategy = new TwoColorMicroarrayStrategy();
        List<EnumWithHelpText> allowedTypes = strategy.getAllowedFileTypes(columns);
        assertEquals(1, allowedTypes.size());
        assertTrue(allowedTypes.contains(FileType.PROCESSED_FILE));
    }

    @Test
    public void testOneColorMicroarrayStrategy_RawFileAndRawMatrixFilePresent() {
        List<DataAssignmentColumn> columns = getAddedColumnList();
        columns.add(new DataAssignmentColumn(1, FileType.RAW_FILE));
        columns.add(new DataAssignmentColumn(2, FileType.RAW_MATRIX_FILE));
        OneColorMicroArrayStrategy strategy = new OneColorMicroArrayStrategy();
        List<EnumWithHelpText> allowedTypes = strategy.getAllowedFileTypes(columns);
        assertEquals(1, allowedTypes.size());
        assertTrue(allowedTypes.contains(FileType.PROCESSED_FILE));
    }

    @Test
    public void testOneColorMicroarrayStrategy_NoRawFiles() {
        List<DataAssignmentColumn> columns = getAddedColumnList();
        OneColorMicroArrayStrategy strategy = new OneColorMicroArrayStrategy();
        List<EnumWithHelpText> allowedTypes = strategy.getAllowedFileTypes(columns);
        assertEquals(3, allowedTypes.size());
        assertTrue(allowedTypes.contains(FileType.PROCESSED_FILE));
        assertTrue(allowedTypes.contains(FileType.RAW_FILE));
        assertTrue(allowedTypes.contains(FileType.RAW_MATRIX_FILE));
    }

}