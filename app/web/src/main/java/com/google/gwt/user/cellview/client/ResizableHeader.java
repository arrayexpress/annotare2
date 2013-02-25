package com.google.gwt.user.cellview.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

import static com.google.gwt.dom.client.BrowserEvents.*;
import static com.google.gwt.dom.client.Style.Unit.PX;

/**
 * Resizable DataGrid Header.
 * Source: https://github.com/gchatelet/GwtResizableDraggableColumns/blob/master/src/fr/mikrosimage/gwt/client/ResizableHeader.java
 *
 * @param <T> the row type of the grid
 */
public abstract class ResizableHeader<T> extends Header<String> {

    private static final String RESIZE = "Resize";
    private static final String RESIZE_tt = "Click and drag to resize column";
    private static final Style.Cursor resizeCursor = Cursor.COL_RESIZE;
    private static final String RESIZE_COLOR = "#A49AED";
    private static final String FOREGROUND_COLOR = "white";
    private static final int MINIMUM_COLUMN_WIDTH = 50;

    private static final int RESIZE_HANDLE_WIDTH = 34;

    private final String title;
    private final Document document = Document.get();
    private final AbstractCellTable<T> table;
    private final Element tableElement;
    private HeaderHelper current;
    protected final Column<T, ?> column;
    private final String resizeStyle;
    private final String resizeToolTip;

    public ResizableHeader(String title, AbstractCellTable<T> table, Column<T, ?> column) {
        this(title, table, column, null, null);
    }

    public ResizableHeader(String title, AbstractCellTable<T> table, Column<T, ?> column,
                           String resizeStyle, String resizeToolTip) {
        super(new HeaderCell());
        if (title == null || table == null || column == null)
            throw new NullPointerException();
        this.title = title;
        this.column = column;
        this.table = table;
        this.tableElement = table.getElement();
        this.resizeStyle = resizeStyle;
        this.resizeToolTip = resizeToolTip;
    }

    @Override
    public String getValue() {
        return title;
    }

    @Override
    public void onBrowserEvent(Context context, Element target, NativeEvent event) {
        if (current == null) {
            current = new HeaderHelper(target, event);
        }
    }

    private static NativeEvent getEventAndPreventPropagation(NativePreviewEvent event) {
        final NativeEvent nativeEvent = event.getNativeEvent();
        nativeEvent.preventDefault();
        nativeEvent.stopPropagation();
        return nativeEvent;
    }

    private static void setLine(Style style, int width, int top, int height, String color) {
        style.setPosition(Position.ABSOLUTE);
        style.setTop(top, PX);
        style.setHeight(height, PX);
        style.setWidth(width, PX);
        style.setBackgroundColor(color);
    }

    private class HeaderHelper implements NativePreviewHandler, DragCallback {
        private final HandlerRegistration handler = Event.addNativePreviewHandler(this);
        private final Element source;
        private boolean dragging;
        final Element span;

        public HeaderHelper(Element target, NativeEvent event) {
            this.source = target;
            event.preventDefault();
            event.stopPropagation();
            final int leftBound = target.getOffsetLeft() + target.getOffsetWidth();
            if (resizeStyle != null) {
                span = createSpanElement(resizeStyle, resizeToolTip, leftBound - RESIZE_HANDLE_WIDTH);
            } else {
                span = createSpanElement(RESIZE, RESIZE_tt, RESIZE_COLOR, resizeCursor, leftBound - RESIZE_HANDLE_WIDTH);
            }
            source.appendChild(span);
        }

        private SpanElement createSpanElement(String styleClassName, String title, double left) {
            final SpanElement span = document.createSpanElement();
            span.setClassName(styleClassName);
            if (title != null) {
                span.setTitle(title);
            }
            final Style style = span.getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setBottom(0, PX);
            style.setHeight(source.getOffsetHeight(), PX);
            style.setTop(source.getOffsetTop(), PX);
            style.setWidth(RESIZE_HANDLE_WIDTH, PX);
            style.setLeft(left, PX);
            return span;
        }

        private SpanElement createSpanElement(String innerText, String title, String backgroundColor, Cursor cursor, double left) {
            final SpanElement span = document.createSpanElement();
            span.setInnerText(innerText);
            span.setAttribute("title", title);
            final Style style = span.getStyle();
            style.setCursor(cursor);
            style.setPosition(Position.ABSOLUTE);
            style.setBottom(0, PX);
            style.setHeight(source.getOffsetHeight(), PX);
            style.setTop(source.getOffsetTop(), PX);
            style.setColor(FOREGROUND_COLOR);
            style.setWidth(RESIZE_HANDLE_WIDTH, PX);
            style.setLeft(left, PX);
            style.setBackgroundColor(backgroundColor);
            return span;
        }

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            final NativeEvent natEvent = event.getNativeEvent();
            final Element target = natEvent.getEventTarget().cast();
            final String eventType = natEvent.getType();

            if (!(target == span)) {
                if (MOUSEDOWN.equals(eventType)) {
                    //No need to do anything, the event will be passed on to the column sort handler
                } else if (!dragging && MOUSEOVER.equals(eventType)) {
                    cleanUp();
                }
                return;
            }

            final NativeEvent nativeEvent = getEventAndPreventPropagation(event);
            if (MOUSEDOWN.equals(eventType)) {
                span.removeFromParent();
                new ColumnResizeHelper(this, source, span, nativeEvent);
                dragging = true;
            }
        }

        private void cleanUp() {
            handler.removeHandler();
            span.removeFromParent();
            current = null;
        }

        public void dragFinished() {
            dragging = false;
            cleanUp();
        }
    }

    private class ColumnResizeHelper implements NativePreviewHandler {
        private final HandlerRegistration handler = Event.addNativePreviewHandler(this);
        private final DivElement resizeLine = document.createDivElement();
        private final Style resizeLineStyle = resizeLine.getStyle();
        private final Element header;
        private final DragCallback dragCallback;
        private final Element caret;

        private ColumnResizeHelper(DragCallback dragCallback, Element header, Element caret, NativeEvent event) {
            this.dragCallback = dragCallback;
            this.header = header;
            this.caret = caret;
            setLine(resizeLineStyle, 2, header.getAbsoluteTop() + header.getOffsetHeight(), getTableBodyHeight(), RESIZE_COLOR);
            moveLine(event.getClientX());
            tableElement.appendChild(resizeLine);
        }

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            final NativeEvent nativeEvent = getEventAndPreventPropagation(event);
            final int clientX = nativeEvent.getClientX();
            final String eventType = nativeEvent.getType();
            if (MOUSEMOVE.equals(eventType)) {
                moveLine(clientX);
            } else if (MOUSEUP.equals(eventType)) {
                handler.removeHandler();
                resizeLine.removeFromParent();
                dragCallback.dragFinished();
                resizeColumn(column, Math.max(clientX - header.getAbsoluteLeft(), MINIMUM_COLUMN_WIDTH));
            }
        }

        private void moveLine(final int clientX) {
            final int xPos = clientX - table.getAbsoluteLeft();
            caret.getStyle().setLeft(xPos - caret.getOffsetWidth() / 2, PX);
            resizeLineStyle.setLeft(xPos, PX);
            resizeLineStyle.setTop(header.getOffsetHeight(), PX);
        }
    }

    protected abstract int getTableBodyHeight();

    protected abstract void resizeColumn(Column<T, ?> column, int width);

    private static class HeaderCell extends AbstractCell<String> {
        public HeaderCell() {
            super(MOUSEMOVE);
        }

        @Override
        public void render(Context context, String value, SafeHtmlBuilder sb) {
            sb.append(SafeHtmlUtils.fromString(value));
        }
    }

    private static interface DragCallback {
        void dragFinished();
    }
}