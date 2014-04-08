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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class GridView<R extends HasIdentity> extends Composite implements PasteArea.PasteEventHandler {

    private static final int PAGE_SIZE = 50;

    interface Binder extends UiBinder<Widget, GridView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    SimpleLayoutPanel gridPanel;

    @UiField
    HorizontalPanel toolBar;

    @UiField
    HorizontalPanel tools;

    private CustomDataGrid<R> dataGrid;
    private MultiSelectionModel<R> selectionModel;
    private ColumnSortEvent.ListHandler<R> sortHandler;
    private SimplePager pager;
    private PasteArea<R> pasteArea;

    private ListDataProvider<R> dataProvider;

    private int permanentColumnCount;

    public GridView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void addTool(Widget tool) {
        tools.add(tool);
    }

    public void setRows(List<R> rows) {
        if (dataGrid != null) {
            dataProvider.setList(rows);
            return;
        }
        dataGrid = new CustomDataGrid<R>(PAGE_SIZE, CustomDataGrid.createResources());
        dataGrid.setEmptyTableWidget(new Label("No data"));

        selectionModel =
                new MultiSelectionModel<R>(new ProvidesKey<R>() {
                    @Override
                    public Object getKey(R item) {
                        return item.getIdentity();
                    }
                });

        dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<R>createCheckboxManager());

        pasteArea = new PasteArea<R>();
        pasteArea.addPasteHandler(this);

        dataGrid.addCellPreviewHandler(pasteArea);

        dataProvider = new ListDataProvider<R>();
        dataProvider.addDataDisplay(dataGrid);
        dataProvider.getList().addAll(rows);

        sortHandler = new ColumnSortEvent.ListHandler<R>(dataProvider.getList());
        dataGrid.addColumnSortHandler(sortHandler);
        permanentColumnCount = 0;
        addCheckBoxColumn();

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        toolBar.add(pager);
        toolBar.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);

        gridPanel.add(dataGrid);
    }

    public void clearAllColumns() {
        if (dataGrid == null) {
            return;
        }
        /* 2 columns must stay: first checkbox and last buffer column */
        clearColumns(1, dataGrid.getColumnCount() - 2);
    }

    public void clearColumns() {
        int columnCount = dataGrid.getColumnCount() - permanentColumnCount - 1;
        if (columnCount <= 0) {
            return;
        }
        clearColumns(permanentColumnCount, columnCount);
    }

    private void clearColumns(int fromIndex, int count) {
        for (int i = 0; i < count; i++) {
            dataGrid.clearColumnWidth(fromIndex + i);
        }
        for (int i = 0; i < count; i++) {
            dataGrid.removeColumn(fromIndex);
        }
        dataGrid.fix();
    }

    public void addPermanentColumn(Column<R, ?> column, Header<?> header, int width, Style.Unit unit) {
        permanentColumnCount++;
        addColumn(column, header, width, unit);
    }

    public void addPermanentColumn(String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit) {
        permanentColumnCount++;
        addColumn(columnName, column, comparator, width, unit);
    }

    public void addColumn(String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit) {
        insertColumn(dataGrid.getColumnCount(), columnName, column, comparator, width, unit);
    }

    public void addColumn(Column<R, ?> column, Header<?> header, int width, Style.Unit unit) {
        insertColumn(dataGrid.getColumnCount(), column, header, width, unit);
    }

    public void insertColumn(int beforeIndex, String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit) {
        if (comparator != null) {
            sortHandler.setComparator(column, comparator);
        }
        dataGrid.insertResizableColumn(column, columnName, beforeIndex);
        dataGrid.setColumnWidth(column, width, unit);
    }

    public void insertColumn(int beforeIndex, Column<R, ?> column, Header<?> header, int width, Style.Unit unit) {
        dataGrid.insertColumn(beforeIndex, column, header);
        dataGrid.setColumnWidth(column, width, unit);
    }

    private void addCheckBoxColumn() {
        Column<R, Boolean> checkboxColumn = new Column<R, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(R object) {
                return dataGrid.getSelectionModel().isSelected(object);
            }
        };
        CheckboxHeader checkboxHeader = new CheckboxHeader();
        checkboxHeader.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                selectAllRows(event.getValue());
            }
        });
        addPermanentColumn(checkboxColumn, checkboxHeader, 40, Style.Unit.PX);
    }

    public void addRow(R row) {
        dataProvider.getList().add(row);
        if (pager.getPageCount() > 1) {
            pager.lastPage();
        }
    }

    private void selectAllRows(boolean selected) {
        int start = pager.getPageStart();
        List<R> sublist = dataProvider.getList().subList(
                start, Math.min(start + pager.getPageSize(), dataProvider.getList().size()));
        for (R row : sublist) {
            selectionModel.setSelected(row, selected);
        }
    }

    public Set<R> getSelectedRows() {
        return selectionModel.getSelectedSet();
    }

    @SuppressWarnings("unchecked")
    public void fillDownKeyboardSelectedColumn() {
        int colIndex = dataGrid.getKeyboardSelectedColumn();
        int rowIndex = dataGrid.getKeyboardSelectedRow();

        if (colIndex >= 2 && colIndex < dataGrid.getColumnCount() &&
                rowIndex >=0 && rowIndex < dataGrid.getRowCount()) {
            Column<R, ?> column = dataGrid.getColumn(colIndex);
            AbstractEditableCell<R, String> cell = (AbstractEditableCell<R, String>) column.getCell();
            List<R> rows = dataProvider.getList();
            String value = (String) column.getValue(rows.get(rowIndex));
            FieldUpdater<R, String> updater = (FieldUpdater<R, String>) column.getFieldUpdater();
            for (int i = rowIndex + 1; i < rows.size(); i++) {
                updater.update(i, rows.get(i), value);
                cell.clearViewData(rows.get(i));
            }
            dataProvider.refresh();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onPaste(PasteArea.PasteEvent event) {
        int colIndex = dataGrid.getKeyboardSelectedColumn();
        int rowIndex = dataGrid.getKeyboardSelectedRow();

        String pastedData = event.getData();
        if (null != pastedData && !pastedData.isEmpty()) {
            String[] pastedRows = pastedData.split("\\r\\n|[\\r\\n]");
            if (colIndex >= 2 && colIndex < dataGrid.getColumnCount() &&
                    rowIndex >=0 && rowIndex < dataGrid.getRowCount()) {
                Column<R, ?> column = dataGrid.getColumn(colIndex);
                AbstractEditableCell<R, String> cell = (AbstractEditableCell<R, String>) column.getCell();
                List<R> rows = dataProvider.getList();
                FieldUpdater<R, String> updater = (FieldUpdater<R, String>) column.getFieldUpdater();
                for (int i = 0; i < Math.min(rows.size() - rowIndex, pastedRows.length); i++) {
                    int j = i + rowIndex;
                    updater.update(j, rows.get(j), pastedRows[i]);
                    cell.clearViewData(rows.get(j));
                }
                dataProvider.refresh();
            }
        }

    }

    public List<R> getRows() {
        return new ArrayList<R>(dataProvider.getList());
    }

    public boolean moveRowUp(R row) {
        List<R> rows = dataProvider.getList();
        int index = rows.indexOf(row);
        if (swapRow(rows, index, index - 1)) {
            dataProvider.refresh();
            return true;
        }
        return false;
    }

    public boolean moveRowDown(R row) {
        List<R> rows = dataProvider.getList();
        int index = rows.indexOf(row);
        if (swapRow(rows, index, index + 1)) {
            dataProvider.refresh();
            return true;
        }
        return false;
    }

    private boolean swapRow(List<R> rows, int from, int to) {
        if ((from < 0) || (to >= rows.size()) || (to < 0)) {
            return false;
        }
        R toMove = rows.get(from);
        rows.set(from, rows.get(to));
        rows.set(to, toMove);
        return true;
    }

    public void removeSelectedRows() {
        Set<R> selectedRows = getSelectedRows();
        dataProvider.getList().removeAll(selectedRows);
    }

    public void redraw() {
        if (dataGrid != null) {
            dataGrid.redraw();
        }
    }

    private class CheckboxHeader extends Header<Boolean> implements HasValue<Boolean> {

        private boolean checked;
        private HandlerManager handlerManager;

        public CheckboxHeader() {
            super(new CheckboxCell());
            checked = false;
        }

        @Override
        public Boolean getValue() {
            return checked;
        }

        @Override
        public void onBrowserEvent(Cell.Context context, Element elem, NativeEvent nativeEvent) {
            int eventType = Event.as(nativeEvent).getTypeInt();
            if (eventType == Event.ONCHANGE) {
                nativeEvent.preventDefault();
                //use value setter to easily fire change event to handlers
                setValue(!checked, true);
            }
        }

        @Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
            return ensureHandlerManager().addHandler(ValueChangeEvent.getType(), handler);
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            ensureHandlerManager().fireEvent(event);
        }

        @Override
        public void setValue(Boolean value) {
            checked = value;
        }

        @Override
        public void setValue(Boolean value, boolean fireEvents) {
            checked = value;
            if (fireEvents) {
                ValueChangeEvent.fire(this, value);
            }
        }

        private HandlerManager ensureHandlerManager() {
            if (handlerManager == null) {
                handlerManager = new HandlerManager(this);
            }
            return handlerManager;
        }
    }
}
