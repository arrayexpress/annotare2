package uk.ac.ebi.fg.annotare.prototype.datagrid.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Arrays;
import java.util.List;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class DataGridSample implements EntryPoint {

    @UiField
    DataGrid<DataRow> dataGrid;

    interface DataGridSampleBinder extends UiBinder<Widget, DataGridSample> { //
    }

    private static class DataRow {
        private final String column1;
        private final String column2;
        private final String column3;

        public DataRow(String column1, String column2, String column3) {
            this.column1 = column1;
            this.column2 = column2;
            this.column3 = column3;
        }
    }

    private static final List<DataRow> DATA_ROWS = Arrays.asList(
            new DataRow("John", "Doe", "123 Fourth Avenue"),
            new DataRow("Joe", "Wells", "22 Lance Ln"),
            new DataRow("George", "Tsunpo", "1600 Pennsylvania Avenue"));

    public void onModuleLoad() {
        final DataGridSampleBinder binder = GWT.create(DataGridSampleBinder.class);
        final Widget widget = binder.createAndBindUi(this);
        RootLayoutPanel.get().add(widget);

        //Add these lines
        TextColumn<DataRow> column1 = new TextColumn<DataRow>() {
            @Override
            public String getValue(DataRow object) {
                return object.column1;
            }
        };
        dataGrid.addColumn(column1, "First column");
        dataGrid.setColumnWidth(column1, 200, Style.Unit.PX);

        TextColumn<DataRow> column2 = new TextColumn<DataRow>() {
            @Override
            public String getValue(DataRow object) {
                return object.column2;
            }
        };
        dataGrid.addColumn(column2, "Second column");
        dataGrid.setColumnWidth(column2, 200, Style.Unit.PX);

        TextColumn<DataRow> column3 = new TextColumn<DataRow>() {
            @Override
            public String getValue(DataRow object) {
                return object.column3;
            }
        };

        dataGrid.addColumn(column3, "Third column");
        dataGrid.setColumnWidth(column3, 200, Style.Unit.PX);

        dataGrid.setRowCount(DATA_ROWS.size(), true);
        dataGrid.setRowData(0, DATA_ROWS);
	}
}

