package uk.ac.ebi.fg.annotare.prototype.datagrid.client;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.CustomDataGrid;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Arrays;
import java.util.List;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class DataGridSample implements EntryPoint {

    @UiField(provided=true)
    CustomDataGrid<DataRow> dataGrid;

    interface DataGridSampleBinder extends UiBinder<Widget, DataGridSample> { //
    }

    private static class DataRow {
        private final String column1;
        private final String column2;
        private final String column3;
        private final String column4;

        public DataRow(String column1, String column2, String column3, String column4) {
            this.column1 = column1;
            this.column2 = column2;
            this.column3 = column3;
            this.column4 = column4;
        }
    }

    private static final List<DataRow> DATA_ROWS = Arrays.asList(
            new DataRow("John", "Doe", "123 Fourth Avenue", "first"),
            new DataRow("Joe", "Wells", "22 Lance Ln", "second"),
            new DataRow("George", "Tsunpo", "1600 Pennsylvania Avenue", "third"),
            new DataRow("John", "Doe", "123 Fourth Avenue", "fourth"),
            new DataRow("Joe", "Wells", "22 Lance Ln", "fifth")
    );

    public DataGridSample() {
        dataGrid = new CustomDataGrid<DataRow>();
    }

    public void onModuleLoad() {
        final DataGridSampleBinder binder = GWT.create(DataGridSampleBinder.class);
        final Widget widget = binder.createAndBindUi(this);
        RootLayoutPanel.get().add(widget);

        //Add these lines
        Column<DataRow, String> column1 = new Column<DataRow, String>(new EditTextCell()) {
            @Override
            public String getValue(DataRow object) {
                return object.column1;
            }
        };
        dataGrid.addColumn(column1, "First column");
        //dataGrid.setColumnWidth(column1, 20, Style.Unit.PC);

        Column<DataRow, String> column2 = new Column<DataRow, String>(new EditTextCell()) {
            @Override
            public String getValue(DataRow object) {
                return object.column2;
            }
        };
        dataGrid.addColumn(column2, "Second column");
        //dataGrid.setColumnWidth(column2, 20, Style.Unit.PC);

        Column<DataRow, String> column3 = new Column<DataRow, String>(new EditTextCell()) {
            @Override
            public String getValue(DataRow object) {
                return object.column3;
            }
        };

        dataGrid.addColumn(column3, "Third column");
        //dataGrid.setColumnWidth(column3, 40, Style.Unit.PC);

        Column<DataRow, String> column4 = new Column<DataRow, String>(new EditSelectionCell(Arrays.asList("first", "second", "third", "fourth", "fifth"))) {
            @Override
            public String getValue(DataRow object) {
                return object.column4;
            }
        };

        dataGrid.addColumn(column4, "Fourth column");
        //dataGrid.setColumnWidth(column4, 20, Style.Unit.PC);

        dataGrid.setRowCount(DATA_ROWS.size(), true);
        dataGrid.setRowData(0, DATA_ROWS);
	}
}

