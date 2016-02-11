package uk.ac.ebi.fg.annotare.prototype.tooltip.client;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ConditonalColumn;
import com.google.gwt.user.cellview.client.CustomDataGrid;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.ebi.fg.annotare.prototype.tooltip.client.cell.EditSuggestCell;

import java.util.Arrays;
import java.util.List;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class TooltipSample implements EntryPoint {

    @UiField(provided=true)
    CustomDataGrid<DataRow> dataGrid;

    @UiField(provided=true)
    SuggestBox suggestBox;

    interface DataGridSampleBinder extends UiBinder<Widget, TooltipSample> { //
    }

    private static class DataRow {
        private String column1;
        private String column2;
        private String column3;
        private String column4;

        public DataRow(String column1, String column2, String column3, String column4) {
            this.column1 = column1;
            this.column2 = column2;
            this.column3 = column3;
            this.column4 = column4;
        }
    }

    private static final List<DataRow> DATA_ROWS = Arrays.asList(
            new DataRow("John", "Doe", "123 Fourth Avenue", ""),
            new DataRow("Joe", "Wells", "22 Lance Ln", "SINGLE"),
            new DataRow("George", "Tsunpo", "1600 Pennsylvania Avenue", "PAIRED"),
            new DataRow("John", "Doe", "123 Fourth Avenue", "SINGLE"),
            new DataRow("Joe", "Wells", "22 Lance Ln", "SINGLE")
    );

    private ListDataProvider<DataRow> dataProvider;
    private MultiWordSuggestOracle oracle;

    public TooltipSample() {
        dataGrid = new CustomDataGrid<DataRow>(CustomDataGrid.createResources());

        oracle = new MultiWordSuggestOracle();
        oracle.add("15 Fourth Avenue");
        oracle.add("45 Fourth Avenue");
        oracle.add("123 Fourth Avenue");
        oracle.add("10 Lance Ln");
        oracle.add("12 Lance Ln");
        oracle.add("22 Lance Ln");
        oracle.add("42 Lance Ln");
        oracle.add("100 Pennsylvania Avenue");
        oracle.add("200 Pennsylvania Avenue");
        oracle.add("400 Pennsylvania Avenue");
        oracle.add("800 Pennsylvania Avenue");
        oracle.add("1600 Pennsylvania Avenue");
        oracle.add("3200 Pennsylvania Avenue");
        oracle.add("1570 Cascade Ln");
        oracle.add("1670 Cascade Ln");
        oracle.add("2070 Cascade Ln");
        oracle.add("2570 Cascade Ln");

        suggestBox = new SuggestBox(oracle);
    }

    public void onModuleLoad() {
        final DataGridSampleBinder binder = GWT.create(DataGridSampleBinder.class);
        final Widget widget = binder.createAndBindUi(this);
        RootLayoutPanel.get().add(widget);

        //Add these lines
        Column<DataRow, String> column1 = new Column<DataRow, String>(new EditSuggestCell(null)) {
            @Override
            public String getValue(DataRow object) {
                return object.column1;
            }
        };

        column1.setFieldUpdater(new FieldUpdater<DataRow, String>() {
            @Override
            public void update(int index, DataRow object, String value) {
                object.column1 = value;
                dataProvider.refresh();
            }
        });

        dataGrid.addColumn(column1, "First column");
        dataGrid.setColumnWidth(column1, 100, Style.Unit.PX);

        Column<DataRow, String> column2 = new ConditonalColumn<DataRow, String>(new EditSuggestCell(null)) {
            @Override
            public String getValue(DataRow object) {
                return object.column2;
            }

            @Override
            public boolean isEditable(DataRow object) {
                return "paired".equalsIgnoreCase(object.column4);
            }
        };

        column2.setFieldUpdater(new FieldUpdater<DataRow, String>() {
            @Override
            public void update(int index, DataRow object, String value) {
                object.column2 = value;
                dataProvider.refresh();
            }
        });

        dataGrid.addColumn(column2, "Second column");
        //dataGrid.setColumnWidth(column2, 20, Style.Unit.PC);

        Column<DataRow, String> column3 = new Column<DataRow, String>(new EditSuggestCell(oracle, true)) {
            @Override
            public String getValue(DataRow object) {
                return object.column3;
            }
        };

        column3.setFieldUpdater(new FieldUpdater<DataRow, String>() {
            @Override
            public void update(int index, DataRow object, String value) {
                object.column3 = value;
                //dataProvider.refresh();
            }
        });

        dataGrid.addColumn(column3, "Third column");
        //dataGrid.setColumnWidth(column3, 40, Style.Unit.PC);

        Column<DataRow, String> column4 = new Column<DataRow, String>(new SelectionCell(Arrays.asList("", "SINGLE", "PAIRED"))) {
            @Override
            public String getValue(DataRow object) {
                return object.column4;
            }
        };

        column4.setFieldUpdater(new FieldUpdater<DataRow, String>() {
            @Override
            public void update(int index, DataRow object, String value) {
                object.column4 = value;
                dataProvider.refresh();
            }
        });
        dataGrid.addColumn(column4, "Fourth column");

        dataProvider = new ListDataProvider<DataRow>();
        dataProvider.addDataDisplay(dataGrid);
        dataProvider.getList().addAll(DATA_ROWS);
	}
}

