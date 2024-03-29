/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class GridView<R extends HasIdentity> extends Composite implements RequiresResize {

    private static final int PAGE_SIZE = 5000;

    interface Binder extends UiBinder<Widget, GridView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    SimpleLayoutPanel gridPanel;

    @UiField
    HorizontalPanel toolBar;

    @UiField
    HorizontalPanel tools;

    @UiField
    SpanElement notePanel;

    private CustomDataGrid<R> dataGrid;
    private MultiSelectionModel<R> selectionModel;
    private ColumnSortEvent.ListHandler<R> sortHandler;
    private SimplePager pager;

    private ListDataProvider<R> dataProvider;

    private int permanentColumnCount;
    private boolean isRowSelectionEnabled;

    public GridView() {
        isRowSelectionEnabled = true;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setRowSelectionEnabled(boolean enabled) {
        isRowSelectionEnabled = enabled;
    }

    public void addTool(Widget tool) {
        tools.add(tool);
    }

    public void setRows(List<R> rows) {
        if (null != dataProvider) {
            dataProvider.setList(rows);
            return;
        }
        dataGrid = new CustomDataGrid<>(PAGE_SIZE);
        dataGrid.addStyleName("gwt-DataGrid");
        dataGrid.setWidth("100%");
        dataGrid.setEmptyTableWidget(new Label("No data"));

        if (isRowSelectionEnabled) {
            selectionModel =
                    new MultiSelectionModel<>(new ProvidesKey<R>() {
                        @Override
                        public Object getKey(R item) {
                            return item.getIdentity();
                        }
                    });

            dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<R>createCheckboxManager());
        }

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(dataGrid);
        dataProvider.getList().addAll(rows);

        sortHandler = new ColumnSortEvent.ListHandler<>(dataProvider.getList());
        dataGrid.addColumnSortHandler(sortHandler);
        permanentColumnCount = 0;
        if (isRowSelectionEnabled) {
            addCheckBoxColumn();
        }

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        //pager.setDisplay(dataGrid);
        //toolBar.add(pager);
        //toolBar.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);

        gridPanel.add(dataGrid);
    }

    public void clearAllColumns() {
        if (null != dataGrid) {
            int startingColumn = isRowSelectionEnabled ? 1 : 0;
            clearColumns(startingColumn, dataGrid.getColumnCount() - 1 - startingColumn);
        }
    }

    public void clearColumns() {
        if (null != dataGrid) {
            int columnCount = dataGrid.getColumnCount() - permanentColumnCount - 1;
            if (columnCount <= 0) {
                return;
            }
            clearColumns(permanentColumnCount, columnCount);
        }
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
        addPermanentColumn(columnName, column, comparator, width, unit,"");
    }

    public void addPermanentColumn(String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit, String helpText) {
        permanentColumnCount++;
        addColumn(columnName, column, comparator, width, unit, helpText);
    }

    public void addColumn(String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit, String helpText) {
        insertColumn(dataGrid.getColumnCount(), columnName, column, comparator, width, unit, helpText);
    }

    public void addColumn(String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit) {
        insertColumn(dataGrid.getColumnCount(), columnName, column, comparator, width, unit);
    }

    public void addColumn(Column<R, ?> column, Header<?> header, int width, Style.Unit unit) {
        insertColumn(dataGrid.getColumnCount(), column, header, width, unit);
    }

    public void insertColumn(int beforeIndex, String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit) {
        insertColumn(beforeIndex, columnName, column, comparator, width, unit, "");
    }

    public void insertColumn(int beforeIndex, String columnName, Column<R, ?> column, Comparator<R> comparator, int width, Style.Unit unit, String helpText) {
        if (comparator != null) {
            sortHandler.setComparator(column, comparator);
        }
        dataGrid.insertResizableColumn(column, columnName, beforeIndex, helpText);
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
        if (isRowSelectionEnabled) {
            //int start = pager.getPageStart();
/*            List<R> sublist = dataProvider.getList().subList(
                    start, Math.min(start + pager.getPageSize(), dataProvider.getList().size()));*/
            List<R> sublist = dataProvider.getList().subList(
                    0, dataProvider.getList().size());
            for (R row : sublist) {
                selectionModel.setSelected(row, selected);
            }
        }
    }

    public Set<R> getSelectedRows() {
        return isRowSelectionEnabled ? selectionModel.getSelectedSet() : Collections.<R>emptySet();
    }

    @SuppressWarnings("unchecked")
    private boolean isColumnEditable(Column<R, ?> column, R row) {
        if (column instanceof ConditionalColumn) {
            return ((ConditionalColumn<R>)column).isEditable(row);
        } else {
            return (column.getCell() instanceof AbstractEditableCell);
        }
    }

    @SuppressWarnings("unchecked")
    public void fillDownKeyboardSelectedColumn() {
        int colIndex = dataGrid.getKeyboardSelectedColumn();
        int rowIndex = dataGrid.getKeyboardSelectedRow() + dataGrid.getPageStart();

        if (colIndex >= 1 && colIndex < dataGrid.getColumnCount() &&
                rowIndex >=0 && rowIndex < dataGrid.getRowCount()) {
            Column<R, ?> column = dataGrid.getColumn(colIndex);
            List<R> rows = dataProvider.getList();
            if (isColumnEditable(column, rows.get(rowIndex)) && !(column instanceof SamplesViewImpl.SampleNameColumn)) {
                AbstractEditableCell<R, String> cell = (AbstractEditableCell<R, String>) column.getCell();
                String value = (String) column.getValue(rows.get(rowIndex));
                FieldUpdater<R, String> updater = (FieldUpdater<R, String>) column.getFieldUpdater();

                for (int i = rowIndex + 1; i < rows.size(); i++) {
                    if (isColumnEditable(column, rows.get(i))) {
                        updater.update(i, rows.get(i), value);
                        cell.clearViewData(rows.get(i));
                    }
                }
                dataProvider.refresh();
            } else {
                NotificationPopupPanel.warning("Fill-down function is not allowed for this column", true, false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean importValuesToKeyboardSelectedColumn(List<String> values) {
        int colIndex = dataGrid.getKeyboardSelectedColumn();
        int rowIndex = dataGrid.getKeyboardSelectedRow() + dataGrid.getPageStart();

        if (null != values && !values.isEmpty()) {
            if (colIndex >= 1 && colIndex < dataGrid.getColumnCount() &&
                    rowIndex >=0 && rowIndex < dataGrid.getRowCount()) {
                Column<R, ?> column = dataGrid.getColumn(colIndex);
                List<R> rows = dataProvider.getList();

                if (isColumnEditable(column, rows.get(rowIndex))) {
                    if (column instanceof SamplesViewImpl.SampleNameColumn) {
                        for (int i = 0; i < values.size(); i++) {
                            String value = values.get(i);
                            for (int j = i + 1; j < values.size(); j++) {
                                if (value.equalsIgnoreCase(values.get(j))) {
                                    NotificationPopupPanel.error("Please ensure all sample names are unique, duplicate name '" + values.get(j) +"'.", true, false);
                                    return false;
                                }
                            }
                        }
                    }

                    Cell<String> cell = (Cell<String>)column.getCell();

                    FieldUpdater<R, String> updater = (FieldUpdater<R, String>) column.getFieldUpdater();
                    for (int i = 0; i < Math.min(rows.size() - rowIndex, values.size()); i++) {
                        int j = i + rowIndex;
                        if (isColumnEditable(column, rows.get(j))) {
                            updater.update(j, rows.get(j), values.get(i));
                            ((AbstractEditableCell)cell).clearViewData(rows.get(j));
                        }
                    }
                    dataProvider.refresh();
                }
            }
        }
        return true;
    }

    public List<R> getRows() {
        return new ArrayList<>(dataProvider.getList());
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

    public void addNote(String note) {
        notePanel.setInnerHTML(note);
    }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
