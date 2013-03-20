package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.PopupPanel;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEvent;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.event.SelectionEventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.gwt.dom.client.BrowserEvents.*;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public abstract class SdrfCell extends AbstractEditableCell<String, SdrfCell.ViewData> {

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

    private PopupPanel popup;
    private SdrfCellOptions optionList;

    private final List<String> options;
    private HashMap<String, Integer> indexForOption = new HashMap<String, Integer>();
    private Element lastParent;
    private Context lastContext;
    private ValueUpdater<String> valueUpdater;

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

        this.options = new ArrayList<String>(asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"));
        int index = 0;
        for (String option : options) {
            indexForOption.put(option, index++);
        }

        this.renderer = renderer;
        optionList = new SdrfCellOptions(options);
        optionList.addSelectionHandler(new SelectionEventHandler<SdrfCellOptions.Selection>() {
            @Override
            public void onSelection(SelectionEvent<SdrfCellOptions.Selection> event) {
                SdrfCellOptions.Selection sel = event.getSelection();
                if (sel.isCreateOption()) {
                    createOption();
                } else if (sel.isEditOption()) {
                    editOptions();
                } else {
                    setSelectionAndClose(sel.getValue());
                }
            }
        });

        popup = new PopupPanel(true, false) /*{
            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                        cancelAndClose();
                    }
                }
            }
        }*/;
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if (event.isAutoClosed()) {
                    cancel();
                }
                popupClosed();
            }
        });
        popup.setPreviewingAllNativeEvents(true);
        popup.add(optionList);
    }

    @Override
    public boolean isEditing(Context context, Element parent, String value) {
        ViewData viewData = getViewData(context.getKey());
        return viewData != null && viewData.isEditing();
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
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
        lastParent = parent;
        lastContext = context;
        this.valueUpdater = valueUpdater;

        Object key = context.getKey();
        ViewData viewData = getViewData(key);
        if (viewData != null && viewData.isEditing()) {
            editEvent(event, viewData);
        } else {
            String type = event.getType();
            int keyCode = event.getKeyCode();
            boolean enterPressed = KEYUP.equals(type)
                    && keyCode == KeyCodes.KEY_ENTER;
            if (CLICK.equals(type) || enterPressed) {
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

    protected void editEvent(NativeEvent event, ViewData viewData) {
        String type = event.getType();
        boolean keyUp = KEYUP.equals(type);
        boolean keyDown = KEYDOWN.equals(type);
        if (keyUp || keyDown) {
            int keyCode = event.getKeyCode();
            if (keyUp && keyCode == KeyCodes.KEY_ENTER) {
                setSelectionAndClose(optionList.getSelectedValue());
            } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
                cancelAndClose();
            } else if (keyCode == KeyCodes.KEY_UP) {
                optionList.moveUp();
            } else if (keyCode == KeyCodes.KEY_DOWN) {
                optionList.moveDown();
            } else {
                filterOptionList(updateViewData(lastParent, viewData, true));
            }
        }
    }

    private void createOption() {
        createOption(getInputElement(lastParent).getValue(),
                new Callback<String, String>() {
                    @Override
                    public void onFailure(String reason) {
                        cancelAndClose();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (!options.contains(result)) {
                            options.add(result);
                        }
                        setSelectionAndClose(result);
                    }
                });
    }

    private void editOptions() {
        cancelAndClose();
        editAllOptions();
    }

    private void cancelAndClose() {
        cancel();
        popup.hide();
    }

    private void cancel() {
        String oldValue = getViewData(lastContext.getKey()).getOriginal();
        setViewData(lastContext.getKey(), null);
        setValue(lastContext, lastParent, oldValue);
    }

    private void setSelectionAndClose(String selection) {
        InputElement input = getInputElement(lastParent);
        input.setValue(selection);
        commit(lastContext, lastParent, getViewData(lastContext.getKey()), valueUpdater);
        popup.hide();
    }

    private void commit(Context context, Element parent, ViewData viewData, ValueUpdater<String> valueUpdater) {
        String value = updateViewData(parent, viewData, false);
        setValue(context, parent, viewData.getOriginal());
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
    }

    private String updateViewData(Element parent, ViewData viewData, boolean isEditing) {
        InputElement input = getInputElement(parent);
        String value = input.getValue();
        viewData.setText(value);
        viewData.setEditing(isEditing);
        return value;
    }

    private void edit(Context context, Element parent, String value) {
        setValue(context, parent, value);
        InputElement input = getInputElement(parent);
        showPopup(parent);
        input.focus();
        filterOptionList("");
    }

    private void filterOptionList(String v) {
        optionList.filter(v);
    }

    private void showPopup(Element parent) {
        final InputElement input = getInputElement(parent);
        if (popup.isAttached()) {
            popup.hide();
        }
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                popup.setPopupPosition(input.getAbsoluteLeft(),
                        input.getAbsoluteBottom());
            }
        });
        popup.addAutoHidePartner(input);
    }

    private void popupClosed() {
        InputElement input = getInputElement(lastParent);
        popup.removeAutoHidePartner(input);
        clearInput(input);
        lastContext = null;
        lastParent = null;
    }

    /*
        private int getSelectedIndex(String value) {
            Integer index = indexForOption.get(value);
            if (index == null) {
                return -1;
            }
            return index;
        }
    */
    private InputElement getInputElement(Element parent) {
        return parent.getFirstChild().cast();
    }

    private native void clearInput(Element input) /*-{
        if (input.selectionEnd)
            input.selectionEnd = input.selectionStart;
        else if ($doc.selection)
            $doc.selection.clear();
    }-*/;

    protected abstract void editAllOptions();

    protected abstract void createOption(String optionName, Callback<String, String> callback);

}
