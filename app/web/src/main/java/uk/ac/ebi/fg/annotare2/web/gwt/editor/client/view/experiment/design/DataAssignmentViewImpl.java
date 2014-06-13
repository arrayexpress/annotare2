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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DynSelectionCell;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentViewImpl extends Composite implements DataAssignmentView {

    public static final String NONE = "none";
    private final GridView<DataAssignmentRow> gridView;
    private Map<FileType, List<DataAssignmentColumn>> columns = new HashMap<FileType, List<DataAssignmentColumn>>();
    private Map<String, String> fileHashes = new HashMap<String, String>();
    private DataAssignment dataAssignment = new DataAssignment();
    private Presenter presenter;

    public DataAssignmentViewImpl() {
        gridView = new GridView<DataAssignmentRow>();

        Button button = new Button("Assign files...");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new AddColumnDialog<ColumnType>(new DialogCallback<ColumnType>() {
                    @Override
                    public void onOkay(ColumnType columnType) {
                        createColumn(columnType.getType());
                    }
                }, getAllowedColumnTypes());
            }
        });
        gridView.addTool(button);

        button = new Button("Delete Column(s)...");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new RemoveColumnsDialog(new DialogCallback<List<Integer>>() {
                    @Override
                    public void onOkay(List<Integer> columns) {
                        removeColumns(columns);
                    }
                }, getDataFileColumnNames());
            }
        });
        gridView.addTool(button);

        button = new Button("Fill Down Value");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fillDownValue();
            }
        });
        gridView.addTool(button);

        initWidget(gridView);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setData(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows) {
        gridView.clearAllColumns();
        gridView.setRows(rows);
        setColumns(columns);
        dataAssignment.init(columns, rows);
    }

    @Override
    public void setDataFiles(List<DataFileRow> dataFiles) {
        updateFileHashes(dataFiles);
        dataAssignment.init(dataFiles);
        gridView.redraw();
    }

    private void updateFileHashes(List<DataFileRow> dataFiles) {
        fileHashes.clear();
        for (DataFileRow row : dataFiles) {
            if (row.getStatus().isOk()) {
                fileHashes.put(row.getName(), row.getMd5());
            }
        }
    }

    private void setColumns(List<DataAssignmentColumn> columns) {
        this.columns.clear();
        for (DataAssignmentColumn column : columns) {
            FileType type = column.getType();
            List<DataAssignmentColumn> list = this.columns.get(type);
            if (list == null) {
                list = new ArrayList<DataAssignmentColumn>();
                this.columns.put(type, list);
            }
            list.add(column);
        }

        addNameColumn();

        for (DataAssignmentColumn column : getColumns()) {
            addDataFileColumn(column, getDataFileColumnName(column));
        }
    }

    private List<DataAssignmentColumn> getColumns() {
        List<DataAssignmentColumn> columns = new ArrayList<DataAssignmentColumn>();
        for (FileType type : FileType.values()) {
            List<DataAssignmentColumn> list = this.columns.get(type);
            if (list == null) {
                continue;
            }
            for (DataAssignmentColumn column : list) {
                columns.add(column);
            }
        }
        return columns;
    }

    private String getDataFileColumnName(DataAssignmentColumn column) {
        FileType type = column.getType();
        int index = columns.get(type).indexOf(column) + 1;
        return type.getTitle() + " Data File" + (index > 1 ? " (" + index + ")" : "");
    }

    private List<ColumnType> getAllowedColumnTypes() {
        List<ColumnType> types = new ArrayList<ColumnType>();
        for (FileType type : FileType.values()) {
            if (!type.isFGEM() || 0 == columns.get(type).size()) {
                types.add(new ColumnType(type));
            }
        }
        return types;
    }

    private List<String> getDataFileColumnNames() {
        List<String> names = new ArrayList<String>();
        for (DataAssignmentColumn column : getColumns()) {
            names.add(getDataFileColumnName(column));
        }
        return names;
    }

    private void addDataFileColumn(final DataAssignmentColumn dataColumn, String columnName) {
        Column<DataAssignmentRow, String> column = new Column<DataAssignmentRow, String>(
                new DynSelectionCell(
                        new DynSelectionCell.ListProvider() {
                            @Override
                            public List<String> getOptions() {
                                return dataAssignment.getOptions(dataColumn);
                            }

                            @Override
                            public String getDefault() {
                                return NONE;
                            }
                        })) {
            @Override
            public String getValue(DataAssignmentRow row) {
                FileRef file = dataColumn.getFileRef(row);
                return null == file ? NONE : file.getName();
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<DataAssignmentRow, String>() {
            @Override
            public void update(int index, DataAssignmentRow row, String value) {
                dataColumn.setFileRef(row, NONE.equals(value) ? null : new FileRef(value, fileHashes.get(value)));
                dataAssignment.update(dataColumn);
                updateColumn(dataColumn);
                gridView.redraw();
            }
        });
        gridView.addColumn(columnName, column, null, 200, Style.Unit.PX);
    }

    private void addNameColumn() {
        Column<DataAssignmentRow, String> column = new Column<DataAssignmentRow, String>(new TextCell()) {
            @Override
            public String getValue(DataAssignmentRow row) {
                return row.getName();
            }
        };
        column.setSortable(true);
        Comparator<DataAssignmentRow> comparator = new Comparator<DataAssignmentRow>() {
            @Override
            public int compare(DataAssignmentRow o1, DataAssignmentRow o2) {
                if (o1 == o2) {
                    return 0;
                }
                String v1 = o1.getName();
                String v2 = o2.getName();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Name", column, comparator, 150, Style.Unit.PX);
    }

    private void removeColumns(List<Integer> columnPositions) {
        if (presenter == null) {
            return;
        }
        List<DataAssignmentColumn> columns = getColumns();
        List<Integer> indices = new ArrayList<Integer>();
        for(Integer position : columnPositions) {
            indices.add(columns.get(position).getIndex());
        }
        presenter.removeColumns(indices);
    }

    private void createColumn(FileType type) {
        if (presenter != null) {
            presenter.createColumn(type);
        }
    }

    private void updateColumn(DataAssignmentColumn column) {
        if (presenter != null) {
            presenter.updateColumn(column);
        }
    }

    private void fillDownValue() {
        gridView.fillDownKeyboardSelectedColumn();
    }

    private static class ColumnType {

        private final FileType type;

        private ColumnType(FileType type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type.getTitle();
        }

        public FileType getType() {
            return type;
        }
    }

    private static class DataAssignment {

        private Map<String, Integer> file2Column = new HashMap<String, Integer>();
        private Map<Integer, List<String>> column2Files = new HashMap<Integer, List<String>>();
        private List<String> files = new ArrayList<String>();

        public void init(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows) {
            file2Column.clear();
            column2Files.clear();
            for (DataAssignmentColumn column : columns) {
                for (DataAssignmentRow row : rows) {
                    FileRef file = column.getFileRef(row);
                    if (null != file && null != file.getName()) {
                        add(file.getName(), column);
                    }
                }
            }
        }

        public void init(List<DataFileRow> dataFiles) {
            files.clear();
            for (DataFileRow row : dataFiles) {
                if (row.getStatus().isOk()) {
                    files.add(row.getName());
                }
            }
        }

        private void add(String fileName, DataAssignmentColumn column) {
            int colIndex = column.getIndex();
            file2Column.put(fileName, colIndex);
            List<String> list = column2Files.get(colIndex);
            if (list == null) {
                list = new ArrayList<String>();
                column2Files.put(colIndex, list);
            }
            list.add(fileName);
        }

        private void remove(DataAssignmentColumn column) {
            int colIndex = column.getIndex();
            List<String> fileNames = column2Files.remove(colIndex);
            if (fileNames == null) {
                return;
            }
            for (String fileName : fileNames) {
                file2Column.remove(fileName);
            }
        }

        public List<String> getOptions(DataAssignmentColumn dataColumn) {
            List<String> options = new ArrayList<String>();
            for (String file : files) {
                Integer colIndex = file2Column.get(file);
                if (colIndex == null || colIndex == dataColumn.getIndex()) {
                    options.add(file);
                }
            }
            return options;
        }

        public void update(DataAssignmentColumn column) {
            remove(column);
            for (FileRef file : column.getFileRefs()) {
                add(file.getName(), column);
            }
        }
    }
}
