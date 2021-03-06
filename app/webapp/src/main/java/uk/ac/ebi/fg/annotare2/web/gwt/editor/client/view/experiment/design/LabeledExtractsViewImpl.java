package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.LabeledExtractsRow;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractsViewImpl extends Composite implements LabeledExtractsView {

    private final GridView<LabeledExtractsRow> gridView;
    private final ListBox labelList;
    private Presenter presenter;

    public LabeledExtractsViewImpl() {
        gridView = new GridView<LabeledExtractsRow>();
        labelList = new ListBox();
        labelList.addItem("labels", "");
        labelList.addItem("Cy3", "Cy3");
        labelList.addItem("Cy5", "Cy5");
        labelList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                assignLabel();
                labelList.setItemSelected(0, true);
            }
        });
        gridView.addTool(labelList);
        initWidget(gridView);
    }

    @Override
    public void setData(List<LabeledExtractsRow> rows, List<String> labels) {
        gridView.setRows(rows);
        setColumns(labels);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void setColumns(List<String> labels) {
        addNameColumn();
        addLabelColumn();
    }

    private void addNameColumn() {
        Column<LabeledExtractsRow, String> column = new Column<LabeledExtractsRow, String>(new TextCell()) {
            @Override
            public String getValue(LabeledExtractsRow row) {
                return row.getName();
            }
        };
        column.setSortable(true);
        Comparator<LabeledExtractsRow> comparator = new Comparator<LabeledExtractsRow>() {
            @Override
            public int compare(LabeledExtractsRow o1, LabeledExtractsRow o2) {
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
        Column<LabeledExtractsRow, Set<String>> column = new Column<LabeledExtractsRow, Set<String>>(new LabelsCell()) {
            @Override
            public Set<String> getValue(LabeledExtractsRow row) {
                return row.getLabels();
            }
        };
        column.setFieldUpdater(new FieldUpdater<LabeledExtractsRow, Set<String>>() {
            @Override
            public void update(int index, LabeledExtractsRow row, Set<String> labels) {
                row.setLabels(labels);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn("Labels", column, null, 350, Style.Unit.PX);
    }

    private void assignLabel() {
        Set<LabeledExtractsRow> selectedRows = gridView.getSelectedRows();
        if (selectedRows.isEmpty()) {
            return;
        }

        String label = labelList.getValue(labelList.getSelectedIndex());
        if (label.isEmpty()) {
            return;
        }

        for(LabeledExtractsRow row : selectedRows) {
            if (row.addLabel(label)) {
                updateRow(row);
                }
            }
        gridView.redraw();
    }

    private void updateRow(LabeledExtractsRow row) {
        presenter.updateRow(row.copy());
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
