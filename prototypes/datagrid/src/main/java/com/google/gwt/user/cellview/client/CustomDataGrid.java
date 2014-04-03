package com.google.gwt.user.cellview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.CellPreviewEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomDataGrid<T> extends DataGrid<T> {
    public interface CustomDataGridResources extends DataGrid.Resources {

        @Override
        @Source("CustomDataGrid.css")
        Style dataGridStyle();

    }

    private final Logger logger = Logger.getLogger("CustomDataGrid");

    public CustomDataGrid() {
        super(50, (CustomDataGridResources) GWT.create(CustomDataGridResources.class));
        setKeyboardSelectionHandler(new CustomDataGridKeyboardSelectionHandler<T>(this));
        //getTableHeadElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
        //getTableBodyElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
    }

    @Override
    public void onBrowserEvent2(Event event) {
        logger.log(Level.INFO, event.getType());
        super.onBrowserEvent2(event);
        logger.log(Level.INFO, getKeyboardSelectedColumn() + ", " + getKeyboardSelectedRow());
    }

    @Override
    public void onBlur() {
        logger.log(Level.INFO, "onBlur called");
    }

    @Override
    public void onFocus() {
        logger.log(Level.INFO, "onFocus called");
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
                if (keyCode == keyCodeNext) {
                    int nextColumn = oldColumn < table.getColumnCount() - 1 ? oldColumn + 1 : oldColumn;
                    table.setKeyboardSelectedColumn(nextColumn);
                    handledEvent(event);
                } else if (keyCode == keyCodePrevious) {
                    int prevColumn = oldColumn > 0 ? oldColumn - 1 : oldColumn;
                    table.setKeyboardSelectedColumn(prevColumn);
                    handledEvent(event);
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
    }
}
