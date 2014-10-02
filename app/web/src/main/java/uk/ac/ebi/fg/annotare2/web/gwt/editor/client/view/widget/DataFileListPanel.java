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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CheckboxHeader;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.CustomDataGrid;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class DataFileListPanel extends SimpleLayoutPanel {

    private final DataGrid<DataFileRow> grid;
    private final ListDataProvider<DataFileRow> dataProvider;
    private final MultiSelectionModel<DataFileRow> selectionModel;
    private final CheckboxHeader checkboxHeader;

    private final static int MAX_FILES = 40000;

    private Presenter presenter;

    public DataFileListPanel() {
        grid = new CustomDataGrid<DataFileRow>(MAX_FILES, false);
        grid.addStyleName("gwt-dataGrid");
        grid.setWidth("100%");
        grid.setHeight("100%");

        selectionModel =
                new MultiSelectionModel<DataFileRow>(new ProvidesKey<DataFileRow>() {
                    @Override
                    public Object getKey(DataFileRow item) {
                        return item.getIdentity();
                    }
                });
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<DataFileRow>createCheckboxManager());
        Column<DataFileRow, Boolean> checkboxColumn = new Column<DataFileRow, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(DataFileRow object) {
                return grid.getSelectionModel().isSelected(object);
            }
        };

        checkboxHeader = new CheckboxHeader();
        checkboxHeader.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                selectAllRows(event.getValue());
            }
        });

        grid.addColumn(checkboxColumn, checkboxHeader);
        grid.setColumnWidth(checkboxColumn, 40, Style.Unit.PX);

        final EditTextCell nameCell = new EditTextCell();
        Column<DataFileRow, String> nameColumn = new Column<DataFileRow, String>(nameCell) {
            @Override
            public String getValue(DataFileRow row) {
                return row.getName();
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<DataFileRow, String>() {
            @Override
            public void update(int index, DataFileRow row, String value) {
                if (isNameValid(value, index)) {
                    presenter.renameFile(row, value);
                } else {
                    nameCell.clearViewData(row);
                    grid.redraw();
                }
            }
        });
        grid.addColumn(nameColumn, "Name");

        Column<DataFileRow, Date> dateColumn = new Column<DataFileRow, Date>(new DateCell(DateTimeFormat.getFormat("dd/MM/yy HH:mm"))) {
            @Override
            public Date getValue(DataFileRow object) {
                return object.getCreated();
            }
        };
        grid.addColumn(dateColumn, "Date");
        grid.setColumnWidth(dateColumn, 110, Style.Unit.PX);

        Column<DataFileRow, String> statusText = new Column<DataFileRow, String>(new TextCell()) {
            @Override
            public String getValue(DataFileRow object) {
                return object.getStatus().getTitle();
            }
        };
        statusText.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        grid.addColumn(statusText, "Status");
        grid.setColumnWidth(statusText, 100, Style.Unit.PX);

        dataProvider = new ListDataProvider<DataFileRow>();
        dataProvider.addDataDisplay(grid);

        grid.setLoadingIndicator(new LoadingIndicator());
        grid.setEmptyTableWidget(new Label("No files uploaded"));
        add(grid);
    }

    public void addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
        selectionModel.addSelectionChangeHandler(handler);
    }

    public Set<DataFileRow> getSelectedRows() {
        return selectionModel.getSelectedSet();
    }
    public void setRows(List<DataFileRow> rows) {
        dataProvider.setList(new ArrayList<DataFileRow>(rows));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void deleteSelectedFiles(final AsyncCallback<Void> callback) {
        final Set<DataFileRow> selection = getSelectedRows();
        if (selection.isEmpty()) {
            Window.alert("Please select files you want to delete first");
        } else if (Window.confirm("The selected files will no longer be available to assign if you delete. Do you want to continue?")) {
            presenter.removeFiles(selection, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Unable to delete files");
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(Void result) {
                    checkboxHeader.setValue(false);
                    selectionModel.clear();
                    callback.onSuccess(result);
                }
            });
        }
    }

    private boolean isNameValid(String name, int rowIndex) {
        if (name == null || name.trim().isEmpty()) {
            Window.alert("File name should not be empty");
            return false;
        }

        if (!name.matches("^[_a-zA-Z0-9\\-\\.]+$")) {
            Window.alert("File name should only contain alphanumeric characters, underscores and dots");
            return false;
        }

        if (isDuplicated(name, rowIndex)) {
            Window.alert("File with the name '" + name + "' already exists");
            return false;
        }
        return true;
    }

    private void selectAllRows(boolean selected) {
        List<DataFileRow> sublist = dataProvider.getList();
        for (DataFileRow row : sublist) {
            selectionModel.setSelected(row, selected);
        }
    }

    private boolean isDuplicated(String name, int rowIndex) {
        List<DataFileRow> rows = dataProvider.getList();
        int i = 0;
        for (DataFileRow row : rows) {
            if (i != rowIndex && name.equals(row.getName())) {
                return true;
            }
            i++;
        }
        return false;
    }

    public interface Presenter {
        void renameFile(DataFileRow dataFileRow, String newFileName);
        void removeFiles(Set<DataFileRow> dataFileRow, AsyncCallback<Void> callback);
    }
}
