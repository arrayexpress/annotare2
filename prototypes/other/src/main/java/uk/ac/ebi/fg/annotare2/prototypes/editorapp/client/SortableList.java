package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SortableList extends Composite implements IsWidget {

    private VerticalPanel panel = new VerticalPanel();

    private List<Integer> indices = new ArrayList<Integer>();

    public SortableList() {
        initWidget(panel);
        addBlankDropTarget();
    }

    public void addWidget(Widget w) {
        int index = panel.getWidgetCount() - 1;
        panel.insert(droppable(draggable(container(w), index), index), panel.getWidgetCount() - 1);
        indices.add(index);
    }

    private void addBlankDropTarget() {
        SimplePanel sp = droppable(container(null), -1);
        sp.setHeight("100px");
        panel.add(sp);
    }

    private SimplePanel container(Widget w) {
        SimplePanel container = new SimplePanel();
        container.setWidth("100%");
        container.addStyleName("dnd-container");
        if (w != null) {
            container.add(w);
        }
        return container;
    }

    private SimplePanel draggable(final SimplePanel w, final int idx) {
        w.getElement().setDraggable(Element.DRAGGABLE_TRUE);

        w.addDomHandler(new DragStartHandler() {
            public void onDragStart(DragStartEvent event) {
                event.setData("data", idx + "");
                int dx = event.getNativeEvent().getClientX() - w.getElement().getAbsoluteLeft();
                event.getDataTransfer().setDragImage(w.getElement(), dx, 10);
                w.addStyleName("dnd-opacity");
            }
        }, DragStartEvent.getType());

        w.addDomHandler(new DragEndHandler() {
            public void onDragEnd(DragEndEvent event) {
                w.removeStyleName("dnd-opacity");
            }
        }, DragEndEvent.getType());

        return w;
    }

    private SimplePanel droppable(final SimplePanel w, final int idx) {

        w.addDomHandler(new DragEnterHandler() {
            public void onDragEnter(DragEnterEvent event) {

            }
        }, DragEnterEvent.getType());

        w.addDomHandler(new DragLeaveHandler() {
            public void onDragLeave(DragLeaveEvent event) {
                w.removeStyleName("dnd-over");
            }
        }, DragLeaveEvent.getType());

        w.addDomHandler(new DragOverHandler() {
            public void onDragOver(DragOverEvent event) {
                w.addStyleName("dnd-over");
            }
        }, DragOverEvent.getType());

        w.addDomHandler(new DropHandler() {
            public void onDrop(DropEvent event) {
                w.removeStyleName("dnd-over");

                int selected = Integer.parseInt(event.getData("data"));

                int dropFrom = indices.indexOf(selected);
                int dropTo = idx < 0 ? indices.size() : indices.indexOf(idx);

                if (Math.abs(dropFrom - dropTo) > 1) {
                    Widget w = panel.getWidget(dropFrom);
                    panel.remove(w);
                    indices.remove(dropFrom);

                    panel.insert(w, dropTo - 1);
                    indices.add(dropTo - 1, selected);
                }
                GWT.log("" + indices);
            }
        }, DropEvent.getType());

        return w;
    }
}
