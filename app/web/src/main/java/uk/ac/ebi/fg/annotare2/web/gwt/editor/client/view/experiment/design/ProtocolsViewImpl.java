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

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.NotificationPopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfileUpdates;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.*;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class ProtocolsViewImpl extends Composite implements ProtocolsView, RequiresResize {

    private GridView<ProtocolRow> gridView;
    private AsyncOptionProvider sequencingHardware;

    private Presenter presenter;

    public ProtocolsViewImpl() {
        gridView = new GridView<ProtocolRow>();
        Button button = new Button("Add Protocol...");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createProtocol();
            }
        });
        gridView.addTool(button);

        button = new Button("Delete Protocols");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedProtocols();
            }
        });
        gridView.addTool(button);

        button = new Button();
        button.setHTML("&#8593;");
        button.setTitle("Move selected protocol one position up");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveProtocolUp();
            }
        });
        gridView.addTool(button);

        button = new Button();
        button.setHTML("&#8595;");
        button.setTitle("Move selected protocol one position down");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveProtocolDown();
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

        sequencingHardware = new AsyncOptionProvider() {
            private List<String> options = new ArrayList<String>();

            @Override
            public void update(final Callback callback) {
                if (presenter != null) {
                    if (options.isEmpty()) {
                        presenter.getSequencingHardwareAsync(new AsyncCallback<List<String>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                callback.setOptions(Collections.<String>emptyList());
                            }

                            @Override
                            public void onSuccess(List<String> result) {
                                if (!result.isEmpty()) {
                                    options.clear();
                                    options.add("");
                                    options.addAll(result);
                                    callback.setOptions(options);
                                }
                            }
                        });
                    } else {
                        callback.setOptions(options);
                    }
                }
            }
        };
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
        addPerformerColumn();
    }

    private void addNameColumn() {
        final EditSuggestCell nameCell = new EditSuggestCell(null) {
            @Override
            public boolean validateInput(String value, int rowIndex) {
                if (value == null || trimValue(value).isEmpty()) {
                    NotificationPopupPanel.error("Protocol with empty name is not permitted.", true);
                    return false;
                }
                if (!isNameUnique(value, rowIndex)) {
                    NotificationPopupPanel.error("Protocol with the name '" + value + "' already exists.", true);
                    return false;
                }
                return true;
            }
        };

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
                row.setName(trimValue(value));
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

    private void addAssignmentColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new ButtonCell() {
            @Override
            public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
                sb.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\">");
                sb.append(data);
                sb.appendHtmlConstant("</button>");
            }
        }) {
            @Override
            public String getValue(ProtocolRow row) {
                return "Assign...";
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                final boolean isRowAssignable = row.isAssignable();
                final String protocolType = row.getProtocolType().getLabel();
                presenter.getAssignmentProfileAsync(row.getId(),
                        new ReportingAsyncCallback<ProtocolAssignmentProfile>(FailureMessage.UNABLE_TO_LOAD_PROTOCOL_ASSIGNMENTS) {
                            @Override
                            public void onSuccess(ProtocolAssignmentProfile result) {
                                if (!isRowAssignable) {
                                    NotificationPopupPanel.warning("Protocol '" + protocolType + "' is always assigned to all " + result.getProtocolSubjectType() + "s.", true);
                                    return;
                                } else if (result.getNames().isEmpty()) {
                                    NotificationPopupPanel.warning("You do not have any " + result.getProtocolSubjectType() + "s to assign protocols to.", true);
                                    return;
                                }
                                new ProtocolAssignmentDialog(result, new DialogCallback<ProtocolAssignmentProfileUpdates>() {
                                    @Override
                                    public void onOkay(ProtocolAssignmentProfileUpdates profileUpdates) {
                                        updateProtocolAssignments(profileUpdates);
                                    }
                                }).show();
                            }
                        }
                );
            }
        });
        gridView.addPermanentColumn("Assign protocols to materials/data files", column, null, 300, Style.Unit.PX);
    }

    private boolean isNameUnique(String name, int rowIndex) {
        List<ProtocolRow> rows = gridView.getRows();
        for (int i = 0; i < rows.size(); i++) {
            if (i != rowIndex && rows.get(i).getName().equals(name)) {
                return false;
            }
        }
        return true;
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
        gridView.addPermanentColumn("Type", column, comparator, 300, Style.Unit.PX);
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
        Column<ProtocolRow, String> column = new ProtocolHardwareColumn(
                new EditTextCell(),
                new EditSelectionCell(sequencingHardware)
        ) {
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

    private void addPerformerColumn() {
        Column<ProtocolRow, String> column = new Column<ProtocolRow, String>(new EditTextCell()) {
            @Override
            public String getValue(ProtocolRow row) {
                String v = row.getPerformer();
                return v == null ? "" : v;
            }
        };
        column.setFieldUpdater(new FieldUpdater<ProtocolRow, String>() {
            @Override
            public void update(int index, ProtocolRow row, String value) {
                row.setPerformer(value);
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
                String v1 = o1.getPerformer();
                String v2 = o2.getPerformer();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Performer", column, comparator, 150, Style.Unit.PX);
    }

    private void updateProtocolAssignments(ProtocolAssignmentProfileUpdates updates) {
        if (presenter != null) {
            presenter.updateProtocolAssignments(updates);
        }
    }

    private void updateRow(ProtocolRow row) {
        if (presenter != null) {
            presenter.updateProtocol(row);
        }
    }

    private void createProtocol() {
        if (presenter == null) {
            return;
        }
        (new AddProtocolDialog(presenter,
                new DialogCallback<ProtocolType>() {
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
            NotificationPopupPanel.warning("Please select protocols you want to delete.", true);
        } else if (Window.confirm("The selected protocols will no longer be available if you delete. Do you want to continue?")) {
            presenter.removeProtocols(new ArrayList<ProtocolRow>(selection));
        }
    }

    private void moveProtocolUp() {
        ProtocolRow row = getSelectedProtocolRowToMove();
        if (gridView.moveRowUp(row)) {
            presenter.moveProtocolUp(row);
        }
    }

    private void moveProtocolDown() {
        ProtocolRow row = getSelectedProtocolRowToMove();
        if (gridView.moveRowDown(row)) {
            presenter.moveProtocolDown(row);
        }
    }

    private ProtocolRow getSelectedProtocolRowToMove() {
        Set<ProtocolRow> selected = gridView.getSelectedRows();
        if (selected.isEmpty()) {
            NotificationPopupPanel.warning("Please select a row to move.", true);
            return null;
        }
        if (selected.size() > 1) {
            NotificationPopupPanel.error("Unable to move more than one row at a time.", true);
            return null;
        }
        return selected.iterator().next();
    }

    private void fillDownValue() {
        gridView.fillDownKeyboardSelectedColumn();
    }

    private String trimValue(String value) {
        if (null != value) {
            value = value.replaceAll("([^\\t]*)[\\t].*", "$1").trim();
        }
        return value;
    }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
