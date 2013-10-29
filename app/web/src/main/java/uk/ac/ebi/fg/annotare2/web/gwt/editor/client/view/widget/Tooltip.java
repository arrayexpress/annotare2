package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class Tooltip extends PopupPanel {

    private final Label label;
    private UIObject current;

    public Tooltip() {
        label = new Label();
        add(label);
    }

    public void attach(Widget widget, final String text) {
        widget.addDomHandler(
                new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        mouseOver(event, text);
                    }
                }, MouseOverEvent.getType());

        widget.addDomHandler(
                new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        mouseOut(event);
                    }
                }, MouseOutEvent.getType());
    }

    private void mouseOver(final MouseOverEvent event, String text) {
        final UIObject target = (UIObject) event.getSource();

        setPopupPositionAndShow(new PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                setPopupPosition(target.getAbsoluteLeft() + event.getX(), target.getAbsoluteTop() - offsetHeight);
            }
        });
        label.setText(text);
        current = target;
    }

    private void mouseOut(MouseOutEvent event) {
        UIObject target = (UIObject) event.getSource();

        if (current != null && current.equals(target)) {
            hide();
        }
    }
}
