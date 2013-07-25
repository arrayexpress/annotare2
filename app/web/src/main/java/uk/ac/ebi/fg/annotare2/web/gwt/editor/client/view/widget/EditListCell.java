package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PopupPanel;

import java.util.List;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

/**
 * @author Olga Melnichuk
 */
public class EditListCell extends AbstractEditableCell<List<String>, List<String>> {

    private static final int ESCAPE = 27;

    private final EditableList editableList;
    private final PopupPanel panel;
    private ValueUpdater<List<String>> valueUpdater;

    private final SafeHtmlRenderer<String> renderer;

    private Object lastKey;
    private List<String> lastValue;
    private int lastIndex;
    private int lastColumn;
    private Element lastParent;

    public EditListCell() {
        this(SimpleSafeHtmlRenderer.getInstance());
    }

    public EditListCell(SafeHtmlRenderer<String> renderer) {
        super(CLICK, KEYDOWN);

        this.renderer = renderer;

        editableList = new EditableList();
        editableList.setHeight("200px");
        editableList.setWidth("300px");

        panel = new PopupPanel(true, true) {
            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                        panel.hide();
                    }
                }
            }
        };
        panel.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {
                lastKey = null;
                lastValue = null;
                lastIndex = -1;
                lastColumn = -1;
                if (lastParent != null && !event.isAutoClosed()) {
                    // Refocus on the containing cell after the user selects a value, but
                    // not if the popup is auto closed.
                    lastParent.focus();
                }
                lastParent = null;
            }
        });
        panel.add(editableList);

        editableList.addValueChangeHandler(new ValueChangeHandler<List<String>>() {
            public void onValueChange(ValueChangeEvent<List<String>> event) {
                Element cellParent = lastParent;
                List<String> oldValue = lastValue;
                Object key = lastKey;
                int index = lastIndex;
                int column = lastColumn;
                panel.hide();

                List<String> value = event.getValue();
                setViewData(key, value);
                setValue(new Context(index, column, key), cellParent, oldValue);
                if (valueUpdater != null) {
                    valueUpdater.update(value);
                }
            }
        });
    }

    @Override
    public boolean isEditing(Context context, Element parent, List<String> value) {
        return lastKey != null && lastKey.equals(context.getKey());
    }

    @Override
    public void render(Context context, List<String> value, SafeHtmlBuilder sb) {
        Object key = context.getKey();
        List<String> viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        String toRender = null;
        if (viewData != null) {
            toRender = join(viewData);
        } else if (value != null) {
            toRender = join(value);
        }

        if (toRender != null && toRender.trim().length() > 0) {
            sb.append(renderer.render(toRender));
        } else {
            // Render a blank space to force the rendered element to have a height.
            // Otherwise it is not clickable.
            sb.appendHtmlConstant("\u00A0");
        }
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, List<String> value,
                               NativeEvent event, ValueUpdater<List<String>> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if (CLICK.equals(event.getType())) {
            onEnterKeyDown(context, parent, value, event, valueUpdater);
        }
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, List<String> value,
                                  NativeEvent event, ValueUpdater<List<String>> valueUpdater) {
        this.lastKey = context.getKey();
        this.lastParent = parent;
        this.lastValue = value;
        this.lastIndex = context.getIndex();
        this.lastColumn = context.getColumn();
        this.valueUpdater = valueUpdater;

        List<String> viewData = getViewData(lastKey);
        editableList.setValue((viewData == null) ? lastValue : viewData, false);
        panel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                panel.setPopupPosition(lastParent.getAbsoluteLeft() + 10,
                        lastParent.getAbsoluteTop() + 10);
            }
        });
    }

    private static String join(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for(String v : values) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(v);
        }
        return sb.toString();
    }
}
