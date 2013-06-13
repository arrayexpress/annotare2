package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.LabeledExtractRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.Comparator;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractsViewImpl extends Composite implements LabeledExtractsView {

    private final GridView<LabeledExtractRow> gridView;

    public LabeledExtractsViewImpl() {
        gridView = new GridView<LabeledExtractRow>();
        Button button = new Button("Labels");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        });
        gridView.addTool(button);
        initWidget(gridView);
    }

    @Override
    public void setData(List<LabeledExtractRow> rows, List<String> labels) {
        gridView.setRows(rows);
        setColumns(labels);
    }

    private void setColumns(List<String> labels) {
        addNameColumn();
        for(String label : labels) {
            addColumn(label);
        }
    }

    private void addNameColumn() {
        Column<LabeledExtractRow, String> column = new Column<LabeledExtractRow, String>(new EditTextCell()) {
            @Override
            public String getValue(LabeledExtractRow row) {
                return row.getName();
            }
        };
        column.setSortable(true);
        Comparator<LabeledExtractRow> comparator = new Comparator<LabeledExtractRow>() {
            @Override
            public int compare(LabeledExtractRow o1, LabeledExtractRow o2) {
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

    private void addColumn(final String label) {
        Column<LabeledExtractRow, String> column = new Column<LabeledExtractRow, String>(new TextCell()) {
            @Override
            public String getValue(LabeledExtractRow object) {
                return object.hasLabel(label) ? "+" : "";
            }
        };
        gridView.addColumn(label, column, null, 150, Style.Unit.PX);
    }

}
