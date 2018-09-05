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
import uk.ac.ebi.fg.annotare2.submission.model.SingleCellExtractAttribute;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SingleCellExtractAttributesRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.BackwardCompatibleSelectionCell;

import java.util.Comparator;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.submission.model.SingleCellExtractAttribute.*;

public class SingleCellExtractAttributesViewImpl extends Composite implements SingleCellExtractAttributesView {

    private final static String MICRORNA_BY_HTS_TYPE = "microRNA profiling by high throughput sequencing";

    private final GridView<SingleCellExtractAttributesRow> gridView;
    private SingleCellExtractAttributesView.Presenter presenter;
    private String aeExperimentType;

    public SingleCellExtractAttributesViewImpl(){

        gridView = new GridView<SingleCellExtractAttributesRow>();

        Button button = new Button("Fill Down Value");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fillDownValue();
            }
        });
        gridView.addTool(button);

        initWidget(gridView);
    }
    @Override
    public void setData(List<SingleCellExtractAttributesRow> rows) {
        gridView.setRows(rows);
        setColumns();
    }

    private void setColumns() {
        addNameColumn();
        addColumn(SINGLE_CELL_ISOLATION);
        addColumn(LIBRARY_CONSTRUCTION);
        addColumn(END_BIAS);
        addColumn(PRIMER);
        addColumn(SPIKE_IN);
        if (MICRORNA_BY_HTS_TYPE.equals(aeExperimentType)) {
            addColumn(INPUT_MOLECULE);
        }

        addColumn(SPIKE_IN_DILUTION);
        addColumn(UMI_BARCODE_READ);
        addColumn(UMI_BARCODE_OFFSET);
        addColumn(UMI_BARCODE_SIZE);
        addColumn(cDNA_READ);

        addColumn(cDNA_READ_OFFSET);
        addColumn(cDNA_READ_SIZE);
        addColumn(CELL_BARCODE_READ);
        addColumn(CELL_BARCODE_OFFSET);
        addColumn(CELL_BARCODE_SIZE);

        addColumn(SAMPLE_BARCODE_READ);
        addColumn(SAMPLE_BARCODE_OFFSET);
        addColumn(SAMPLE_BARCODE_SIZE);
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
                /*if (LIBRARY_LAYOUT.equals(attr)) {
                    row.setValue("", NOMINAL_LENGTH);
                    row.setValue("", NOMINAL_SDEV);
                    row.setValue("", ORIENTATION);
                }*/
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX, attr.getHelpText());
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
}
