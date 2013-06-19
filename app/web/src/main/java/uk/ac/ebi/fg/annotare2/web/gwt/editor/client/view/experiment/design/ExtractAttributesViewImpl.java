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

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.configmodel.ExtractAttribute;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExtractAttributeRow;

import java.util.Comparator;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.configmodel.ExtractAttribute.*;

/**
 * @author Olga Melnichuk
 */
public class ExtractAttributesViewImpl extends Composite implements ExtractAttributesView {

    private final GridView<ExtractAttributeRow> gridView;

    public ExtractAttributesViewImpl() {
        gridView = new GridView<ExtractAttributeRow>();
        initWidget(gridView);
    }

    @Override
    public void setData(List<ExtractAttributeRow> rows) {
        gridView.setRows(rows);
        setColumns();
    }

    private void setColumns() {
        addNameColumn();
        addColumn(LIBRARY_LAYOUT);
        addColumn(LIBRARY_SOURCE);
        addColumn(LIBRARY_STRATEGY);
        addColumn(LIBRARY_SELECTION);
    }

    private void addNameColumn() {
        Column<ExtractAttributeRow, String> column = new Column<ExtractAttributeRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ExtractAttributeRow row) {
                return row.getName();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ExtractAttributeRow, String>() {
            @Override
            public void update(int index, ExtractAttributeRow row, String value) {
                // TODO check names are unique
                row.setName(value);
                updateRow(row);
            }
        });
        column.setSortable(true);
        Comparator<ExtractAttributeRow> comparator = new Comparator<ExtractAttributeRow>() {
            @Override
            public int compare(ExtractAttributeRow o1, ExtractAttributeRow o2) {
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

    private void addColumn(final ExtractAttribute attr) {
        Column<ExtractAttributeRow, String> column = new Column<ExtractAttributeRow, String>(new SelectionCell(attr.getOptions())) {
            @Override
            public String getValue(ExtractAttributeRow row) {
                String v = row.getValue(attr);
                return v == null ? "" : v;
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<ExtractAttributeRow, String>() {
            @Override
            public void update(int index, ExtractAttributeRow row, String value) {
                row.setValue(value, attr);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX);
    }

    private void updateRow(ExtractAttributeRow row) {
        //TODO
    }
}
