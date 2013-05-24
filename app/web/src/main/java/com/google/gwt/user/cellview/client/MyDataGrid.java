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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.HeaderPanel;

import java.util.List;

import static com.google.gwt.dom.client.Style.Unit.PX;
import static java.lang.Math.max;

/**
 * DataGrid with resizable columns.
 *
 * @author Olga Melnichuk
 */
public class MyDataGrid<T> extends DataGrid<T> {

    private boolean initialized = false;

    public MyDataGrid(int pageSize, Resources resources) {
        super(pageSize, resources);
        //getTableHeadElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
        //getTableBodyElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
        //getTableFootElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
    }

    @Override
    public void setRowData(int start, List<? extends T> values) {
        HeaderPanel header = (HeaderPanel) getWidget();
        final CustomScrollPanel scrollPanel = (CustomScrollPanel) header.getContentWidget();
        final int hPos = scrollPanel.getHorizontalScrollbar().getHorizontalScrollPosition();

        super.setRowData(start, values);

        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                scrollPanel.setHorizontalScrollPosition(hPos);
            }
        });
    }

    private int getVisibleWidth() {
        HeaderPanel header = (HeaderPanel) getWidget();
        return header.getContentWidget().getOffsetWidth();
    }

    public void addResizableColumn(Column<T, ?> column, String title) {
        insertResizableColumn(column, title, getColumnCount());
    }

    public void insertResizableColumn(Column<T, ?> column, String title, int beforeIndex) {
        insertColumn(beforeIndex, column, new MyResizableHeader<T>(title, column, this), null);
    }

    @Override
    public void insertColumn(int beforeIndex, Column<T, ?> col, Header<?> header, Header<?> footer) {
        if (getColumnCount() == 0) {
            Column<T, ?> lastColumn = new Column<T, String>(new TextCell()) {
                @Override
                public String getValue(T object) {
                    return "";
                }
            };
            super.insertColumn(0, lastColumn, new MyResizableHeader<T>("", lastColumn, this), null);
            beforeIndex = 1;
            initialized = true;
        }
        if (getColumnCount() > 0 && beforeIndex == getColumnCount()) {
            beforeIndex -= 1;
        }
        super.insertColumn(beforeIndex, col, header, footer);
    }

    protected int getTableBodyHeight() {
        return getTableBodyElement().getOffsetHeight();
    }

    protected void resizeColumn(int colIndex, int colWidth) {
        int residualWidth = 0;
        for (int i = 0; i < getColumnCount() - 1; i++) {
            if (i != colIndex) {
                residualWidth += getHeaderOffsetWidth(i);
            }
        }

        int lastColIndex = getColumnCount() - 1;
        int tableWidth = tableHeader.getOffsetWidth();
        int minWidth = getVisibleWidth();
        if (colIndex == lastColIndex) {
            int newTableWidth = max(residualWidth + colWidth, minWidth);
            int newColumnWidth = max(newTableWidth - residualWidth, 0);
            setTableWidth(residualWidth + newColumnWidth, PX);
            return;
        }
        int currColWidth = getHeaderOffsetWidth(colIndex);
        int lastColWidth = getHeaderOffsetWidth(lastColIndex);
        int borderWidth = tableWidth - residualWidth - currColWidth - lastColWidth;

        int newTableWidth = residualWidth + colWidth + borderWidth;
        lastColWidth = max(minWidth - newTableWidth, 0);

        setColumnWidth(colIndex, colWidth + "px");
        setTableWidth(newTableWidth + lastColWidth, PX);
    }

    protected void resizeColumn(Column<T, ?> target, int colWidth) {
        resizeColumn(getColumnIndex(target), colWidth);
    }

    public void fix() {
        removeUnusedDataGridColumns();
        adjustWidth();
    }

    // a workaround (for more details see:  http://code.google.com/p/google-web-toolkit/issues/detail?id=6711)
    private void removeUnusedDataGridColumns() {
        int columnCount = getColumnCount();
        NodeList<Element> colGroups = getElement().getElementsByTagName("colgroup");

        for (int i = 0; i < colGroups.getLength(); i++) {
            Element colGroupEle = colGroups.getItem(i);
            NodeList<Element> colList = colGroupEle.getElementsByTagName("col");

            for (int j = colList.getLength() - 2; j >= columnCount - 1; j--) {
                colGroupEle.removeChild(colList.getItem(j));
            }
        }
    }

    private void adjustWidth() {
        int residualWidth = 0;
        int lastColIndex = getColumnCount() - 1;
        for (int i = 0; i < lastColIndex; i++) {
            residualWidth += getHeaderOffsetWidth(i);
        }

        int minWidth = getVisibleWidth();
        if (residualWidth < minWidth) {
            setTableWidth(minWidth, PX);
            return;
        }
        setTableWidth(residualWidth, PX);
    }

    protected int getHeaderOffsetWidth(int col) {
        checkColumnBounds(col);
        return tableHeader.ensureTableColElement(col).getOffsetWidth();
    }

    private void checkColumnBounds(int col) {
        if (col < 0 || col >= getColumnCount()) {
            throw new IndexOutOfBoundsException("Column index out of bound: " + col);
        }
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
