/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package com.google.gwt.user.cellview.client;


import com.google.gwt.cell.client.TextCell;

/**
 * DataGrid with resizable columns.
 *
 * @author Olga Melnichuk
 */
public class MyDataGrid<T> extends DataGrid<T> {

    private int minWidth;

    private boolean lastColumnAdded = false;

    public MyDataGrid(int pageSize, Resources resources) {
        super(pageSize, resources);
        getTableHeadElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
        getTableBodyElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
        getTableFootElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
    }

    public void setMinimumTableWidthInPx(int width) {
        super.setMinimumTableWidth(width, com.google.gwt.dom.client.Style.Unit.PX);
        minWidth = width;
    }

    public void addColumn(String title, Column<T, ?> column) {
        if (!lastColumnAdded) {
            Column<T, ?> lastColumn = new Column<T, String>(new TextCell()) {
                @Override
                public String getValue(T object) {
                    return "";
                }
            };
            addColumn(lastColumn, new MyResizableHeader<T>("", lastColumn, this));
            lastColumnAdded = true;
        }
        insertColumn(getColumnCount() - 1, column, new MyResizableHeader<T>(title, column, this));
    }

    protected int getTableBodyHeight() {
        return getTableBodyElement().getOffsetHeight();
    }

    protected void resizeColumn(Column<T, ?> target, int colWidth) {
        int tableWidth = tableHeader.getOffsetWidth();
        int columnCount = getColumnCount();
        int colIndex = -1;
        int lastColIndex = columnCount - 1;
        int residualWidth = 0;
        for (int i = 0; i < lastColIndex; i++) {
            Column<T, ?> column = getColumn(i);
            if (column != target) {
                residualWidth += getHeaderOffsetWidth(i);
            } else {
                colIndex = i;
            }
        }
        if (colIndex == lastColIndex) {
            return;
        }
        int currColWidth = getHeaderOffsetWidth(colIndex);
        int lastColWidth = getHeaderOffsetWidth(lastColIndex);
        int borderWidth = tableWidth - residualWidth - currColWidth - lastColWidth;

        int newTableWidth = residualWidth + colWidth + borderWidth;
        lastColWidth = Math.max(minWidth - newTableWidth, 0);

        setColumnWidth(target, colWidth + "px");
        setColumnWidth(getColumn(columnCount - 1), lastColWidth + "px");
        setTableWidth(newTableWidth + lastColWidth, com.google.gwt.dom.client.Style.Unit.PX);
    }

    protected int getHeaderOffsetWidth(int index) {
        return tableHeader.ensureTableColElement(index).getOffsetWidth();
    }

    static class MyResizableHeader<T> extends ResizableHeader<T> {
        private final MyDataGrid<T> dataGrid;

        public MyResizableHeader(String title, Column<T, ?> column, MyDataGrid<T> dataGrid) {
            super(title, dataGrid, column);
            this.dataGrid = dataGrid;
        }

        @Override
        protected int getTableBodyHeight() {
            return dataGrid.getTableBodyHeight();
        }

        @Override
        protected void resizeColumn(Column<T, ?> column, int width) {
            dataGrid.resizeColumn(column, width);
        }
    }

}
