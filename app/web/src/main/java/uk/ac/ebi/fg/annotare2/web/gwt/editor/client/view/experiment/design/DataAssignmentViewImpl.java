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
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentRow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataAssignmentViewImpl extends Composite implements DataAssignmentView {

    private final GridView<DataAssignmentRow> gridView;
    private static List<String> options = new ArrayList<String>();
    static {
        options.add("");
        options.add("data.raw.1.zip");
        options.add("data.raw.2.zip");
        options.add("data.raw.3.zip");
    }

    public DataAssignmentViewImpl() {
        gridView = new GridView<DataAssignmentRow>();
        initWidget(gridView);
    }

    @Override
    public void setRows(List<DataAssignmentRow> rows) {
        gridView.setRows(rows);
        setColumns();
    }

    private void setColumns() {
        addNameColumn();
        addRawDataFileColumn(options);
        addProcessedDataFileColumn(options);
    }

    private void addRawDataFileColumn(List<String> options) {
        Column<DataAssignmentRow, String> column = new Column<DataAssignmentRow, String>(new SelectionCell(options)) {
            @Override
            public String getValue(DataAssignmentRow row) {
                return "";
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<DataAssignmentRow, String>() {
            @Override
            public void update(int index, DataAssignmentRow row, String value) {
                //row.setValue(value);
                //updateRow(row);
            }
        });
        gridView.addPermanentColumn("Raw Data File", column, null, 150, Style.Unit.PX);
    }

    private void addProcessedDataFileColumn(List<String> options) {
        Column<DataAssignmentRow, String> column = new Column<DataAssignmentRow, String>(new SelectionCell(options)) {
            @Override
            public String getValue(DataAssignmentRow row) {
                return "";
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<DataAssignmentRow, String>() {
            @Override
            public void update(int index, DataAssignmentRow row, String value) {
                //row.setValue(value);
                //updateRow(row);
            }
        });
        gridView.addPermanentColumn("Processed Data File", column, null, 150, Style.Unit.PX);
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

}
