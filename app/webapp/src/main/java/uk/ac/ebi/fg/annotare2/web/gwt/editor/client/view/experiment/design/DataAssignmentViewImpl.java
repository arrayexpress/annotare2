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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import uk.ac.ebi.fg.annotare2.submission.model.EnumWithHelpText;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DynSelectionCell;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentViewImpl extends Composite implements DataAssignmentView, RequiresResize {

    public static final String NONE = "none";

    private final GridView<DataAssignmentRow> gridView;
    private List<DataAssignmentColumn> columns = new ArrayList<DataAssignmentColumn>();

    private DataAssignment dataAssignment = new DataAssignment();
    private ExperimentProfileType experimentType;
    private Presenter presenter;

    public DataAssignmentViewImpl() {
        gridView = new GridView<>();
        gridView.setRowSelectionEnabled(false);

        Button button = new Button("Assign Files...");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new AssignFilesDialog(new DialogCallback<FileType>() {
                    @Override
                    public boolean onOk(FileType type) {
                        createColumn(type);
                        return true;
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
                    public boolean onOk(List<Integer> columns) {
                        removeColumns(columns);
                        return true;
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
        button.setTitle("Copy the selected value into all the rows below");
        gridView.addTool(button);

        button = new Button("Import Values");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                importValues();
            }
        });
        button.setTitle("Use this feature to insert/paste a column of values from your spreadsheet into Annotare (from the selected cell downward)");
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
    public void updateData(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows) {
        this.columns = columns;
        dataAssignment.init(columns, rows);
    }

    @Override
    public void setDataFiles(List<DataFileRow> dataFiles) {
        dataAssignment.init(dataFiles);
        gridView.redraw();
    }

    @Override
    public void setExperimentType(ExperimentProfileType type) {
        experimentType = type;
    }

    private void setColumns(List<DataAssignmentColumn> columns) {
        this.columns = columns;
        addNameColumn();

        Map<FileType, Integer> columnsByType = new HashMap<FileType, Integer>();
        for (int index = 0; index < columns.size(); index++) {
            FileType type = columns.get(index).getType();
            columnsByType.put(type, (columnsByType.containsKey(type) ? columnsByType.get(type) : 0) + 1);
            addDataFileColumn(index, getDataFileColumnName(type, columnsByType.get(type)));
        }
    }

    private int countColumnsByType(FileType type) {
        int count = 0;
        for (DataAssignmentColumn c : columns) {
            if (c.getType() == type) {
                count++;
            }
        }
        return count;
    }

    private String getDataFileColumnName(FileType type, int index) {
        return type.getTitle() + " Data File" + (index > 1 ? " (" + index + ")" : "");
    }

    private List<EnumWithHelpText> getAllowedColumnTypes() {
        List<EnumWithHelpText> types = new ArrayList<>();
        for (FileType type : FileType.values()) {
            if (ExperimentProfileType.SEQUENCING == experimentType) {
                if (!type.isFGEM() || type.isProcessed() && (0 == countColumnsByType(type))) {
                    types.add(type);
                }
            } else if (0 == countColumnsByType(type)) {
                types.add(type);
            }
        }
        return types;
    }

    private List<String> getDataFileColumnNames() {
        List<String> names = new ArrayList<String>();
        Map<FileType, Integer> columnsByType = new HashMap<FileType, Integer>();
        for (DataAssignmentColumn column : columns) {
            FileType type = column.getType();
            columnsByType.put(type, (columnsByType.containsKey(type) ? columnsByType.get(type) : 0) + 1);
            names.add(getDataFileColumnName(type, columnsByType.get(type)));
        }
        return names;
    }

    private void addDataFileColumn(final int columnIndex, String columnName) {
        Column<DataAssignmentRow, String> column = new Column<DataAssignmentRow, String>(
                new DynSelectionCell<String>(
                        new DynSelectionCell.ListProvider<String>() {
                            @Override
                            public List<DynSelectionCell.Option<String>> getOptions() {
                                return dataAssignment.getOptions(columns.get(columnIndex));
                            }

                            @Override
                            public DynSelectionCell.Option<String> getDefault() {
                                return new DynSelectionCell.Option<String>() {
                                    @Override
                                    public String getValue() {
                                        return NONE;
                                    }

                                    @Override
                                    public String getText() {
                                        return NONE;
                                    }
                                };
                            }
                        })) {
            @Override
            public String getValue(DataAssignmentRow row) {
                FileRef file = columns.get(columnIndex).getFileRef(row);
                return null == file ? NONE : file.getName();
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<DataAssignmentRow, String>() {
            @Override
            public void update(int index, DataAssignmentRow row, String value) {
                columns.get(columnIndex).setFileRef(row, NONE.equals(value) ? null : dataAssignment.getFileRef(value));
                dataAssignment.update(columns.get(columnIndex));
                updateColumn(columns.get(columnIndex));
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

    private void importValues() {
        new ImportValuesDialog(
                new DialogCallback<List<String>>() {
                    @Override
                    public boolean onOk(List<String> values) {
                        return gridView.importValuesToKeyboardSelectedColumn(values);
                    }
                });
    }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }


    private static class DataAssignment {

        private Map<FileRef, Integer> file2Column = new HashMap<FileRef, Integer>();
        private Map<Integer, List<FileRef>> column2Files = new HashMap<Integer, List<FileRef>>();
        private List<FileRef> files = new ArrayList<FileRef>();

        public void init(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows) {
            file2Column.clear();
            column2Files.clear();
            for (DataAssignmentColumn column : columns) {
                for (DataAssignmentRow row : rows) {
                    FileRef file = column.getFileRef(row);
                    if (null != file && null != file.getName()) {
                        add(file, column);
                    }
                }
            }
        }

        public void init(List<DataFileRow> dataFiles) {
            files.clear();
            for (DataFileRow row : dataFiles) {
                if (row.getStatus().isOk()) {
                    files.add(new FileRef(row.getName(), row.getMd5()));
                }
            }
        }

        private void add(FileRef file, DataAssignmentColumn column) {
            int colIndex = column.getIndex();
            file2Column.put(file, colIndex);
            List<FileRef> list = column2Files.get(colIndex);
            if (list == null) {
                list = new ArrayList<FileRef>();
                column2Files.put(colIndex, list);
            }
            list.add(file);
        }

        private void remove(DataAssignmentColumn column) {
            int colIndex = column.getIndex();
            List<FileRef> files = column2Files.remove(colIndex);
            if (null != files) {
                for (FileRef file : files) {
                    file2Column.remove(file);
                }
            }
        }

        public List<DynSelectionCell.Option<String>> getOptions(DataAssignmentColumn dataColumn) {
            List<DynSelectionCell.Option<String>> options = new ArrayList<DynSelectionCell.Option<String>>();
            for (final FileRef file : files) {
                Integer colIndex = file2Column.get(file);
                if (colIndex == null || colIndex == dataColumn.getIndex()) {
                    options.add(new DynSelectionCell.Option<String>() {
                        @Override
                        public String getValue() {
                            return file.getName();
                        }

                        @Override
                        public String getText() {
                            return file.getName();
                        }
                    });
                }
            }
            return options;
        }

        public FileRef getFileRef(String name) {
            for (FileRef file : files) {
                if (file.getName().equals(name)) {
                    return file;
                }
            }
            return null;
        }

        public void update(DataAssignmentColumn column) {
            remove(column);
            for (FileRef file : column.getFileRefs()) {
                add(file, column);
            }
        }
    }
}
