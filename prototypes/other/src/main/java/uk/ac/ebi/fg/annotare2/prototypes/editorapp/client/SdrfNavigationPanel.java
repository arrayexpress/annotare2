package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class SdrfNavigationPanel extends Widget {

    interface Template extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<tr onclick=\"\" class=\"{0}\">{1}</tr>")
        SafeHtml tr(String classes, SafeHtml contents);

        @SafeHtmlTemplates.Template("<td class=\"{0}\">{1}</td>")
        SafeHtml td(String classes, SafeHtml contents);
    }

    static Template template;

    private final TableElement tableElem;

    private final TableSectionElement body;

    public SdrfNavigationPanel() {
        tableElem = Document.get().createTableElement();
        tableElem.setCellSpacing(0);
        tableElem.getStyle().setTableLayout(Style.TableLayout.FIXED);
        tableElem.getStyle().setWidth(100.0, Style.Unit.PCT);
        setElement(tableElem);

        body = Document.get().createTBodyElement();
        tableElem.appendChild(body);
    }

   /* public void add(String text) {
        SafeHtmlBuilder

        SafeHtmlBuilder trBuilder = new SafeHtmlBuilder();
        trBuilder.append()
    }*/

}
