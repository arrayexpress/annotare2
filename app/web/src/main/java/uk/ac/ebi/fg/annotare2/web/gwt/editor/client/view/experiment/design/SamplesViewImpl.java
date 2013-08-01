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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestBoxCell;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ValidationMessage;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class SamplesViewImpl extends Composite implements SamplesView {

    private final GridView<SampleRow> gridView;
    private ValidationMessage errorMessage;

    private List<SampleColumn> columns = new ArrayList<SampleColumn>();

    private Presenter presenter;

    public SamplesViewImpl() {
        gridView = new GridView<SampleRow>();
        Button button = new Button("Sample Attributes");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new SampleColumnsDialog(columns,
                        presenter.getEfoTerms(),
                        new SampleColumnsDialog.Callback() {
                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onOkay(List<SampleColumn> columns) {
                                updateColumns(columns);
                            }
                        });
            }
        });
        gridView.addTool(button);

        button = new Button("Add Sample");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createNewSample();
            }
        });
        gridView.addTool(button);

        button = new Button("Delete Selected Rows");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                deleteSelectedSamples();
            }
        });
        gridView.addTool(button);

        errorMessage = new ValidationMessage();
        gridView.addTool(errorMessage);
        initWidget(gridView);
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
    }

    private void setColumns(List<SampleColumn> columns) {
        this.columns = new ArrayList<SampleColumn>(columns);
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

    private void createNewSample() {
        presenter.createSample();
    }

    private void deleteSelectedSamples() {
        Set<SampleRow> selection = gridView.getSelectedRows();
        if (selection.isEmpty()) {
            return;
        }
        presenter.removeSamples(new ArrayList<SampleRow>(selection));
        gridView.removeSelectedRows();
    }

    private void addNameColumn() {
        final EditTextCell nameCell = new EditTextCell();
        Column<SampleRow, String> column = new Column<SampleRow, String>(nameCell) {
            @Override
            public String getValue(SampleRow row) {
                return row.getName();
            }
        };
        column.setFieldUpdater(new FieldUpdater<SampleRow, String>() {
            @Override
            public void update(int index, SampleRow row, String value) {
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

    private boolean isNameValid(String name, int rowIndex) {
        if (name == null || name.trim().isEmpty()) {
            showErrorMessage("Sample name can't be empty");
            return false;
        }
        if (isDuplicated(name, rowIndex)) {
            showErrorMessage("Sample with the name '" + name + "' already exists");
            return false;
        }
        return true;
    }

    private boolean isDuplicated(String name, int rowIndex) {
        List<SampleRow> rows = gridView.getRows();
        Set<String> names = new HashSet<String>();
        int i = 0;
        for (SampleRow row : rows) {
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

    private void addColumn(final SampleColumn sampleColumn) {
        Column<SampleRow, String> column = new Column<SampleRow, String>(
                createCellEditor(sampleColumn)
        ) {
            @Override
            public String getValue(SampleRow row) {
                return row.getValue(sampleColumn);
            }
        };
        column.setFieldUpdater(new FieldUpdater<SampleRow, String>() {
            @Override
            public void update(int index, SampleRow row, String value) {
                row.setValue(value, sampleColumn);
                updateRow(row);
            }
        });
        column.setSortable(true);
        gridView.addColumn(sampleColumn.getPrettyName(), column, null, 150, Style.Unit.PX);
    }

    private Cell<String> createCellEditor(SampleColumn sampleColumn) {
        final List<Cell<String>> editor = new ArrayList<Cell<String>>();
        final ColumnValueTypeEfoTerms efoSuggestService = presenter.getEfoTerms();

        sampleColumn.getValueType().visit(new ColumnValueType.Visitor() {
            @Override
            public void visitTextValueType(TextValueType valueType) {
                editor.add(new EditTextCell());
            }

            @Override
            public void visitTermValueType(final OntologyTermValueType valueType) {
                editor.add(new SuggestBoxCell(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
                    @Override
                    public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                        OntologyTerm term = valueType.getEfoTerm();
                        if (term == null) {
                            efoSuggestService.getTerms(query, limit, callback);
                        } else {
                            efoSuggestService.getTerms(query, term, limit, callback);
                        }
                    }
                })));
            }

            @Override
            public void visitNumericValueType(NumericValueType valueType) {
                editor.add(new EditTextCell());
                // TODO allow only numeric values
            }
        });
        return editor.iterator().next();
    }
}
