package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.HasSelectionHandlers;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEvent;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEventHandler;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Olga Melnichuk
 */
public class SdrfNavigationPanel extends FlexTable implements IsWidget, HasSelectionHandlers<SdrfNavigationPanel.Item> {

    public interface Resources extends ClientBundle {

        @Source("../public/triangle.png")
        ImageResource removeIcon();

        @Source("../public/SdrfNavigationPanel.css")
        Style style();
    }

    /**
     * Styles used by this widget.
     */
    public interface Style extends CssResource {

        String cell();

        String hoveredCell();

        String selectedCell();

        String tdWall();

        String hoveredTdWall();

        String selectedTdWall();

        String removeIcon();

        String emptyCell();
    }

    static Resources DEFAULT_RESOURCES;

    private final Style style;

    private final Resources resources;

    private final List<SdrfSection> sections = new ArrayList<SdrfSection>();

    final FlexTable table;

    private int i1 = -1, i2 = -1;
    private int selected1 = -1, selected2 = -1;

    private DecoratedPopupPanel popup;

    public SdrfNavigationPanel() {
        resources = getDefaultResources();
        style = resources.style();
        style.ensureInjected();

        table = this;
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.getElement().getStyle().setWidth(100.0, Unit.PCT);
        table.getElement().getStyle().setTableLayout(TableLayout.FIXED);
        addEmptyRow();
        for (SdrfSection s : SdrfSection.values()) {
            addRow(s);
        }

        addDomHandler(new MouseMoveHandler() {
            public void onMouseMove(MouseMoveEvent event) {
                tdMouseMove(event);
            }
        }, MouseMoveEvent.getType());

        addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                tdClick(event);
            }
        }, ClickEvent.getType());
    }

    private void tdClick(ClickEvent event) {
        if (i1 >= 0 && i2 >= 0) {
            changeSelection(i1, i2);
            fireSelectionEvent(new Item(min(i1, i2), sections.get(min(i1, i2)), sections.get(max(i1, i2))));
        }
    }

    private void tdMouseMove(MouseMoveEvent event) {
        Cell cell = getCellForEvent(event.getNativeEvent());
        if (cell == null) {
            return;
        }

        if (cell.getCellIndex() != 0 || cell.getRowIndex() == getRowCount() - 1) {
            if (i1 >= 0 && i1 < getRowCount() - 1) {
                table.getFlexCellFormatter().removeStyleName(i1, 0, style.hoveredTdWall());
            }
            if (i2 >= 0 && i2 < getRowCount() - 1) {
                table.getFlexCellFormatter().removeStyleName(i2, 0, style.hoveredTdWall());
            }
            i1 = -1;
            i2 = -1;
            return;
        }

        GWT.log("Cell: " + cell.getRowIndex() + ", " + cell.getCellIndex());

        if (table.getRowCount() < 3) {
            return;
        }

        int row = cell.getRowIndex();
        Element td = table.getFlexCellFormatter().getElement(row, cell.getCellIndex());
        int row2 = row + 1;
        if (row != 0) {
            int top = td.getAbsoluteTop();
            int bottom = td.getAbsoluteBottom();
            int toTop = Math.abs(event.getY() - top);
            int toBottom = Math.abs(event.getY() - bottom);
            row2 = toTop > toBottom && (row < table.getRowCount() - 2) ? row + 1 : row - 1;
        }

        if (row != i1 || row2 != i2) {
            if (i1 >= 0) {
                table.getFlexCellFormatter().removeStyleName(i1, 0, style.hoveredTdWall());
            }
            if (i2 >= 0) {
                table.getFlexCellFormatter().removeStyleName(i2, 0, style.hoveredTdWall());
            }
            table.getFlexCellFormatter().addStyleName(row, 0, style.hoveredTdWall());
            table.getFlexCellFormatter().addStyleName(row2, 0, style.hoveredTdWall());
            i1 = row;
            i2 = row2;
        }
    }

    private Cell getCellForEvent(NativeEvent event) {
        Element td = getEventTargetCell(Event.as(event));
        if (td == null) {
            return null;
        }

        int row = TableRowElement.as(td.getParentElement()).getSectionRowIndex();
        int column = TableCellElement.as(td).getCellIndex();
        return new TableCell(row, column);
    }

    private static Resources getDefaultResources() {
        if (DEFAULT_RESOURCES == null) {
            DEFAULT_RESOURCES = GWT.create(Resources.class);
        }
        return DEFAULT_RESOURCES;
    }

    private void addEmptyRow() {
        table.insertRow(0);
        table.insertCell(0, 0);
        FlexTable.FlexCellFormatter formatter = table.getFlexCellFormatter();
        formatter.setColSpan(0, 0, 2);

        SimplePanel innerCell = new SimplePanel(new Label("Add"));
        innerCell.addStyleName(style.emptyCell());
        final SimplePanel outerCell = new SimplePanel(innerCell);
        outerCell.addStyleName(style.emptyCell());
        table.setWidget(0, 0, outerCell);

        outerCell.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showPopup(event);
            }
        }, ClickEvent.getType());

        outerCell.addDomHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                outerCell.addStyleName(style.hoveredCell());
            }
        }, MouseOverEvent.getType());

        outerCell.addDomHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                outerCell.removeStyleName(style.hoveredCell());
            }
        }, MouseOutEvent.getType());
    }

    private void showPopup(ClickEvent event) {
        PopupPanel p = createPopup();
        Widget source = (Widget) event.getSource();
        int x = source.getAbsoluteLeft() + source.getOffsetWidth();
        int y = source.getAbsoluteTop() + 10;
        p.setPopupPosition(x, y);
        p.show();
    }

    private PopupPanel createPopup() {
        if (popup == null) {
            MenuBar menuBar = new MenuBar(true);
            for (final SdrfSection o : SdrfSection.values()) {
                menuBar.addItem(o.getTitle(), new Command() {
                    public void execute() {
                        addRow(o);
                        popup.hide();
                    }
                });
            }

            popup = new DecoratedPopupPanel(true);
            popup.setWidget(menuBar);
        }
        return popup;
    }

    private void addRow(SdrfSection section) {
        sections.add(section);

        final int row = table.insertRow(table.getRowCount() - 1);
        table.insertCell(row, 0);
        table.insertCell(row, 0);

        table.getFlexCellFormatter().addStyleName(row, 0, style.tdWall());

        final SimplePanel cell = new SimplePanel(new Label(section.getTitle()));
        cell.addStyleName(style.cell());
        table.setWidget(row, 1, cell);

        cell.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                changeSelection(row, -1);
                fireSelectionEvent(new Item(row, sections.get(row)));
            }
        }, ClickEvent.getType());

        cell.addDomHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                cell.addStyleName(style.hoveredCell());
            }
        }, MouseOverEvent.getType());

        cell.addDomHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                cell.removeStyleName(style.hoveredCell());
            }
        }, MouseOutEvent.getType());
    }

    private void changeSelection(int index1, int index2) {
        if (selected1 >= 0) {
            table.getFlexCellFormatter().getElement(selected1, 0).removeClassName(style.selectedTdWall());
            table.getWidget(selected1, 1).removeStyleName(style.selectedCell());
        }
        if (selected2 >= 0) {
            table.getFlexCellFormatter().getElement(selected2, 0).removeClassName(style.selectedTdWall());
        }
        if (index2 < 0) {
            table.getWidget(index1, 1).addStyleName(style.selectedCell());
        } else {
            table.getFlexCellFormatter().getElement(index1, 0).addClassName(style.selectedTdWall());
            table.getFlexCellFormatter().getElement(index2, 0).addClassName(style.selectedTdWall());
        }
        selected1 = index1;
        selected2 = index2;
    }

    private void fireSelectionEvent(Item item) {
        SelectionEvent.fire(this, item);
    }

    private class TableCell extends Cell {
        public TableCell(int row, int column) {
            super(row, column);
        }
    }

    public HandlerRegistration addSelectionHandler(SelectionEventHandler<Item> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    public static class Item {
        private int index = -1;
        private SdrfSection section1;
        private SdrfSection section2;
        private boolean pair = false;

        public Item(int index, SdrfSection section1, SdrfSection section2) {
            this.index = index;
            this.section1 = section1;
            this.section2 = section2;
            pair = true;
        }

        public Item(int index, SdrfSection section1) {
            this.index = index;
            this.section1 = section1;
        }

        public SdrfSection getSection1() {
            return section1;
        }

        public SdrfSection getSection2() {
            return section2;
        }

        public boolean isPair() {
            return pair;
        }

        public boolean isFirst() {
            return index == 0;
        }
    }
}
