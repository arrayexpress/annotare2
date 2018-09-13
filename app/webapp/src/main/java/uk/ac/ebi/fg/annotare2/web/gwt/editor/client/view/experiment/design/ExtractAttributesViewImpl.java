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

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ConditionalColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.submission.model.ExtractAttribute;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExtractAttributesRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.BackwardCompatibleSelectionCell;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DynSelectionCell;

import java.util.Comparator;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.submission.model.ExtractAttribute.*;

/**
 * @author Olga Melnichuk
 */
public class ExtractAttributesViewImpl extends Composite implements ExtractAttributesView {

    private final static String MICRORNA_BY_HTS_TYPE = "microRNA profiling by high throughput sequencing";

    private final GridView<ExtractAttributesRow> gridView;
    private Presenter presenter;
    private String aeExperimentType;

    public ExtractAttributesViewImpl() {
        gridView = new GridView<>();
        gridView.setRowSelectionEnabled(false);

        Button button = new Button("Fill Down Value");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fillDownValue();
            }
        });
        gridView.addTool(button);

        initWidget(gridView);
    }

    @Override
    public void setData(List<ExtractAttributesRow> rows) {
        gridView.setRows(rows);
        setColumns();
    }

    @Override
    public void setAeExperimentType(String experimentType) {
        aeExperimentType = experimentType;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void setColumns() {
        addNameColumn();
        addColumn(LIBRARY_LAYOUT);
        addColumn(LIBRARY_SOURCE);
        addColumn(LIBRARY_STRATEGY);
        addColumn(LIBRARY_SELECTION);
        addColumn(LIBRARY_STRAND);
        if (MICRORNA_BY_HTS_TYPE.equals(aeExperimentType)) {
            addColumn(ADAPTER_SEQUENCE);
        }
        addColumnForPairedLayout(NOMINAL_LENGTH);
        addColumnForPairedLayout(NOMINAL_SDEV);
        addColumnForPairedLayout(ORIENTATION);
    }

    private void addNameColumn() {
        Column<ExtractAttributesRow, String> column = new Column<ExtractAttributesRow, String>(new TextCell()) {
            @Override
            public String getValue(ExtractAttributesRow row) {
                return row.getName();
            }
        };
        // TODO: commented out until we start saving changes in the experiment profile
        //column.setFieldUpdater(new FieldUpdater<ExtractAttributesRow, String>() {
        //    @Override
        //    public void update(int index, ExtractAttributesRow row, String value) {
        //        // TODO check names are unique
        //        row.setName(value);
        //        updateRow(row);
        //    }
        //});
        column.setSortable(true);
        Comparator<ExtractAttributesRow> comparator = new Comparator<ExtractAttributesRow>() {
            @Override
            public int compare(ExtractAttributesRow o1, ExtractAttributesRow o2) {
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
        final Cell<String> cell = attr.hasOptions() ? new BackwardCompatibleSelectionCell<>(attr.getOptions()) : new EditTextCell();
        Column<ExtractAttributesRow, String> column = new Column<ExtractAttributesRow, String>(cell) {
            @Override
            public String getValue(ExtractAttributesRow row) {
                String value = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(value)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(value);
                }
                return value == null ? "" : attr.hasOptions() && attr.hasValue(value) ? attr.getOption(value) : value;
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<ExtractAttributesRow, String>() {
            @Override
            public void update(int index, ExtractAttributesRow row, String value) {
                String oldValue = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(oldValue)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(oldValue);
                }
                row.setValue(attr.hasOptions() ? attr.getValue(value) : value, attr);
                if (LIBRARY_LAYOUT.equals(attr) && !isPairedLayout(value)) {
                    row.setValue("", NOMINAL_LENGTH);
                    row.setValue("", NOMINAL_SDEV);
                    row.setValue("", ORIENTATION);
                }
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX, attr.getHelpText());
    }

    private void addColumnForPairedLayout(final ExtractAttribute attr) {
        final Cell<String> cell = attr.hasOptions() ? new BackwardCompatibleSelectionCell(attr.getOptions()) : new EditTextCell();

        Column<ExtractAttributesRow, String> column = new ConditionalColumn<ExtractAttributesRow>(cell) {
            @Override
            public String getValue(ExtractAttributesRow row) {
                String value = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(value)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(value);
                }
                return value == null ? "" : attr.hasOptions() ? attr.getOption(value) : value;
            }

            @Override
            public boolean isEditable(ExtractAttributesRow row) {
                return isPairedLayout(row.getValue(LIBRARY_LAYOUT));
            }
        };
        column.setCellStyleNames("app-SelectionCell");
        column.setFieldUpdater(new FieldUpdater<ExtractAttributesRow, String>() {
            @Override
            public void update(int index, ExtractAttributesRow row, String value) {
                String oldValue = row.getValue(attr);
                if (attr.hasOptions() && !attr.hasValue(oldValue)) {
                    ((BackwardCompatibleSelectionCell)cell).updateOptions(oldValue);
                }
                row.setValue(attr.hasOptions() ? attr.getValue(value) : value, attr);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn(attr.getTitle(), column, null, 150, Style.Unit.PX, attr.getHelpText());
    }

    private void updateRow(ExtractAttributesRow row) {
        presenter.updateRow(row.copy());
        gridView.redraw();
    }

    private boolean isPairedLayout(String layout) {
        return "paired".equalsIgnoreCase(layout);
    }

    private void fillDownValue() {
        gridView.fillDownKeyboardSelectedColumn();
    }
}
