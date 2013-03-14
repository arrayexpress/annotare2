package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfSectionView extends Composite implements IsWidget {

    private static final int PAGE_SIZE = 50;

    @UiField
    Label title;

    @UiField
    DockLayoutPanel tablePanel;

    private final SdrfSection section;
    private final boolean isSectionFirst;

    private MyDataGrid<SdrfRow> dataGrid;

    private final List<SdrfRow> allRows = new ArrayList<SdrfRow>();

    private List<SdrfColumn> allColumns = new ArrayList<SdrfColumn>();

    private int columnOffset;

    interface Binder extends UiBinder<Widget, SdrfSectionView> {
    }

    public SdrfSectionView(SdrfSection section, boolean isSectionFirst) {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        this.section = section;
        this.isSectionFirst = isSectionFirst;

        title.setText(section.getTitle());
        initTable(allRows, allColumns);
    }

    private void initTable(List<SdrfRow> rows, List<SdrfColumn> columns) {
        MyDataGridResources resources = com.google.gwt.core.shared.GWT.create(MyDataGridResources.class);
        dataGrid = new MyDataGrid<SdrfRow>(PAGE_SIZE, resources);
        dataGrid.setEmptyTableWidget(new Label("No data"));

        final SelectionModel<SdrfRow> selectionModel =
                new MultiSelectionModel<SdrfRow>(new ProvidesKey<SdrfRow>() {
                    @Override
                    public Object getKey(SdrfRow item) {
                        return item.getIndex();
                    }
                });

        dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<SdrfRow>createCheckboxManager());

        SimplePager.Resources pagerResources = com.google.gwt.core.shared.GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);

        ListDataProvider<SdrfRow> dataProvider = new ListDataProvider<SdrfRow>();
        dataProvider.addDataDisplay(dataGrid);
        dataProvider.getList().addAll(rows);

        ColumnSortEvent.ListHandler<SdrfRow> sortHandler =
                new ColumnSortEvent.ListHandler<SdrfRow>(dataProvider.getList());
        dataGrid.addColumnSortHandler(sortHandler);

        initDefaultColumns(dataGrid, sortHandler);

        for (SdrfColumn column : columns) {
            addColumn(column);
        }

        Button columnsButton = new Button("Columns");
        columnsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openColumnsDialog();
            }
        });
        HorizontalPanel tableBar = new HorizontalPanel();
        tableBar.add(columnsButton);
        tableBar.add(new Button("Add Row"));
        tableBar.add(new Button("1 : 1"));
        tableBar.add(pager);

        tablePanel.addNorth(tableBar, 40);
        tablePanel.add(dataGrid);
    }

    private void openColumnsDialog() {
        SdrfColumnsDialog dialog = new SdrfColumnsDialog(section.getColumnTypes(isSectionFirst), allColumns);
        dialog.addSelectionHandler(new SelectionHandler<List<SdrfColumn>>() {
            @Override
            public void onSelection(SelectionEvent<List<SdrfColumn>> event) {
                updateColumns(event.getSelectedItem());
            }
        });
        dialog.show();
    }

    private void updateColumns(List<SdrfColumn> newColumns) {
        int colIndex = 0;
        SdrfColumn col = allColumns.size() <= colIndex ? null : allColumns.get(colIndex);
        for (int i = 0; i < newColumns.size(); i++) {
            SdrfColumn newColumn = newColumns.get(i);
            if (col == null) {
                addColumn(newColumn);
            } else if (!col.equals(newColumn)) {
                insertColumn(newColumn, columnOffset + i);
            } else {
                if (colIndex + 1 < allColumns.size()) {
                    col = allColumns.get(++colIndex);
                } else {
                    col = null;
                }
            }
        }

        for (int j = allColumns.size() - 1; j >= colIndex; j--) {
            removeColumn(columnOffset + newColumns.size() + j);
        }

        allColumns = new ArrayList<SdrfColumn>();
        allColumns.addAll(newColumns);
    }

    private void initDefaultColumns(MyDataGrid<SdrfRow> grid, ColumnSortEvent.ListHandler<SdrfRow> sortHandler) {
        addIndexColumn(grid, sortHandler);
        addCheckBoxColumn(grid);
        addNameColumn(grid, sortHandler);
        columnOffset += 3;
    }

    private void addCheckBoxColumn(final MyDataGrid<SdrfRow> grid) {
        Column<SdrfRow, Boolean> checkboxColumn = new Column<SdrfRow, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(SdrfRow object) {
                return grid.getSelectionModel().isSelected(object);
            }
        };
        grid.addColumn(checkboxColumn, new CheckboxHeader());
        grid.setColumnWidth(checkboxColumn, 40, Style.Unit.PX);
    }

    private void addIndexColumn(MyDataGrid<SdrfRow> grid, ColumnSortEvent.ListHandler<SdrfRow> sortHandler) {
        Column<SdrfRow, String> column = new Column<SdrfRow, String>(new TextCell()) {
            @Override
            public String getValue(SdrfRow row) {
                return row.getIndex() + "";
            }
        };
        sortHandler.setComparator(column, new Comparator<SdrfRow>() {
            @Override
            public int compare(SdrfRow o1, SdrfRow o2) {
                if (o1 == o2) {
                    return 0;
                }
                int v1 = o1.getIndex();
                int v2 = o2.getIndex();
                return new Integer(v1).compareTo(v2);
            }
        });
        column.setSortable(true);
        grid.addColumn(column, new TextHeader("N"));
        grid.setColumnWidth(column, 50, Style.Unit.PX);
    }

    private void addNameColumn(MyDataGrid<SdrfRow> dataGrid, ColumnSortEvent.ListHandler<SdrfRow> sortHandler) {
        Column<SdrfRow, String> column = new Column<SdrfRow, String>(new TextCell()) {
            @Override
            public String getValue(SdrfRow row) {
                return row.getName();
            }
        };
        sortHandler.setComparator(column, new Comparator<SdrfRow>() {
            @Override
            public int compare(SdrfRow o1, SdrfRow o2) {
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

    private void insertColumn(final SdrfColumn sdrfColumn, int beforeIndex) {
        Column<SdrfRow, String> column = new Column<SdrfRow, String>(new TextCell()) {
            @Override
            public String getValue(SdrfRow row) {
                return row.getValue(sdrfColumn);
            }
        };
        column.setSortable(true);
        dataGrid.insertResizableColumn(column, sdrfColumn.getTitle(), beforeIndex);
        dataGrid.setColumnWidth(beforeIndex - 1, 150, Style.Unit.PX);
    }

    private void addColumn(final SdrfColumn sdrfColumn) {
        insertColumn(sdrfColumn, dataGrid.getColumnCount());
    }

    private void removeColumn(int index) {
        dataGrid.removeColumn(index);
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

    private static class SdrfRow {
        private final Map<SdrfColumn, String> values = new HashMap<SdrfColumn, String>();
        private final int index;
        private String name;

        private SdrfRow(int index, String name) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getValue(SdrfColumn column) {
            return values.get(column);
        }
    }
}
