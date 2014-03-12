/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Row;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.LoadingIndicator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SheetModeViewImpl extends Composite implements SheetModeView, RequiresResize {

    private static final int PAGE_SIZE = 100;

    private final DockLayoutPanel panel;

    private final LoadingIndicator loadingIndicator;
    private final String noDataText;

    public SheetModeViewImpl() {
        this("There's no data yet, come later");
    }

    public SheetModeViewImpl(String noDataText) {
        this.noDataText = noDataText;
        loadingIndicator = new LoadingIndicator();
        panel = new DockLayoutPanel(Style.Unit.PX);
        panel.add(loadingIndicator);
        initWidget(panel);
    }

    @Override
    public void setTable(Table table, boolean hasHeaders) {
        panel.remove(loadingIndicator);

        MyDataGridResources resources = GWT.create(MyDataGridResources.class);
        MyDataGrid<IndexedRow> dataGrid = new MyDataGrid<IndexedRow>(PAGE_SIZE, resources);
        dataGrid.setEmptyTableWidget(new Label(noDataText));

        dataGrid.setSelectionModel(new NoSelectionModel<IndexedRow>());

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);

        ListDataProvider<IndexedRow> dataProvider = new ListDataProvider<IndexedRow>();
        dataProvider.addDataDisplay(dataGrid);
        dataProvider.getList().addAll(getRows(table, hasHeaders));

        ColumnSortEvent.ListHandler<IndexedRow> sortHandler =
                new ColumnSortEvent.ListHandler<IndexedRow>(dataProvider.getList());
        dataGrid.addColumnSortHandler(sortHandler);

        initColumns(table, dataGrid, sortHandler, hasHeaders);

        panel.addNorth(pager, 40);
        panel.add(dataGrid);
    }

    private List<IndexedRow> getRows(Table table, boolean hasHeaders) {
        int nRows = table.getHeight();
        List<IndexedRow> rows = new ArrayList<IndexedRow>();
        int i = 1;
        for (int j = (hasHeaders ? 1 : 0); j < nRows; j++) {
            rows.add(new IndexedRow(table.getRow(j), i++));
        }
        return rows;
    }

    private void initColumns(Table table, MyDataGrid<IndexedRow> dataGrid, ColumnSortEvent.ListHandler<IndexedRow> sortHandler, boolean hasHeaders) {
        if (table == null || table.isEmpty()) {
            return;
        }

        int nColumns = table.getTrimmedWidth();

        Row headerRow = table.getRow(0);

        for (int i = 0; i < nColumns + 1; i++) {
            if (i == 0) {
                Column<IndexedRow, String> column = new Column<IndexedRow, String>(new TextCell()) {
                    @Override
                    public String getValue(IndexedRow row) {
                        return row.getIndex() + "";
                    }
                };
                sortHandler.setComparator(column, new Comparator<IndexedRow>() {
                    @Override
                    public int compare(IndexedRow o1, IndexedRow o2) {
                        if (o1 == o2) {
                            return 0;
                        }
                        int v1 = o1.getIndex();
                        int v2 = o2.getIndex();
                        return new Integer(v1).compareTo(v2);
                    }
                });
                column.setSortable(true);
                dataGrid.addColumn(column, new TextHeader("N"));
                dataGrid.setColumnWidth(i, 50, Style.Unit.PX);
                continue;
            }

            final int colIndex = (i - 1);
            String title = hasHeaders ? headerRow.getValue(colIndex) : (i + 1) + "";
            Column<IndexedRow, String> column = new Column<IndexedRow, String>(new TextCell(MultiLineSafeHtmlRenderer.getInstance())) {
                @Override
                public String getCellStyleNames(Cell.Context context, IndexedRow row) {
                    String value = row.getValue(colIndex);
                    return isEmpty(value) ? "app-MageTabEmptyCell" :
                            isUnassigned(value) ? "app-MageTabUnassignedCell" :
                                    null;
                }

                @Override
                public String getValue(IndexedRow row) {
                    String value = row.getValue(colIndex);
                    return isEmpty(value) || isUnassigned(value) ? "" : value;
                }

                private boolean isUnassigned(String value) {
                    return value != null && value.startsWith("__UNASSIGNED__@");
                }

                private boolean isEmpty(String value) {
                    return value == null || value.isEmpty();
                }
            };
            sortHandler.setComparator(column, new Comparator<IndexedRow>() {
                @Override
                public int compare(IndexedRow o1, IndexedRow o2) {
                    if (o1 == o2) {
                        return 0;
                    }
                    String v1 = o1.getValue(colIndex);
                    String v2 = o2.getValue(colIndex);
                    if (v1 != null) {
                        return (v2 != null) ? v1.compareTo(v2) : 1;
                    }
                    return -1;
                }
            });
            column.setSortable(true);
            dataGrid.addResizableColumn(column, title);
            dataGrid.setColumnWidth(i, 150, Style.Unit.PX);
        }
        dataGrid.getColumnSortList().push(dataGrid.getColumn(0));
    }

    @Override
    public void onResize() {
        panel.onResize();
    }

    public static class IndexedRow {
        private final int index;
        private final Row row;

        public IndexedRow(Row row, int index) {
            this.index = index;
            this.row = row;
        }

        public String getValue(int column) {
            return row.getValue(column);
        }

        public int getIndex() {
            return index;
        }
    }
}