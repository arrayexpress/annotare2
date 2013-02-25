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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.ebi.fg.annotare2.magetab.table.Row;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import com.google.gwt.user.cellview.client.MyDataGridResources;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SheetModeViewImpl extends Composite implements SheetModeView, RequiresResize {

    private static final int PAGE_SIZE = 100;

    private final DockLayoutPanel panel;

    public SheetModeViewImpl() {
        panel = new DockLayoutPanel(Style.Unit.PX);
        initWidget(panel);
    }

    @Override
    public void setTable(Table table, boolean hasHeaders) {
        MyDataGridResources resources = GWT.create(MyDataGridResources.class);
        MyDataGrid<Row> dataGrid = new MyDataGrid<Row>(PAGE_SIZE, resources);
        dataGrid.setEmptyTableWidget(new Label("There's no data yet, come later"));
        dataGrid.setMinimumTableWidthInPx(panel.getOffsetWidth());

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);

        initColumns(table, dataGrid, hasHeaders);
        initRows(table, dataGrid, hasHeaders);

        panel.addNorth(pager, 40);
        panel.add(dataGrid);
    }

    private void initRows(Table table, MyDataGrid<Row> dataGrid, boolean hasHeaders) {
        ListDataProvider<Row> dataProvider = new ListDataProvider<Row>();
        dataProvider.addDataDisplay(dataGrid);

        int nRows = table.getHeight();
        List<Row> rows = new ArrayList<Row>();
        for (int j = (hasHeaders ? 1 : 0); j < nRows; j++) {
            rows.add(table.getRow(j));
        }
        dataProvider.setList(rows);
    }

    private void initColumns(Table table, MyDataGrid<Row> dataGrid, boolean hasHeaders) {
        if (table == null || table.isEmpty()) {
            return;
        }

        int nColumns = table.getTrimmedWidth();

        Row headerRow = table.getRow(0);

        for (int i = 0; i < nColumns; i++) {
            final int colIndex = i;
            String title = hasHeaders ? headerRow.getValue(colIndex) : i + "";

            Column<Row, String> column = new Column<Row, String>(new TextCell()) {
                @Override
                public String getValue(Row row) {
                    return row.getValue(colIndex);
                }
            };
            column.setSortable(false);
            dataGrid.setColumnWidth(i, 150, Style.Unit.PX);
            dataGrid.addColumn(title, column);
        }
    }

    @Override
    public void onResize() {
        panel.onResize();
    }
}