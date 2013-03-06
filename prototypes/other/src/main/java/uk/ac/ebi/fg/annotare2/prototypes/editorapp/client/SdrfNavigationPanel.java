package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class SdrfNavigationPanel extends FlexTable implements IsWidget {

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

    interface Template extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<tr onclick=\"\" class=\"{0}\">{1}</tr>")
        SafeHtml tr(String classes, SafeHtml contents);

        @SafeHtmlTemplates.Template("<td class=\"{0}\">{1}</td>")
        SafeHtml td(String classes, SafeHtml contents);

        @SafeHtmlTemplates.Template("<div class=\"{0}\">{1}</div>")
        SafeHtml div(String classes, SafeHtml contents);
    }

    static List<String> order = asList("Sources", "Samples", "Extracts", "Labeled Extracts", "Assays", "Scans", "Array Data Files", "Normalizations", "Derived Array Data Files");

    static Template template;

    static Resources DEFAULT_RESOURCES;

    private final Style style;

    private final Resources resources;

    private final List<String> sections = new ArrayList<String>();

    final FlexTable table;

    private int i1 = -1, i2 = -1;


    private DecoratedPopupPanel popup;

    public SdrfNavigationPanel() {
        resources = getDefaultResources();
        style = resources.style();
        style.ensureInjected();

        if (template == null) {
            template = GWT.create(Template.class);
        }

        table = this;
        table.setCellSpacing(0);
        table.getElement().getStyle().setWidth(100.0, Unit.PCT);
        table.getElement().getStyle().setTableLayout(TableLayout.FIXED);
        addEmptyRow();

        addDomHandler(new MouseMoveHandler() {
            public void onMouseMove(MouseMoveEvent event) {
                tdMouseMove(event);
            }
        }, MouseMoveEvent.getType());
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

            GWT.log("i1 = " + i1 + ", i2 = " + i2);
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
            for (final String o : order) {
                menuBar.addItem(o, new Command() {
                    public void execute() {
                        addCell(o);
                        popup.hide();
                    }
                });
            }

            popup = new DecoratedPopupPanel(true);
            popup.setWidget(menuBar);
        }
        return popup;
    }


    private void addCell(String text) {
        // todo preserve ordering
        sections.add(text);

        int row = table.insertRow(table.getRowCount() - 1);
        table.insertCell(row, 0);
        table.insertCell(row, 0);

        table.getFlexCellFormatter().addStyleName(row, 0, style.tdWall());

        final SimplePanel cell = new SimplePanel(new Label(text));
        cell.addStyleName(style.cell());
        table.setWidget(row, 1, cell);

        cell.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //todo open section
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

    private class TableCell extends Cell {
        public TableCell(int row, int column) {
            super(row, column);
        }
    }
}
