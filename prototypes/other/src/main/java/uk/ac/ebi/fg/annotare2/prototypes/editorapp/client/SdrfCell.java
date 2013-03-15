package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PopupPanel;

import static com.google.gwt.dom.client.BrowserEvents.*;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class SdrfCell extends AbstractEditableCell<String, SdrfCell.ViewData> {

    private static final int ESCAPE = 27;

    interface Template extends SafeHtmlTemplates {
        @Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\" style=\"width:100%;\"></input>")
        SafeHtml input(String value);
    }

    static class ViewData {

        private boolean isEditing;
        private String original;
        private String text;

        public ViewData(String text) {
            text = text == null ? "" : text;
            this.original = text;
            this.text = text;
            this.isEditing = true;
        }

        public String getOriginal() {
            return original;
        }

        public String getText() {
            return text;
        }

        public boolean isEditing() {
            return isEditing;
        }

        public void setEditing(boolean isEditing) {
            this.isEditing = isEditing;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ViewData viewData = (ViewData) o;

            if (isEditing != viewData.isEditing) return false;
            if (original != null ? !original.equals(viewData.original) : viewData.original != null) return false;
            if (text != null ? !text.equals(viewData.text) : viewData.text != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (isEditing ? 1 : 0);
            result = 31 * result + (original != null ? original.hashCode() : 0);
            result = 31 * result + (text != null ? text.hashCode() : 0);
            return result;
        }
    }

    private static Template template;

    private final SafeHtmlRenderer<String> renderer;

    private PopupPanel panel;
    private SdrfCellOptions optionList;

    public SdrfCell() {
        this(SimpleSafeHtmlRenderer.getInstance());
    }

    public SdrfCell(SafeHtmlRenderer<String> renderer) {
        super(CLICK, KEYUP, KEYDOWN, BLUR);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        if (renderer == null) {
            throw new IllegalArgumentException("renderer == null");
        }
        this.renderer = renderer;
        this.optionList = new SdrfCellOptions(asList("one", "two", "three"));
        this.panel = new PopupPanel(true, true) {
            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                        // Dismiss when escape is pressed
                        panel.hide();
                    }
                }
            }
        };
        panel.add(optionList);
    }

    @Override
    public boolean isEditing(Context context, Element parent, String value) {
        ViewData viewData = getViewData(context.getKey());
        return viewData != null && viewData.isEditing();
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        // Get the view data.
        Object key = context.getKey();
        ViewData viewData = getViewData(key);
        if (viewData != null && !viewData.isEditing() && value != null
                && value.equals(viewData.getText())) {
            clearViewData(key);
            viewData = null;
        }

        String toRender = value;
        if (viewData != null) {
            String text = viewData.getText();
            if (viewData.isEditing()) {
                // Do not use the renderer in edit mode because the value of a text
                // input element is always treated as text. SafeHtml isn't valid in the
                // context of the value attribute.
                sb.append(template.input(text));
                return;
            } else {
                // The user pressed enter, but view data still exists.
                toRender = text;
            }
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
    public void onBrowserEvent(Context context, Element parent, String value,
                               NativeEvent event, ValueUpdater<String> valueUpdater) {
        Object key = context.getKey();
        ViewData viewData = getViewData(key);
        if (viewData != null && viewData.isEditing()) {
            // Handle the edit event.
            editEvent(context, parent, value, viewData, event, valueUpdater);
        } else {
            String type = event.getType();
            int keyCode = event.getKeyCode();
            boolean enterPressed = KEYUP.equals(type)
                    && keyCode == KeyCodes.KEY_ENTER;
            if (CLICK.equals(type) || enterPressed) {
                // Go into edit mode.
                if (viewData == null) {
                    viewData = new ViewData(value);
                    setViewData(key, viewData);
                } else {
                    viewData.setEditing(true);
                }
                edit(context, parent, value);
            }
        }
    }

    protected void editEvent(Context context, Element parent, String value, ViewData viewData, NativeEvent event, ValueUpdater<String> valueUpdater) {
        String type = event.getType();
        boolean keyUp = KEYUP.equals(type);
        boolean keyDown = KEYDOWN.equals(type);
        if (keyUp || keyDown) {
            int keyCode = event.getKeyCode();
            if (keyUp && keyCode == KeyCodes.KEY_ENTER) {
                commit(context, parent, viewData, valueUpdater);
            } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
                setViewData(context.getKey(), null);
                cancel(context, parent, value);
            } else {
                updateViewData(parent, viewData, true);
            }
        } else if (BLUR.equals(type)) {
            // Commit the change. Ensure that we are blurring the input element and
            // not the parent element itself.
            EventTarget eventTarget = event.getEventTarget();
            if (Element.is(eventTarget)) {
                Element target = Element.as(eventTarget);
                if ("input".equals(target.getTagName().toLowerCase())) {
                    commit(context, parent, viewData, valueUpdater);
                }
            }
        }
    }

    private void commit(Context context, Element parent, ViewData viewData,
                        ValueUpdater<String> valueUpdater) {
        String value = updateViewData(parent, viewData, false);
        clearInput(getInputElement(parent));
        setValue(context, parent, viewData.getOriginal());
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
    }

    private void cancel(Context context, Element parent, String value) {
        clearInput(getInputElement(parent));
        setValue(context, parent, value);
    }

    private String updateViewData(Element parent, ViewData viewData,
                                  boolean isEditing) {
        InputElement input = (InputElement) parent.getFirstChild();
        String value = input.getValue();
        viewData.setText(value);
        viewData.setEditing(isEditing);
        return value;
    }


    private void edit(Context context, Element parent, String value) {
        setValue(context, parent, value);
        InputElement input = getInputElement(parent);
        input.focus();
        input.select();
        showPopup(input);
    }

    private void showPopup(final InputElement input) {
        if (panel.isAttached()) {
            panel.hide();
        }
        panel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                panel.setPopupPosition(input.getAbsoluteLeft() + 10,
                        input.getAbsoluteTop() + 10);
            }
        });
    }

    private InputElement getInputElement(Element parent) {
        return parent.getFirstChild().cast();
    }

    private native void clearInput(Element input) /*-{
        if (input.selectionEnd)
            input.selectionEnd = input.selectionStart;
        else if ($doc.selection)
            $doc.selection.clear();
    }-*/;

}
