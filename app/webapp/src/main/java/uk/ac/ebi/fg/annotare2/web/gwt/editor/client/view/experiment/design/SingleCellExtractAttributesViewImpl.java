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

    private final static String _10xV1 = "10x 3' v1";
    private final static String _10xV2 = "10x 3' v2";
    private final static String _10xV3 = "10x 3' v3";
    private final static String _10xV5 = "10x 5' v1";
    private final static String _10xV5_2 = "10x 5' v2 (dual index)";
    private final static String _10xFB_1 = "10x feature barcode (cell surface protein profiling)";
    private final static String _10xFB_2 = "10x feature barcode (sample multiplexing)";
    private final static String _10xIGE = "10x Ig enrichment";
    private final static String _10xATAC = "10x scATAC-seq";
    private final static String _10xTCR = "10x TCR enrichment";
    private final static String DROP_SEQ = "Drop-seq";
    private final static String DRONC_SEQ = "DroNc-seq";
    private final static String IN_DROP = "inDrop";
    private final static String SMART_SEQ2 = "smart-seq2";
    private final static String SCATAC_SEQ = "scATAC-seq";

    public SingleCellExtractAttributesViewImpl(){

        gridView = new GridView<>();
        gridView.setRowSelectionEnabled(false);

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
    public void setData(ArrayList<OntologyTerm> efoTerms, List<SingleCellExtractAttributesRow> rows) {
        this.efoTerms = efoTerms;
        gridView.setRows(rows);
        setColumns();
    }

    @Override
    public void setDataRows(List<SingleCellExtractAttributesRow> rows) {
        gridView.setRows(rows);
    }

    private void setColumns() {
        addNameColumn();

        addColumn(LIBRARY_CONSTRUCTION);
        addColumn(SINGLE_CELL_ISOLATION);
        addColumn(END_BIAS);

        addInputMoleculeColumn();

        addColumn(PRIMER);
        addColumn(SPIKE_IN);
        addColumn(SPIKE_IN_DILUTION);


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
                preFillValueInColumns(attr, value, row);
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
                return !(isSmartSeqLibrarySelected(row.getValue(LIBRARY_CONSTRUCTION)));
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
                return !(isSmartSeqLibrarySelected(row.getValue(LIBRARY_CONSTRUCTION)));
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

    private void preFillValueInColumns(SingleCellExtractAttribute attr, String value, SingleCellExtractAttributesRow row) {
        if(LIBRARY_CONSTRUCTION.equals(attr)) {
            removePreFillValues(row);
            if (value.equalsIgnoreCase(_10xV2) || value.equalsIgnoreCase(_10xV1) || value.equalsIgnoreCase(_10xV3)) {
                preFill10xV3Values(row, value);
            }
            else if(value.equalsIgnoreCase(_10xV5) || value.equalsIgnoreCase(_10xV5_2)){
                preFill10xV5Values(row, value);
            }
            else if(value.equalsIgnoreCase(_10xFB_1) ||
                    value.equalsIgnoreCase(_10xFB_2) ||
                    value.equalsIgnoreCase(_10xIGE) ||
                    value.equalsIgnoreCase(_10xATAC) ||
                    value.equalsIgnoreCase((_10xTCR))){
                preFillOther10xValues(row, value);
            }
            else if(value.equalsIgnoreCase(DROP_SEQ) ||
                    value.equalsIgnoreCase(DRONC_SEQ) ||
                    value.equalsIgnoreCase(IN_DROP)){
                preFillDropSeqValues(row, value);
            }
            else if(value.equalsIgnoreCase(SMART_SEQ2)){
                preFillSmartSeqValues(row);
            }
            else if(value.equalsIgnoreCase(SCATAC_SEQ)){
                preFillSCATACSeqValues(row);
            }
            else {
                removePreFillValues(row);
            }
        }
    }

    private void removePreFillValues(SingleCellExtractAttributesRow row) {
        row.setValue("", SINGLE_CELL_ISOLATION);
        row.setValue("", INPUT_MOLECULE);
        row.setValue("", PRIMER);
        row.setValue("", END_BIAS);
        row.setValue("", UMI_BARCODE_READ);
        row.setValue("", UMI_BARCODE_OFFSET);
        row.setValue("", UMI_BARCODE_SIZE);
        row.setValue("", CELL_BARCODE_READ);
        row.setValue("", CELL_BARCODE_OFFSET);
        row.setValue("", CELL_BARCODE_SIZE);
        row.setValue("", cDNA_READ);
        row.setValue("", cDNA_READ_OFFSET);
        row.setValue("", cDNA_READ_SIZE);
        row.setValue("", SAMPLE_BARCODE_READ);
        row.setValue("", SAMPLE_BARCODE_OFFSET);
        row.setValue("", SAMPLE_BARCODE_SIZE);
    }

    private void preFill10xV3Values(SingleCellExtractAttributesRow row, String value){
        row.setValue("10x technology", SINGLE_CELL_ISOLATION);
        row.setValue("polyA RNA", INPUT_MOLECULE);
        row.setValue("oligo-dT", PRIMER);
        row.setValue("3 prime tag", END_BIAS);
        row.setValue(value.equalsIgnoreCase(_10xV1) ? "read2" : "read1" , UMI_BARCODE_READ);
        row.setValue(value.equalsIgnoreCase(_10xV1) ? "0" : "16", UMI_BARCODE_OFFSET);
        row.setValue(value.equalsIgnoreCase(_10xV3) ? "12" : "10", UMI_BARCODE_SIZE);
        row.setValue(value.equalsIgnoreCase(_10xV1) ? "index1" : "read1", CELL_BARCODE_READ);
        row.setValue("0", CELL_BARCODE_OFFSET);
        row.setValue(value.equalsIgnoreCase(_10xV1) ? "14" : "16", CELL_BARCODE_SIZE);
        row.setValue(value.equalsIgnoreCase(_10xV1) ? "read1" : "read2", cDNA_READ);
        row.setValue("0", cDNA_READ_OFFSET);
        row.setValue(value.equalsIgnoreCase(_10xV3) ? "91" : "98", cDNA_READ_SIZE);
        row.setValue(value.equalsIgnoreCase(_10xV1) ? "index 2" : "index1", SAMPLE_BARCODE_READ);
        row.setValue("0", SAMPLE_BARCODE_OFFSET);
        row.setValue("8", SAMPLE_BARCODE_SIZE);
    }

    private void preFill10xV5Values(SingleCellExtractAttributesRow row, String value){
        row.setValue("10x technology", SINGLE_CELL_ISOLATION);
        row.setValue("polyA RNA", INPUT_MOLECULE);
        row.setValue("oligo-dT", PRIMER);
        row.setValue("5 prime tag", END_BIAS);
        if(value.equalsIgnoreCase(_10xV5)){
            row.setValue("read1" , UMI_BARCODE_READ);
            row.setValue("16", UMI_BARCODE_OFFSET);
            row.setValue("10", UMI_BARCODE_SIZE);
            row.setValue("read1", CELL_BARCODE_READ);
            row.setValue("0", CELL_BARCODE_OFFSET);
            row.setValue("16", CELL_BARCODE_SIZE);
            row.setValue("read2", cDNA_READ);
            row.setValue("0", cDNA_READ_OFFSET);
            row.setValue("91", cDNA_READ_SIZE);
            row.setValue("index1", SAMPLE_BARCODE_READ);
            row.setValue("0", SAMPLE_BARCODE_OFFSET);
            row.setValue("8", SAMPLE_BARCODE_SIZE);
        }
    }

    private void preFillOther10xValues(SingleCellExtractAttributesRow row, String value){
        row.setValue("10x technology", SINGLE_CELL_ISOLATION);
        if(value.equalsIgnoreCase(_10xIGE) || value.equalsIgnoreCase(_10xTCR)){
            row.setValue("polyA RNA", INPUT_MOLECULE);
            row.setValue("oligo-dT", PRIMER);
            row.setValue("5 prime tag", END_BIAS);
        }
        else if(value.equalsIgnoreCase(_10xATAC)){
            row.setValue("genomic DNA", INPUT_MOLECULE);
            row.setValue("not applicable", END_BIAS);
            row.setValue("index1", CELL_BARCODE_READ);
            row.setValue("0", CELL_BARCODE_OFFSET);
            row.setValue("16", CELL_BARCODE_SIZE);
            row.setValue("", cDNA_READ);
            row.setValue("0", cDNA_READ_OFFSET);
            row.setValue("50", cDNA_READ_SIZE);
            row.setValue("index2", SAMPLE_BARCODE_READ);
            row.setValue("0", SAMPLE_BARCODE_OFFSET);
            row.setValue("8", SAMPLE_BARCODE_SIZE);
        }
    }

    private void preFillDropSeqValues(SingleCellExtractAttributesRow row, String value){
        if(value.equalsIgnoreCase(DROP_SEQ)){
            row.setValue("droplet-based cell isolation", SINGLE_CELL_ISOLATION);
            row.setValue("polyA RNA", INPUT_MOLECULE);
            row.setValue("oligo-dT", PRIMER);
            row.setValue("3 prime tag", END_BIAS);
            row.setValue("read1" , UMI_BARCODE_READ);
            row.setValue("12", UMI_BARCODE_OFFSET);
            row.setValue("8", UMI_BARCODE_SIZE);
            row.setValue("read1", CELL_BARCODE_READ);
            row.setValue("0", CELL_BARCODE_OFFSET);
            row.setValue("12", CELL_BARCODE_SIZE);
            row.setValue("read2", cDNA_READ);
            row.setValue("0", cDNA_READ_OFFSET);
            row.setValue("50", cDNA_READ_SIZE);
        }else if(value.equalsIgnoreCase(DRONC_SEQ)){
            row.setValue("droplet-based cell isolation", SINGLE_CELL_ISOLATION);
        } else{
            row.setValue("inDrop", SINGLE_CELL_ISOLATION);
        }

    }

    private void preFillSmartSeqValues(SingleCellExtractAttributesRow row){
        row.setValue("FACS", SINGLE_CELL_ISOLATION);
        row.setValue("polyA RNA", INPUT_MOLECULE);
        row.setValue("oligo-dT", PRIMER);
        row.setValue("none", END_BIAS);
    }

    private void preFillSCATACSeqValues(SingleCellExtractAttributesRow row){
        row.setValue("genomic DNA", INPUT_MOLECULE);
        row.setValue("not applicable", END_BIAS);
    }

    private boolean is10xDropSeqLibrarySelected(String value){
        return _10xV1.equalsIgnoreCase(value) ||
                _10xV2.equalsIgnoreCase(value) ||
                _10xV3.equalsIgnoreCase(value) ||
                DROP_SEQ.equalsIgnoreCase(value) ||
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

    private  boolean isSmartSeqLibrarySelected(String value){
        return "Smart-like (using SMARTer chemistry)".equalsIgnoreCase(value) ||
                "Smart-seq2".equalsIgnoreCase(value);
    }

    @Override
    public void setAeExperimentType(String experimentType) {
        aeExperimentType = experimentType;
    }

    @Override
    public void setPresenter(SingleCellExtractAttributesView.Presenter presenter) {
        this.presenter = presenter;
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