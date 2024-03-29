package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemSelectionEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources.EditorResources;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ListView<T> extends Composite {

    interface Binder extends UiBinder<Widget, ListView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    protected VerticalPanel listPanel;

    @UiField
    protected Image addIcon;

    @UiField
    protected Image removeIcon;

    @UiField
    SpanElement messageSpan;

    private int selection = 0;

    public ListView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @UiFactory
    public EditorResources getResources() {
        EditorResources.EDITOR_RESOURCES.editorStyles().ensureInjected();
        return EditorResources.EDITOR_RESOURCES;
    }

    protected DisclosureListItem getItem(int index) {
        return (DisclosureListItem) listPanel.getWidget(index);
    }

    protected List<DisclosureListItem> getItems() {
        List<DisclosureListItem> items = new ArrayList<DisclosureListItem>();
        for (int i = 0; i < listPanel.getWidgetCount(); i++) {
            items.add((DisclosureListItem) listPanel.getWidget(i));
        }
        return items;
    }

    protected DisclosureListItem addListItem(ItemView<T> itemView) {
        DisclosureListItem item = new DisclosureListItem(itemView);
        item.openPanel();
        listPanel.add(item);
        item.addItemSelectionHandler(new ItemSelectionEventHandler() {
            @Override
            public void onSelect(boolean selected) {
                if (selected) {
                    selection++;
                } else if (selection > 0) {
                    selection--;
                }
            }
        });
        return item;
    }

    protected List<Integer> getSelected() {
        if (selection == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> selected = new ArrayList<Integer>();
        int size = listPanel.getWidgetCount();
        for (int i = size - 1; i >= 0; i--) {
            DisclosureListItem item = (DisclosureListItem) listPanel.getWidget(i);
            if (item.isSelected()) {
                selected.add(i);
            }
        }
        return selected;
    }

    protected int removeItems(List<Integer> indices) {
        int count = 0;
        for (Integer i : indices) {
            if (listPanel.remove(i)) {
                count++;
            }
        }
        return count;
    }

    protected void clear() {
        listPanel.clear();
    }
}
