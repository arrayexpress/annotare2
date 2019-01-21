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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.EditSuggestCell;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.AsyncOptionProvider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EditSelectionCell;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class SamplesViewImpl extends Composite implements SamplesView, RequiresResize {

    private static final int COLUMN_WIDTH = 200;

    private final GridView<SampleRow> gridView;

    private List<SampleColumn> columns = new ArrayList<>();
    private AsyncOptionProvider materialTypes;
    private int maxSamplesLimit;

    private Presenter presenter;

    public SamplesViewImpl() {
        maxSamplesLimit = 1000;

        gridView = new GridView<>();
        Button button = new Button("Add Sample Attributes and Variables *");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new SampleColumnsDialog(columns,
                        presenter.getEfoTerms(),
                        presenter.getExperimentDesigns(),
                        presenter.getExperimentProfileType(),
                        new DialogCallback<List<SampleColumn>>() {
                            @Override
                            public boolean onOk(List<SampleColumn> columns) {
                                updateColumns(columns);
                                return true;
                            }
                        });
            }
        });
        button.setTitle("Add sample attributes for the biological starting materials (e.g. cells, tissues) and mark the experimental variables (attributes) that are subject of investigation");
        gridView.addTool(button);

        button = new Button("Add Samples");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createSamples();
            }
        });
        gridView.addTool(button);

        button = new Button("Delete Samples");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                deleteSelectedSamples();
            }
        });
        button.setTitle(" Select samples to delete, then click here");
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

        button = new Button("Paste Into Column");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                importValues();
            }
        });
        button.setTitle("Use this feature to insert/paste a column of values  from your spreadsheet into Annotare (from the selected cell downward)");
        gridView.addTool(button);

        initWidget(gridView);

        materialTypes = new AsyncOptionProvider() {
            private List<String> options = new ArrayList<>();

            @Override
            public void update(final Callback callback) {
                if (presenter != null) {
                    if (options.isEmpty()) {
                        presenter.getMaterialTypesAsync(new AsyncCallback<ArrayList<String>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                callback.setOptions(Collections.<String>emptyList());
                            }

                            @Override
                            public void onSuccess(ArrayList<String> result) {
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
    public void setData(List<SampleRow> rows, List<SampleColumn> columns) {
        gridView.clearAllColumns();
        gridView.setRows(rows);
        setColumns(columns);

        materialTypes.update();
    }

    @Override
    public void setExperimentType(ExperimentProfileType type) {
        if (type.isTwoColorMicroarray() || type.isMethylationMicroarray()) {
            maxSamplesLimit = 500;
        }
    }

    private void setColumns(List<SampleColumn> columns) {
        this.columns = new ArrayList<>(columns);
        addNameColumn();
        for (SampleColumn column : columns) {
            addColumn(column);
        }
    }

    private void updateColumns(List<SampleColumn> newColumns) {
        presenter.updateColumns(newColumns);
    }

    private void updateRow(SampleRow row) {
        presenter.updateRow(row.copy());
    }

    private void createSamples() {
        new AddSamplesDialog(new DialogCallback<AddSamplesDialog.Result>() {
            @Override
            public boolean onOk(AddSamplesDialog.Result result) {
                presenter.createSamples(result.numOfSamples, result.namingPattern, result.startingNumber);
                return true;
            }
        }, presenter);
    }

    private void deleteSelectedSamples() {
        Set<SampleRow> selection = gridView.getSelectedRows();
        if (selection.isEmpty()) {
            NotificationPopupPanel.warning("Please select samples you want to delete.", true, false);
        } else if (Window.confirm("The selected samples and all associated information will no longer be available if you delete. Do you want to continue?")) {
            presenter.removeSamples(new ArrayList<>(selection));
            gridView.removeSelectedRows();
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

    private void addNameColumn() {
        final EditSuggestCell nameCell = new EditSuggestCell(null) {
            @Override
            public boolean validateInput(String value, int rowIndex) {
                if (value == null || trimValue(value).isEmpty()) {
                    NotificationPopupPanel.error("Sample with empty name is not permitted.", true, false);
                    return false;
                }
                if (!isNameUnique(value, rowIndex)) {
                    NotificationPopupPanel.error("Sample with the name '" + value + "' already exists.", true, false);
                    return false;
                }
                return true;
            }
        };
        Column<SampleRow, String> column = new SampleNameColumn(nameCell);

        column.setFieldUpdater(new FieldUpdater<SampleRow, String>() {
            @Override
            public void update(int index, SampleRow row, String value) {
                row.setName(trimValue(value));
                updateRow(row);
            }
        });
        column.setSortable(true);
        Comparator<SampleRow> comparator = new Comparator<SampleRow>() {
            @Override
            public int compare(SampleRow o1, SampleRow o2) {
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

    public class SampleNameColumn extends Column<SampleRow, String> {

        public SampleNameColumn(Cell<String> cell) {
            super(cell);
        }

        @Override
        public String getValue(SampleRow row) {
            return row.getName();
        }
    }

    private boolean isNameUnique(String name, int rowIndex) {
        List<SampleRow> rows = gridView.getRows();
        for (int i = 0; i < rows.size(); i++) {
            if (i != rowIndex && rows.get(i).getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    private void addColumn(final SampleColumn sampleColumn) {
        if (sampleColumn.getType().isMaterialType()) {
            addMaterialTypeColumn(sampleColumn);
        } else if (sampleColumn.getUnits() != null) {
            addTextColumn(sampleColumn);
        } else if (sampleColumn.getTerm() != null) {
            addEfoSuggestColumn(sampleColumn);
        } else {
            addTextColumn(sampleColumn);
        }
    }

    private void addTextColumn(final SampleColumn sampleColumn) {
        Column<SampleRow, String> column = new Column<SampleRow, String>(
                new EditTextCell()
        ) {
            @Override
            public String getValue(SampleRow row) {
                return row.getValue(sampleColumn);
            }
        };
        column.setFieldUpdater(new FieldUpdater<SampleRow, String>() {
            @Override
            public void update(int index, SampleRow row, String value) {
                row.setValue(trimValue(value), sampleColumn);
                updateRow(row);
            }
        });
        //column.setSortable(true);
        gridView.addColumn(sampleColumn.getTitle(), column, null, COLUMN_WIDTH, Style.Unit.PX);
    }

    private void addEfoSuggestColumn(final SampleColumn sampleColumn) {
        final SampleAttributeEfoSuggest efoSuggestService = presenter.getEfoTerms();
        final OntologyTerm term = sampleColumn.getTerm();

        Cell<String> efoSuggestCell = new EditSuggestCell(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
                efoSuggestService.getTerms(query, term, limit, callback);
            }
        }), sampleColumn.getTemplate().isMandatory());

        Column<SampleRow, String> column = new Column<SampleRow, String>(
                efoSuggestCell
        ) {
            @Override
            public String getValue(SampleRow row) {
                return row.getValue(sampleColumn);
            }
        };
        column.setFieldUpdater(new FieldUpdater<SampleRow, String>() {
            @Override
            public void update(int index, SampleRow row, String value) {
                row.setValue(trimValue(value), sampleColumn);
                updateRow(row);
            }
        });
        //column.setSortable(true);
        gridView.addColumn(sampleColumn.getTitle(), column, null, COLUMN_WIDTH, Style.Unit.PX);
    }

    private void addMaterialTypeColumn(final SampleColumn sampleColumn) {
        Column<SampleRow, String> column = new Column<SampleRow, String>(new EditSelectionCell(materialTypes)) {
            @Override
            public String getValue(SampleRow row) {
                return row.getValue(sampleColumn);
            }
        };
        column.setFieldUpdater(new FieldUpdater<SampleRow, String>() {
            @Override
            public void update(int index, SampleRow row, String value) {
                row.setValue(trimValue(value), sampleColumn);
                updateRow(row);
            }
        });
        //column.setSortable(true);
        gridView.addPermanentColumn("Material Type", column, null, COLUMN_WIDTH, Style.Unit.PX);
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
