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
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;

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
                createProtocol();
            }
        });
        gridView.addTool(createButton);
        Button removeButton = new Button("Delete Selected Rows");
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
        addDescriptionColumn();
        addParametersColumn();
        addHardwareColumn();
        addSoftwareColumn();
        addContactColumn();
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
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new TextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                return row.getProtocolType().getLabel();
            }
        };
        column.setSortable(true);
        Comparator<ProtocolRow> comparator = new Comparator<ProtocolRow>() {
            @Override
            public int compare(ProtocolRow o1, ProtocolRow o2) {
                if (o1 == o2) {
                    return 0;
                }
                String v1 = o1.getProtocolType().getLabel();
                String v2 = o2.getProtocolType().getLabel();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Type", column, comparator, 150, Style.Unit.PX);
    }

    private void addDescriptionColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                return row.getDescription();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                row.setDescription(value);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn("Description", column, null, 150, Style.Unit.PX);
    }

    private void addParametersColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                return row.getParameters();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                row.setParameters(value);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn("Parameters", column, null, 150, Style.Unit.PX);
    }

    private void addHardwareColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                return row.getHardware();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                row.setHardware(value);
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
                String v1 = o1.getHardware();
                String v2 = o2.getHardware();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Hardware", column, comparator, 150, Style.Unit.PX);
    }

    private void addSoftwareColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                return row.getSoftware();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                row.setSoftware(value);
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
                String v1 = o1.getSoftware();
                String v2 = o2.getSoftware();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Software", column, comparator, 150, Style.Unit.PX);
    }

    private void addContactColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                return row.getContact();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                row.setContact(value);
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
                String v1 = o1.getContact();
                String v2 = o2.getContact();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Contact", column, comparator, 150, Style.Unit.PX);
    }

    private void updateRow(ProtocolRow row) {
        //TODO
    }

    private void createProtocol() {
        if (presenter == null) {
            return;
        }
        (new ProtocolCreateDialog(presenter,
                new ProtocolCreateDialog.Callback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onOkay(ProtocolType protocolType) {
                        createProtocol(protocolType);
                    }
                })).show();
    }

    private void createProtocol(ProtocolType protocolType) {
        presenter.createProtocol(protocolType);
    }
}
