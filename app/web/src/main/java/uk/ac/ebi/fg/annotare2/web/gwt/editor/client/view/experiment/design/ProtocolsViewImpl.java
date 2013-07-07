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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;

import java.util.Comparator;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ProtocolsViewImpl extends Composite implements ProtocolsView {

    private GridView<ProtocolRow> gridView;

    private Presenter presenter;

    public ProtocolsViewImpl() {
        gridView = new GridView<ProtocolRow>();
        Button createButton = new Button("Add Protocol");
        createButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (presenter != null) {
                    (new ProtocolCreateDialog(presenter)).show();
                }
            }
        });
        gridView.addTool(createButton);
        Button removeButton = new Button("Remove Selected");
        gridView.addTool(removeButton);
        initWidget(gridView);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setData(List<ProtocolRow> rows) {
        gridView.setRows(rows);
        setColumns();
    }

    private void setColumns() {
        addNameColumn();
        addTypeColumn();
    }

    private void addNameColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                return row.getName();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                // TODO check names are unique
                row.setName(value);
                updateRow(row);
            }
        });
        column.setSortable(true);
        Comparator<ProtocolRow> comparator = new Comparator<ProtocolRow>() {
            @Override
            public int compare(ProtocolRow o1, ProtocolRow o2) {
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

    private void addTypeColumn() {
        //TODO
    }

    private void updateRow(ProtocolRow row) {
        //TODO
    }
}
