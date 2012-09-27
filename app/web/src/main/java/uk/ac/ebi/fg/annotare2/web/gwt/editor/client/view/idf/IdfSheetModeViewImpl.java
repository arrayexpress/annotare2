/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import uk.ac.ebi.fg.annotare2.magetab.base.Row;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;

import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class IdfSheetModeViewImpl extends Composite implements IdfSheetModeView {

    private final HorizontalPanel panel;

    public IdfSheetModeViewImpl() {
        panel = new HorizontalPanel();
        //panel.setHeight("100%");
        //panel.setWidth("100%");
        initWidget(panel);
    }

    @Override
    public void setTable(Table table) {
        //DataGrid<Row> dataGrid = new DataGrid<Row>();
        //dataGrid.setEmptyTableWidget(new Label("No data found"));
        //CellTable<Row> cellTable = new CellTable<Row>();
        //cellTable.setWidth("100%", true);

        int tableWidth = table.getTrimmedWidth();
        int tableHeight = table.getHeight();
        //cellTable.setVisibleRange(0, tableHeight);
       // dataGrid.setVisibleRange(0, tableHeight);

        ArrayList<ColumnConfig<Row, ?>> columnConfigs = new ArrayList<ColumnConfig<Row, ?>>();

        for (int i = 0; i < tableWidth; i++) {
            final int colIndex = i;
            Column<Row, String> column = new Column<Row, String>(new TextCell()) {
                @Override
                public String getValue(Row row) {
                    return row.getValue(colIndex);
                }
            };
            //cellTable.addColumn(column, new TextHeader(""));
      //      dataGrid.addColumn(column, new TextHeader("test"));
      //      dataGrid.setColumnWidth(column, 100, Style.Unit.PX);
            ColumnConfig<Row, String> config = new ColumnConfig<Row, String>(new ValueProvider<Row, String>() {
                @Override
                public String getValue(Row row) {
                    return row.getValue(colIndex);
                }

                @Override
                public void setValue(Row row, String value) {
                    row.getValue(colIndex);
                }

                @Override
                public String getPath() {
                    return null;
                }
            });
            config.setHeader(" ");
            config.setSortable(false);
            config.setMenuDisabled(true);
            columnConfigs.add(config);
        }

        //TODO move this to the Table
        ArrayList<Row> rows = new ArrayList<Row>();
        for (int j = 0; j < tableHeight; j++) {
            rows.add(table.getRow(j));
        }

        ListDataProvider<Row> dataProvider = new ListDataProvider<Row>();
        dataProvider.setList(rows);
        //dataProvider.addDataDisplay(cellTable);
        //panel.setWidget(cellTable);
      //  dataProvider.addDataDisplay(dataGrid);
       // panel.setWidget(dataGrid);

        ListStore<Row> store = new ListStore<Row>(new ModelKeyProvider<Row>() {
            @Override
            public String getKey(Row item) {
                return item.toString();
            }
        });
        store.addAll(rows);

        ColumnModel<Row> columnModel = new ColumnModel<Row>(columnConfigs);

        Grid<Row> grid = new Grid<Row>(store, columnModel);
        grid.setColumnReordering(false);
        panel.add(grid);


    }
}
