package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
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

    private Button columnsButton;

    private final List<SdrfRow> allRows = new ArrayList<SdrfRow>();

    private final List<SdrfColumn> allColumns = new ArrayList<SdrfColumn>();

    interface Binder extends UiBinder<Widget, SdrfSectionView> {
    }

    public SdrfSectionView(String section) {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);

        title.setText(section);
        initTable(allRows, allColumns);
    }

    private void initTable(List<SdrfRow> rows, List<SdrfColumn> columns) {
        MyDataGridResources resources = com.google.gwt.core.shared.GWT.create(MyDataGridResources.class);
        MyDataGrid<SdrfRow> dataGrid = new MyDataGrid<SdrfRow>(PAGE_SIZE, resources);
        dataGrid.setEmptyTableWidget(new Label("There's no data yet, come later"));
        dataGrid.setMinimumTableWidthInPx(tablePanel.getOffsetWidth());

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

        columnsButton = new Button();
        HorizontalPanel tableBar = new HorizontalPanel();
        tableBar.add(pager);
        tableBar.add(columnsButton);

        tablePanel.addNorth(tableBar, 40);
        tablePanel.add(dataGrid);
    }

    private void initDefaultColumns(MyDataGrid<SdrfRow> dataGrid, ColumnSortEvent.ListHandler<SdrfRow> sortHandler) {
        addIndexColumn(dataGrid, sortHandler);
        addCheckBoxColumn(dataGrid);
    }

    private void addCheckBoxColumn(final MyDataGrid<SdrfRow> dataGrid) {
        Column<SdrfRow, Boolean> checkboxColumn = new Column<SdrfRow, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(SdrfRow object) {
                return dataGrid.getSelectionModel().isSelected(object);
            }
        };
        dataGrid.addColumn(checkboxColumn, new CheckboxHeader());
        dataGrid.setColumnWidth(dataGrid.getColumnCount2() - 1, 40, Style.Unit.PX);
    }

    private void addIndexColumn(MyDataGrid<SdrfRow> dataGrid, ColumnSortEvent.ListHandler<SdrfRow> sortHandler) {
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
        dataGrid.addColumn("+", column);
        dataGrid.setColumnWidth(dataGrid.getColumnCount2() - 1, 50, Style.Unit.PX);
    }

    private void addColumn(SdrfColumn column) {
        //TODO
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

        private SdrfRow(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static enum SdrfColumn {
        CHARCTERISTIC("Characteristic"),
        FACTOR_VALUE("Factor Value"),
        ARRAY_DESIGN("Array Design"),
        LABEL("Label"),
        MATERIAL_TYPE("Material Type"),
        PROVIDER("Provider"),
        TECHNOLOGY_TYPE("Technology Type"),
        COMMENT("Comment"),
        PROTOCOL("Protocol");

        private final String title;

        private SdrfColumn(String title) {
            this.title = title;
        }
    }

}
