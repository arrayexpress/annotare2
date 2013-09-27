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

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfileUpdates;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EditListCell;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ValidationMessage;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class ProtocolsViewImpl extends Composite implements ProtocolsView {

    private GridView<ProtocolRow> gridView;
    private ValidationMessage errorMessage;

    private Presenter presenter;

    public ProtocolsViewImpl() {
        gridView = new GridView<ProtocolRow>();
        Button createButton = new Button("Add Protocol...");
        createButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createProtocol();
            }
        });
        gridView.addTool(createButton);
        Button removeButton = new Button("Delete Selected Rows");
        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedProtocols();
            }
        });
        gridView.addTool(removeButton);
        errorMessage = new ValidationMessage();
        gridView.addTool(errorMessage);
        initWidget(gridView);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setData(List<ProtocolRow> rows) {
        gridView.clearAllColumns();
        gridView.setRows(rows);
        setColumns();
    }

    private void setColumns() {
        addNameColumn();
        addAssignmentColumn();
        addTypeColumn();
        addDescriptionColumn();
        addParametersColumn();
        addHardwareColumn();
        addSoftwareColumn();
        addContactColumn();
    }

    private void addNameColumn() {
        final EditTextCell nameCell = new EditTextCell();
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(nameCell) {
            @Override
            public String getValue(ProtocolRow row) {
                String v = row.getName();
                return v == null ? "" : v;
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                if (isNameValid(value, index)) {
                    row.setName(value);
                    updateRow(row);
                } else {
                    nameCell.clearViewData(row);
                    gridView.redraw();
                }
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

    private void addAssignmentColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new ButtonCell()) {
            @Override
            public String getValue(ProtocolRow object) {
                return "Assign...";
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                presenter.getAssignmentProfileAsync(row.getId(), new AsyncCallback<ProtocolAssignmentProfile>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Can't load protocol assignments");
                    }

                    @Override
                    public void onSuccess(ProtocolAssignmentProfile result) {
                        new ProtocolAssignmentDialog(result, new DialogCallback<ProtocolAssignmentProfileUpdates>() {
                            @Override
                            public void onCancel() {
                                // do nothing
                            }

                            @Override
                            public void onOkay(ProtocolAssignmentProfileUpdates profileUpdates) {
                                //TODO update protocol assignments
                            }
                        }).show();
                    }
                });
            }
        });
        gridView.addPermanentColumn("Assignment", column, null, 100, Style.Unit.PX);
    }

    private boolean isNameValid(String name, int rowIndex) {
        if (name == null || name.trim().isEmpty()) {
            showErrorMessage("Protocol name can't be empty");
            return false;
        }
        if (isDuplicated(name, rowIndex)) {
            showErrorMessage("Protocol with the name '" + name + "' already exists");
            return false;
        }
        return true;
    }

    private boolean isDuplicated(String name, int rowIndex) {
        List<ProtocolRow> rows = gridView.getRows();
        Set<String> names = new HashSet<String>();
        int i = 0;
        for (ProtocolRow row : rows) {
            if (i != rowIndex) {
                names.add(row.getName());
            }
            i++;
        }
        return names.contains(name);
    }

    private void showErrorMessage(String msg) {
        errorMessage.setMessage(msg);
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
                String v = row.getDescription();
                return v == null ? "" : v;
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
        Column<ProtocolRow, List<String>> column = new Column<ProtocolRow, List<String>>(new EditListCell()) {
            @Override
            public List<String> getValue(ProtocolRow row) {
                return row.getParameters();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, List<String>>() {
            @Override
            public void update(int index, ProtocolRow row, List<String> value) {
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
                String v = row.getHardware();
                return v == null ? "" : v;
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
                String v = row.getSoftware();
                return v == null ? "" : v;
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
                String v = row.getContact();
                return v == null ? "" : v;
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
        if (presenter != null) {
            presenter.updateUpdateProtocol(row);
        }
    }

    private void createProtocol() {
        if (presenter == null) {
            return;
        }
        (new ProtocolCreationDialog(presenter,
                new DialogCallback<ProtocolType>() {
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

    private void removeSelectedProtocols() {
        Set<ProtocolRow> selection = gridView.getSelectedRows();
        if (selection.isEmpty()) {
            return;
        }
        presenter.removeProtocols(new ArrayList<ProtocolRow>(selection));
        gridView.removeSelectedRows();
    }
}
