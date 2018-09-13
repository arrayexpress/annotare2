package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.cellview.client.ConditionalColumn;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.SingleCellExtractAttribute;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SingleCellExtractAttributesRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.BackwardCompatibleSelectionCell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.submission.model.SingleCellExtractAttribute.*;

public class SingleCellExtractAttributesViewImpl extends Composite implements SingleCellExtractAttributesView {

    private final GridView<SingleCellExtractAttributesRow> gridView;
    private SingleCellExtractAttributesView.Presenter presenter;
    private String aeExperimentType;
    private List<String> inputMoleculeOptions;
    private List<OntologyTerm> efoTerms;

    public SingleCellExtractAttributesViewImpl(){

        gridView = new GridView<>();

        Button button = new Button("Fill Down Value");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fillDownValue();
            }
        });
        gridView.addTool(button);

        inputMoleculeOptions = new ArrayList<>();
        efoTerms = new ArrayList<>();
        initWidget(gridView);
    }
    @Override
    public void setData(List<SingleCellExtractAttributesRow> rows) {
        gridView.setRows(rows);
    }

    private void setColumns() {
        addNameColumn();

        addColumn(SINGLE_CELL_ISOLATION);
        addColumn(LIBRARY_CONSTRUCTION);
        addColumn(END_BIAS);
        addColumn(PRIMER);
        addColumn(SPIKE_IN);
        addColumn(SPIKE_IN_DILUTION);

        addInputMoleculeColumn();

        addColumnForLibraryConstruction10xSeq(UMI_BARCODE_READ);
        addColumnForLibraryConstruction10xSeq(UMI_BARCODE_OFFSET);
        addColumnForLibraryConstruction10xSeq(UMI_BARCODE_SIZE);

        addColumnForLibraryConstruction(cDNA_READ);
        addColumnForLibraryConstruction(cDNA_READ_OFFSET);
        addColumnForLibraryConstruction(cDNA_READ_SIZE);

        addColumnForLibraryConstruction(CELL_BARCODE_READ);
        addColumnForLibraryConstruction(CELL_BARCODE_OFFSET);
        addColumnForLibraryConstruction(CELL_BARCODE_SIZE);

        addColumnForLibraryConstruction(SAMPLE_BARCODE_READ);
        addColumnForLibraryConstruction(SAMPLE_BARCODE_OFFSET);
        addColumnForLibraryConstruction(SAMPLE_BARCODE_SIZE);
    }

    private void addInputMoleculeColumn() {
        convertEfoTermsToStrings();
        addColumn(INPUT_MOLECULE, inputMoleculeOptions);
    }

    private void addNameColumn() {
        Column<SingleCellExtractAttributesRow, String> column = new Column<SingleCellExtractAttributesRow, String>(new TextCell()) {
            @Override
            public String getValue(SingleCellExtractAttributesRow row) {
                return row.getName();
            }
        };
        // TODO: commented out until we start saving changes in the experiment profile
        //column.setFieldUpdater(new FieldUpdater<ExtractAttributesRow, String>() {
        //    @Override
        //    public void update(int index, ExtractAttributesRow row, String value) {
        //        // TODO check names are unique
        //        row.setName(value);
        //        updateRow(row);
        //    }
        //});
        column.setSortable(true);
        Comparator<SingleCellExtractAttributesRow> comparator = new Comparator<SingleCellExtractAttributesRow>() {
            @Override
            public int compare(SingleCellExtractAttributesRow o1, SingleCellExtractAttributesRow o2) {
                if (o1 == o2) {
                    return 0;
                }
                String v1 = o1.getName();
                String v2 = o2.getName();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Name", column, comparator, 150, Style.Unit.PX);
    }

    private void addColumn(final SingleCellExtractAttribute attr) {
        final Cell<String> cell = attr.hasOptions() ? new BackwardCompatibleSelectionCell<>(attr.getOptions()) : new EditTextCell();
        Column<SingleCellExtractAttributesRow, String> column = new Column<SingleCellExtractAttributesRow, String>(cell) {
            @Override
            public String getValue(SingleCellExtractAttributesRow row) {
                String value = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(value)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(value);
                }
                return value == null ? "" : attr.hasOptions() && attr.hasValue(value) ? attr.getOption(value) : value;
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<SingleCellExtractAttributesRow, String>() {
            @Override
            public void update(int index, SingleCellExtractAttributesRow row, String value) {
                String oldValue = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(oldValue)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(oldValue);
                }
                row.setValue(attr.hasOptions() ? attr.getValue(value) : value, attr);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX, attr.getHelpText());
    }

    private void addColumn(final SingleCellExtractAttribute attr, final List<String> efoTerms) {
        final Cell<String> cell = new BackwardCompatibleSelectionCell<>(efoTerms);
        Column<SingleCellExtractAttributesRow, String> column = new Column<SingleCellExtractAttributesRow, String>(cell) {
            @Override
            public String getValue(SingleCellExtractAttributesRow row) {
                String value = row.getValue(attr);
                if (!efoTerms.contains(value)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(value);
                }
                return value == null ? "" : value;
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<SingleCellExtractAttributesRow, String>() {
            @Override
            public void update(int index, SingleCellExtractAttributesRow row, String value) {
                String oldValue = row.getValue(attr);
                if (!efoTerms.contains(oldValue)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(oldValue);
                }
                row.setValue(value, attr);
                updateAvailableColumns(attr, value, row);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX, attr.getHelpText());
    }

    private void addColumnForLibraryConstruction(final SingleCellExtractAttribute attr) {
        final Cell<String> cell = attr.hasOptions() ? new BackwardCompatibleSelectionCell(attr.getOptions()) : new EditTextCell();

        Column<SingleCellExtractAttributesRow, String> column = new ConditionalColumn<SingleCellExtractAttributesRow>(cell) {
            @Override
            public String getValue(SingleCellExtractAttributesRow row) {
                String value = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(value)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(value);
                }
                return value == null ? "" : attr.hasOptions() ? attr.getOption(value) : value;
            }

            @Override
            public boolean isEditable(SingleCellExtractAttributesRow row) {
                return is10xDropSeqLibrarySelected(row.getValue(LIBRARY_CONSTRUCTION));
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<SingleCellExtractAttributesRow, String>() {
            @Override
            public void update(int index, SingleCellExtractAttributesRow row, String value) {
                String oldValue = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(oldValue)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(oldValue);
                }
                row.setValue(attr.hasOptions() ? attr.getValue(value) : value, attr);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX, attr.getHelpText());
    }

    private void addColumnForLibraryConstruction10xSeq(final SingleCellExtractAttribute attr) {
        final Cell<String> cell = attr.hasOptions() ? new BackwardCompatibleSelectionCell(attr.getOptions()) : new EditTextCell();

        Column<SingleCellExtractAttributesRow, String> column = new ConditionalColumn<SingleCellExtractAttributesRow>(cell) {
            @Override
            public String getValue(SingleCellExtractAttributesRow row) {
                String value = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(value)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(value);
                }
                return value == null ? "" : attr.hasOptions() ? attr.getOption(value) : value;
            }

            @Override
            public boolean isEditable(SingleCellExtractAttributesRow row) {
                return is10xSeqLibrarySelected(row.getValue(LIBRARY_CONSTRUCTION));
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<SingleCellExtractAttributesRow, String>() {
            @Override
            public void update(int index, SingleCellExtractAttributesRow row, String value) {
                String oldValue = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(oldValue)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(oldValue);
                }
                row.setValue(attr.hasOptions() ? attr.getValue(value) : value, attr);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX, attr.getHelpText());
    }

    private void updateAvailableColumns(SingleCellExtractAttribute attr, String value, SingleCellExtractAttributesRow row) {
        if(LIBRARY_CONSTRUCTION.equals(attr) && !is10xDropSeqLibrarySelected(value)){
            row.setValue("", UMI_BARCODE_READ);
            row.setValue("", UMI_BARCODE_OFFSET);
            row.setValue("", UMI_BARCODE_SIZE);
            row.setValue("", cDNA_READ);
            row.setValue("", cDNA_READ_OFFSET);
            row.setValue("", cDNA_READ_SIZE);
            row.setValue("", CELL_BARCODE_READ);
            row.setValue("", CELL_BARCODE_OFFSET);
            row.setValue("", CELL_BARCODE_SIZE);
            row.setValue("", SAMPLE_BARCODE_READ);
            row.setValue("", SAMPLE_BARCODE_OFFSET);
            row.setValue("", SAMPLE_BARCODE_SIZE);
        } else if (LIBRARY_CONSTRUCTION.equals(attr) && !is10xSeqLibrarySelected(value)){
            row.setValue("", UMI_BARCODE_READ);
            row.setValue("", UMI_BARCODE_OFFSET);
            row.setValue("", UMI_BARCODE_SIZE);
        }
    }

    private boolean is10xDropSeqLibrarySelected(String value){
        return "10xV1".equalsIgnoreCase(value) ||
                "10xV2".equalsIgnoreCase(value) ||
                "Drop-seq".equalsIgnoreCase(value) ||
                "other".equalsIgnoreCase(value);
    }

    private boolean is10xSeqLibrarySelected(String value){
        return is10xDropSeqLibrarySelected(value) ||
                "CEL-seq".equalsIgnoreCase(value) ||
                "CEL-seq2".equalsIgnoreCase(value) ||
                "MARS-seq".equalsIgnoreCase(value) ||
                "SCRB-seq".equalsIgnoreCase(value) ||
                "STRT-seq".equalsIgnoreCase(value);
    }

    @Override
    public void setAeExperimentType(String experimentType) {
        aeExperimentType = experimentType;
    }

    @Override
    public void setPresenter(SingleCellExtractAttributesView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setEfoTerms(ArrayList<OntologyTerm> efoTerms) {
        this.efoTerms = efoTerms;
        setColumns();
    }

    private void updateRow(SingleCellExtractAttributesRow row) {
        presenter.updateRow(row.copy());
        gridView.redraw();
    }

    private void fillDownValue() {
        gridView.fillDownKeyboardSelectedColumn();
    }

    private void convertEfoTermsToStrings(){
        inputMoleculeOptions.add("");
        for (OntologyTerm term:
             efoTerms) {
            inputMoleculeOptions.add(trimValue(term.getLabel()));
        }
    }

    private String trimValue(String value) {
        if (null != value) {
            value = value.replaceAll("([^\\t]*)[\\t].*", "$1").trim();
        }
        return value;
    }
}
