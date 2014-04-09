package com.google.gwt.user.cellview.client;

import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PasteArea;
import com.google.gwt.view.client.CellPreviewEvent;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomDataGrid<T> extends DataGrid<T> implements PasteArea.PasteEventHandler {

    public interface CustomStyle extends DataGrid.Style {
        String dataGridKeyboardSelectedInactiveCell();
    }

    public interface CustomResources extends DataGrid.Resources {

        @Override
        @Source("CustomDataGrid.css")
        CustomStyle dataGridStyle();

    }

    public static CustomResources createResources() {
        return GWT.create(CustomResources.class);
    }

    private final CustomResources resources;

    private final static Logger logger = Logger.getLogger("CustomDataGrid");

    public CustomDataGrid(CustomResources resources) {
        super(50, resources);
        this.resources = resources;

        PasteArea<T> pasteArea = new PasteArea<T>();
        pasteArea.addPasteHandler(this);
        this.addCellPreviewHandler(pasteArea);
        sinkEvents(Event.ONPASTE);

        setKeyboardSelectionHandler(new CustomDataGridKeyboardSelectionHandler<T>(this));
    }

    @Override
    public void onPaste(PasteArea.PasteEvent event) {
        logger.log(Level.INFO, "pasted: " + event.getData());
        Window.alert(event.getData());
    }

    @Override
    protected void onFocus() {
        TableCellElement td = getKeyboardSelectedTableCellElement();
        if (td != null) {
            TableRowElement tr = td.getParentElement().cast();
            td.replaceClassName(getStyle().dataGridKeyboardSelectedInactiveCell(), getStyle().dataGridKeyboardSelectedCell());
            setRowStyleName(tr, getStyle().dataGridKeyboardSelectedRow(), getStyle().dataGridKeyboardSelectedRowCell(), true);
        }
    }

    @Override
    protected void onBlur() {
        TableCellElement td = getKeyboardSelectedTableCellElement();
        if (td != null) {
            TableRowElement tr = td.getParentElement().cast();
            td.replaceClassName(getStyle().dataGridKeyboardSelectedCell(), getStyle().dataGridKeyboardSelectedInactiveCell());
            setRowStyleName(tr, getStyle().dataGridKeyboardSelectedRow(), getStyle().dataGridKeyboardSelectedRowCell(), false);
        }
    }

    @Override
    protected void setKeyboardSelected(int index, boolean selected, boolean stealFocus) {
        if (KeyboardSelectionPolicy.DISABLED == getKeyboardSelectionPolicy()
                || !isRowWithinBounds(index)) {
            return;
        }

        TableRowElement tr = getSubRowElement(index + getPageStart(), getKeyboardSelectedSubRow());
        if (null != tr) {
            NodeList<TableCellElement> cells = tr.getCells();
            for (int i = 0; i < cells.getLength(); i++) {
                TableCellElement td = cells.getItem(i);
                td.removeClassName(getStyle().dataGridKeyboardSelectedInactiveCell());
            }
        }

        super.setKeyboardSelected(index, selected, stealFocus);
    }

    private TableCellElement getKeyboardSelectedTableCellElement() {
        int colIndex = getKeyboardSelectedColumn();
        if (colIndex < 0) {
            return null;
        }

        // Do not use getRowElement() because that will flush the presenter.
        int rowIndex = getKeyboardSelectedRow();
        if (rowIndex < 0 || rowIndex >= getTableBodyElement().getRows().getLength()) {
            return null;
        }
        TableRowElement tr = getSubRowElement(rowIndex + getPageStart(), getKeyboardSelectedSubRow());
        if (tr != null) {
            int cellCount = tr.getCells().getLength();
            if (cellCount > 0) {
                int column = Math.min(colIndex, cellCount - 1);
                return tr.getCells().getItem(column);
            }
        }
        return null;
    }

    private void setRowStyleName(TableRowElement tr, String rowStyle, String cellStyle, boolean add) {
        setStyleName(tr, rowStyle, add);
        NodeList<TableCellElement> cells = tr.getCells();
        for (int i = 0; i < cells.getLength(); i++) {
            setStyleName(cells.getItem(i), cellStyle, add);
        }
    }

    private CustomStyle getStyle() {
        return resources.dataGridStyle();
    }

    public static class CustomDataGridKeyboardSelectionHandler<T> extends
            DefaultKeyboardSelectionHandler<T> {

        private AbstractCellTable<T> table;

        public CustomDataGridKeyboardSelectionHandler(AbstractCellTable<T> table) {
            super(table);
            this.table = table;
        }

        @Override
        public AbstractCellTable<T> getDisplay() {
            return table;
        }

        @Override
        public void onCellPreview(CellPreviewEvent<T> event) {
            NativeEvent nativeEvent = event.getNativeEvent();
            String eventType = event.getNativeEvent().getType();

            if (BrowserEvents.KEYDOWN.equals(eventType) && !event.isCellEditing()) {
        /*
         * Handle keyboard navigation, unless the cell is being edited. If the
         * cell is being edited, we do not want to change rows.
         *
         * Prevent default on navigation events to prevent default scrollbar
         * behavior.
         */
                int oldColumn = table.getKeyboardSelectedColumn();
                boolean isRtl = LocaleInfo.getCurrentLocale().isRTL();
                int keyCodeNext = isRtl ? KeyCodes.KEY_LEFT : KeyCodes.KEY_RIGHT;
                int keyCodePrevious = isRtl ? KeyCodes.KEY_RIGHT : KeyCodes.KEY_LEFT;
                int keyCode = nativeEvent.getKeyCode();
                int newColumn = oldColumn;
                if (keyCode == keyCodeNext) {
                    newColumn = oldColumn < table.getColumnCount() - 1 ? oldColumn + 1 : oldColumn;
                    handledEvent(event);
                } else if (keyCode == keyCodePrevious) {
                    newColumn = oldColumn > 0 ? oldColumn - 1 : oldColumn;
                    handledEvent(event);
                }
                if (newColumn != oldColumn && isColumnInteractive(table.getColumn(newColumn))) {
                    table.setKeyboardSelectedColumn(newColumn);
                }
            } else if (BrowserEvents.CLICK.equals(eventType) || BrowserEvents.FOCUS.equals(eventType)) {
        /*
         * Move keyboard focus to the clicked column, even if the cell is being
         * edited. Unlike key events, we aren't moving the currently selected
         * row, just updating it based on where the user clicked.
         *
         * Since the user clicked, allow focus to go to a non-interactive
         * column.
         */
                int col = event.getColumn();
                int relRow = event.getIndex() - getDisplay().getPageStart();
                int subrow = event.getContext().getSubIndex();
                if ((table.getKeyboardSelectedColumn() != col)
                        || (table.getKeyboardSelectedRow() != relRow)
                        || (table.getKeyboardSelectedSubRow() != subrow)) {
                    boolean stealFocus = false;
                    if (BrowserEvents.CLICK.equals(eventType)) {
                        // If a natively focusable element was just clicked, then do not
                        // steal focus.
                        Element target = Element.as(event.getNativeEvent().getEventTarget());
                        stealFocus = !CellBasedWidgetImpl.get().isFocusable(target);
                    }

                    // Update the row and subrow.
                    table.setKeyboardSelectedRow(relRow, subrow, stealFocus);

                    // Update the column index.
                    table.setKeyboardSelectedColumn(col, stealFocus);
                    handledEvent(event);
                }

                // Do not cancel the event as the click may have occurred on a Cell.
                return;
            }

            // Let the parent class handle the event.
            super.onCellPreview(event);
        }

        private boolean isColumnInteractive(HasCell<?, ?> column) {
            Set<String> consumedEvents = column.getCell().getConsumedEvents();
            return consumedEvents != null && consumedEvents.size() > 0;
        }
    }
}
