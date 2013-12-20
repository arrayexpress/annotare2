package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.LabeledExtractRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.AsyncOptionProvider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.AsyncSelectionCell;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtractsViewImpl extends Composite implements LabeledExtractsView {

    private final GridView<LabeledExtractRow> gridView;
    private Presenter presenter;
    private AsyncOptionProvider labelProvider;

    public LabeledExtractsViewImpl() {
        gridView = new GridView<LabeledExtractRow>();
        initWidget(gridView);

        labelProvider = new AsyncOptionProvider() {
            @Override
            public void update(final Callback callback) {
                if (presenter == null) {
                    callback.setOptions(Collections.<String>emptyList());
                } else {
                    presenter.loadLabels(new AsyncCallback<List<String>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            callback.setOptions(Collections.<String>emptyList());
                        }

                        @Override
                        public void onSuccess(List<String> result) {
                            callback.setOptions(result);
                        }
                    });
                }

            }
        };
    }

    @Override
    public void setData(List<LabeledExtractRow> rows) {
        gridView.setRows(rows);
        setColumns();
        labelProvider.update();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void setColumns() {
        addNameColumn();
        addLabelColumn();
    }

    private void addNameColumn() {
        Column<LabeledExtractRow, String> column = new Column<LabeledExtractRow, String>(new TextCell()) {
            @Override
            public String getValue(LabeledExtractRow row) {
                return row.getName();
            }
        };
        column.setSortable(true);
        Comparator<LabeledExtractRow> comparator = new Comparator<LabeledExtractRow>() {
            @Override
            public int compare(LabeledExtractRow o1, LabeledExtractRow o2) {
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
        Column<LabeledExtractRow, String> column = new Column<LabeledExtractRow, String>(new AsyncSelectionCell(labelProvider)) {
            @Override
            public String getValue(LabeledExtractRow row) {
                return row.getLabel();
            }
        };
        column.setFieldUpdater(new FieldUpdater<LabeledExtractRow, String>() {
            @Override
            public void update(int index, LabeledExtractRow row, String label) {
                row.setLabel(label);
                updateRow(row);
            }
        });
        gridView.addPermanentColumn("Label", column, null, 350, Style.Unit.PX);
    }

    private void updateRow(LabeledExtractRow row) {
        presenter.updateRow(row.copy());
    }

    /*static class LabelsCell extends AbstractCell<Set<String>> {

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
    }*/
}
