package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExtractLabelsRow;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractsViewImpl extends Composite implements LabeledExtractsView {

    private final GridView<ExtractLabelsRow> gridView;

    public LabeledExtractsViewImpl() {
        gridView = new GridView<ExtractLabelsRow>();
        Button button = new Button("Add label");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        });
        gridView.addTool(button);
        initWidget(gridView);
    }

    @Override
    public void setData(List<ExtractLabelsRow> rows, List<String> labels) {
        gridView.setRows(rows);
        setColumns(labels);
    }

    private void setColumns(List<String> labels) {
        addNameColumn();
        addLabelColumn();
       /* for(String label : labels) {
            addColumn(label);
        }*/
    }

    private void addNameColumn() {
        Column<ExtractLabelsRow, String> column = new Column<ExtractLabelsRow, String>(new TextCell()) {
            @Override
            public String getValue(ExtractLabelsRow row) {
                return row.getName();
            }
        };
        column.setSortable(true);
        Comparator<ExtractLabelsRow> comparator = new Comparator<ExtractLabelsRow>() {
            @Override
            public int compare(ExtractLabelsRow o1, ExtractLabelsRow o2) {
                if (o1 == o2) {
                    return 0;
                }
                String v1 = o1.getName();
                String v2 = o2.getName();
                return v1.compareTo(v2);
            }
        };
        gridView.addPermanentColumn("Extract Name", column, comparator, 150, Style.Unit.PX);
    }

    private void addLabelColumn() {
        Column<ExtractLabelsRow, Set<String>> column = new Column<ExtractLabelsRow, Set<String>>(new LabelsCell()) {
            @Override
            public Set<String> getValue(ExtractLabelsRow row) {
                return row.getLabels();
            }
        };
        column.setFieldUpdater(new FieldUpdater<ExtractLabelsRow, Set<String>>() {
            @Override
            public void update(int index, ExtractLabelsRow row, Set<String> labels) {
                row.setLabels(labels);
                //TODO update row
            }
        });
        gridView.addPermanentColumn("Labels", column, null, 350, Style.Unit.PX);
    }

    private void addColumn(final String label) {
        Column<ExtractLabelsRow, String> column = new Column<ExtractLabelsRow, String>(new TextCell()) {
            @Override
            public String getValue(ExtractLabelsRow object) {
                return object.hasLabel(label) ? "+" : "";
            }
        };
        gridView.addColumn(label, column, null, 150, Style.Unit.PX);
    }


    static class LabelsCell extends AbstractCell<Set<String>> {

        interface Templates extends SafeHtmlTemplates {
            @SafeHtmlTemplates.Template(
                    "<div class=\"app-RemovableItem\"><div class=\"app-ItemRemoveButton\"></div><div>{0}</div></div>")
            SafeHtml item(SafeHtml value);
        }

        private static Templates templates = GWT.create(Templates.class);

        LabelsCell() {
            super("click");
        }

        @Override
        public void render(Context context, Set<String> values, SafeHtmlBuilder sb) {
            if (values == null) {
                return;
            }
            sb.appendHtmlConstant("<div>");
            for (String value : values) {
                sb.append(templates.item(SafeHtmlUtils.fromString(value)));
            }
            sb.appendHtmlConstant("</div>");
        }

        @Override
        public void onBrowserEvent(Context context, Element parent, Set<String> value, NativeEvent event,
                                   ValueUpdater<Set<String>> valueUpdater) {
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("click".equals(event.getType())) {
                EventTarget eventTarget = event.getEventTarget();
                Element target = Element.as(eventTarget);
                if (parent.getFirstChildElement().isOrHasChild(target)) {
                    String itemToRemove = target.getParentElement().getInnerText();
                    Set<String>newValue = updateValue(value, itemToRemove, valueUpdater);
                    setValue(context, parent, newValue);
                }
            }
        }

        private Set<String> updateValue(Set<String> value, String itemToeRemove, ValueUpdater<Set<String>> valueUpdater) {
            Set<String> newValue = new LinkedHashSet<String>(value);
            newValue.remove(itemToeRemove);
            valueUpdater.update(newValue);
            return newValue;
        }
    }

}
