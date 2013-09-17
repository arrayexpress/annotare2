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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.configmodel.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SingleSelectionCell;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentViewImpl extends Composite implements DataAssignmentView {

    private final GridView<DataAssignmentRow> gridView;
    private Map<FileType, List<DataAssignmentColumn>> columns = new HashMap<FileType, List<DataAssignmentColumn>>();
    private DataAssignment dataAssignment = new DataAssignment();
    private Presenter presenter;

    public DataAssignmentViewImpl() {
        gridView = new GridView<DataAssignmentRow>();

        Button addColumnButton = new Button("Add Column");
        addColumnButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new AddColumnDialog<ColumnType>(new DialogCallback<ColumnType>() {
                    @Override
                    public void onCancel() {
                        // do nothing
                    }

                    @Override
                    public void onOkay(ColumnType columnType) {
                        createColumn(columnType.getType());
                    }
                }, ColumnType.values());
            }
        });
        gridView.addTool(addColumnButton);

        Button removeColumnsButton = new Button("Delete Column(s)");
        removeColumnsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new RemoveColumnsDialog(new DialogCallback<List<Integer>>() {
                    @Override
                    public void onCancel() {
                        // do nothing
                    }

                    @Override
                    public void onOkay(List<Integer> columns) {
                        removeColumns(columns);
                    }
                }, getDataFileColumnNames());
            }
        });
        gridView.addTool(removeColumnsButton);

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
        dataAssignment.init(dataFiles);
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
        return type.getTitle() + " Data File (" + index + ")";
    }

    private List<String> getDataFileColumnNames() {
        List<String> names = new ArrayList<String>();
        for (DataAssignmentColumn column : getColumns()) {
            names.add(getDataFileColumnName(column));
        }
        return names;
    }

    private void addDataFileColumn(final DataAssignmentColumn dataColumn, String columnName) {
        Column<DataAssignmentRow, Long> column = new Column<DataAssignmentRow, Long>(
                new SingleSelectionCell<Long>(
                        new SingleSelectionCell.ListProvider<Long>() {
                            @Override
                            public List<SingleSelectionCell.Option<Long>> getOptions() {
                                return dataAssignment.getOptions(dataColumn);
                            }
                        })) {
            @Override
            public Long getValue(DataAssignmentRow row) {
                Long fileId = dataColumn.getFileId(row);
                return fileId == null ? 0L : fileId;
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<DataAssignmentRow, Long>() {
            @Override
            public void update(int index, DataAssignmentRow row, Long value) {
                dataColumn.setFileId(row, value == 0L ? null : value);
                dataAssignment.update(dataColumn);
                //updateRow(row);
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

        public static List<ColumnType> values() {
            List<ColumnType> values = new ArrayList<ColumnType>();
            for (FileType type : FileType.values()) {
                values.add(new ColumnType(type));
            }
            return values;
        }
    }

    private static class DataAssignment {

        private Map<Long, Integer> file2Column = new HashMap<Long, Integer>();
        private Map<Integer, List<Long>> column2Files = new HashMap<Integer, List<Long>>();
        private List<SingleSelectionCell.Option<Long>> files = new ArrayList<SingleSelectionCell.Option<Long>>();

        public void init(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows) {
            file2Column.clear();
            column2Files.clear();
            for (DataAssignmentColumn column : columns) {
                for (DataAssignmentRow row : rows) {
                    Long fileId = column.getFileId(row);
                    if (fileId != null) {
                        add(fileId, column);
                    }
                }
            }
        }

        private void add(Long fileId, DataAssignmentColumn column) {
            int colIndex = column.getIndex();
            file2Column.put(fileId, colIndex);
            List<Long> list = column2Files.get(colIndex);
            if (list == null) {
                list = new ArrayList<Long>();
                column2Files.put(colIndex, list);
            }
            list.add(fileId);
        }

        private void remove(DataAssignmentColumn column) {
            int colIndex = column.getIndex();
            List<Long> fileIds = column2Files.remove(colIndex);
            if (fileIds == null) {
                return;
            }
            for (Long fileId : fileIds) {
                file2Column.remove(fileId);
            }
        }

        public void init(List<DataFileRow> dataFiles) {
            files.clear();
            for (DataFileRow row : dataFiles) {
                if (row.getStatus().isOk()) {
                    files.add(new SingleSelectionCell.Option<Long>(row.getId(), row.getName()));
                }
            }
        }

        public List<SingleSelectionCell.Option<Long>> getOptions(DataAssignmentColumn dataColumn) {
            List<SingleSelectionCell.Option<Long>> options = new ArrayList<SingleSelectionCell.Option<Long>>();
            options.add(new SingleSelectionCell.Option<Long>(0L, "none"));
            for (SingleSelectionCell.Option<Long> option : files) {
                Integer colIndex = file2Column.get(option.getValue());
                if (colIndex == null || colIndex == dataColumn.getIndex()) {
                    options.add(option);
                }
            }
            return options;
        }

        public void update(DataAssignmentColumn column) {
            remove(column);
            for (Long fileId : column.getFileIds()) {
                add(fileId, column);
            }
        }
    }
}
