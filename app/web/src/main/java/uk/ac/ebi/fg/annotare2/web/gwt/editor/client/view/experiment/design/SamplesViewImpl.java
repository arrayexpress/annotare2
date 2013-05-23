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
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestBoxCell;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SamplesViewImpl extends Composite implements SamplesView {

    private static final int PAGE_SIZE = 50;

    interface Binder extends UiBinder<Widget, SamplesViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    SimpleLayoutPanel gridPanel;

    @UiField
    HorizontalPanel toolBar;

    private MyDataGrid<SampleRow> dataGrid;
    private ListDataProvider<SampleRow> dataProvider;

    private List<SampleColumn> columns = new ArrayList<SampleColumn>();

    private int permanentColumnCount;

    private Presenter presenter;

    public SamplesViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        toolBar.add(createTools());
    }

    @Override
    public void setData(List<SampleRow> rows, List<SampleColumn> columns) {
        //TODO put resources as inner interface
        MyDataGridResources resources = GWT.create(MyDataGridResources.class);
        dataGrid = new MyDataGrid<SampleRow>(PAGE_SIZE, resources);
        dataGrid.setEmptyTableWidget(new Label("No data"));

        final SelectionModel<SampleRow> selectionModel =
                new MultiSelectionModel<SampleRow>(new ProvidesKey<SampleRow>() {
                    @Override
                    public Object getKey(SampleRow item) {
                        return item.getName();
                    }
                });

        dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<SampleRow>createCheckboxManager());

        dataProvider = new ListDataProvider<SampleRow>();
        dataProvider.addDataDisplay(dataGrid);
        dataProvider.getList().addAll(rows);

        ColumnSortEvent.ListHandler<SampleRow> sortHandler =
                new ColumnSortEvent.ListHandler<SampleRow>(dataProvider.getList());
        dataGrid.addColumnSortHandler(sortHandler);

        setColumns(dataGrid, sortHandler, columns);

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        toolBar.add(pager);
        toolBar.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);

        gridPanel.add(dataGrid);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void setColumns(MyDataGrid<SampleRow> dataGrid, ColumnSortEvent.ListHandler<SampleRow> sortHandler, List<SampleColumn> columns) {
        this.columns = new ArrayList<SampleColumn>(columns);

        addPermanentColumns(dataGrid, sortHandler);
        permanentColumnCount = dataGrid.getColumnCount();
        for (SampleColumn column : columns) {
            addColumn(column);
        }
    }

    private void updateColumns(List<SampleColumn> newColumns) {
        for (int i = 0; i < this.columns.size(); i++) {
            dataGrid.clearColumnWidth(permanentColumnCount - 1 + i);
        }
        for (SampleColumn column : this.columns) {
            dataGrid.removeColumn(permanentColumnCount - 1);
        }

        dataGrid.fix();

        for (SampleColumn column : newColumns) {
            addColumn(column);
        }
        this.columns = new ArrayList<SampleColumn>(newColumns);
        presenter.updateColumns(newColumns);
    }

    private void addColumn(SampleColumn column) {
        insertColumn(column, dataGrid.getColumnCount());
    }

    private void insertColumn(final SampleColumn sampleColumn, int beforeIndex) {
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
        dataGrid.insertResizableColumn(column, sampleColumn.getName(), beforeIndex);
        dataGrid.setColumnWidth(beforeIndex - 1, 150, Style.Unit.PX);
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
            public void visitTermValueType(final EfoTermValueType valueType) {
                editor.add(new SuggestBoxCell(new EfoSuggestOracle(new SuggestService<EfoTermDto>() {
                    @Override
                    public void suggest(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
                        EfoTermDto term = valueType.getEfoTerm();
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

    private void addPermanentColumns(MyDataGrid<SampleRow> dataGrid, ColumnSortEvent.ListHandler<SampleRow> sortHandler) {
        addCheckBoxColumn(dataGrid);
        addNameColumn(dataGrid, sortHandler);
    }

    private void addCheckBoxColumn(final MyDataGrid<SampleRow> dataGrid) {
        Column<SampleRow, Boolean> checkboxColumn = new Column<SampleRow, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(SampleRow object) {
                return dataGrid.getSelectionModel().isSelected(object);
            }
        };
        dataGrid.addColumn(checkboxColumn, new CheckboxHeader());
        dataGrid.setColumnWidth(checkboxColumn, 40, Style.Unit.PX);
    }

    private void addNameColumn(MyDataGrid<SampleRow> dataGrid, ColumnSortEvent.ListHandler<SampleRow> sortHandler) {
        Column<SampleRow, String> column = new Column<SampleRow, String>(new EditTextCell()) {
            @Override
            public String getValue(SampleRow row) {
                return row.getName();
            }
        };
        column.setFieldUpdater(new FieldUpdater<SampleRow, String>() {
            @Override
            public void update(int index, SampleRow row, String value) {
                // TODO check names are unique
                row.setName(value);
                updateRow(row);
            }
        });
        sortHandler.setComparator(column, new Comparator<SampleRow>() {
            @Override
            public int compare(SampleRow o1, SampleRow o2) {
                if (o1 == o2) {
                    return 0;
                }
                String v1 = o1.getName();
                String v2 = o2.getName();
                return v1.compareTo(v2);
            }
        });
        column.setSortable(true);
        dataGrid.addResizableColumn(column, "Name");
        dataGrid.setColumnWidth(column, 100, Style.Unit.PX);
    }

    private HorizontalPanel createTools() {
        HorizontalPanel tools = new HorizontalPanel();
        tools.setSpacing(3);
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
        tools.add(button);

        button = new Button("Delete Selected Rows");
        tools.add(button);
        return tools;
    }

    private void updateRow(SampleRow row) {
        presenter.updateRow(row);
    }

    private class CheckboxHeader extends Header<Boolean> implements HasValue<Boolean> {

        private boolean checked;
        private HandlerManager handlerManager;

        public CheckboxHeader() {
            super(new CheckboxCell());
            checked = false;
        }

        @Override
        public Boolean getValue() {
            return checked;
        }

        @Override
        public void onBrowserEvent(Cell.Context context, Element elem, NativeEvent nativeEvent) {
            int eventType = Event.as(nativeEvent).getTypeInt();
            if (eventType == Event.ONCHANGE) {
                nativeEvent.preventDefault();
                //use value setter to easily fire change event to handlers
                setValue(!checked, true);
            }
        }

        @Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
            return ensureHandlerManager().addHandler(ValueChangeEvent.getType(), handler);
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            ensureHandlerManager().fireEvent(event);
        }

        @Override
        public void setValue(Boolean value) {
            checked = value;
        }

        @Override
        public void setValue(Boolean value, boolean fireEvents) {
            checked = value;
            if (fireEvents) {
                ValueChangeEvent.fire(this, value);
            }
        }

        private HandlerManager ensureHandlerManager() {
            if (handlerManager == null) {
                handlerManager = new HandlerManager(this);
            }
            return handlerManager;
        }
    }
}
