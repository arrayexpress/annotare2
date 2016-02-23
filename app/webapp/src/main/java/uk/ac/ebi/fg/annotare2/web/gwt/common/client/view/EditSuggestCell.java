/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

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
import com.google.gwt.user.client.ui.BasicSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.SuggestionDisplay;

import static com.google.gwt.dom.client.BrowserEvents.*;

public class EditSuggestCell extends
        AbstractEditableCell<String, EditSuggestCell.ViewData> {

    interface Template extends SafeHtmlTemplates {
        @Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\"></input>")
        SafeHtml input(String value);
    }

    /**
     * The view data object used by this cell. We need to store both the text and
     * the state because this cell is rendered differently in edit mode. If we did
     * not store the edit state, refreshing the cell with view data would always
     * put us in to edit state, rendering a text box instead of the new text
     * string.
     */
    static class ViewData {

        private boolean isEditing;

        /**
         * If true, this is not the first edit.
         */
        private boolean isEditingAgain;

        /**
         * Keep track of the original value at the start of the edit, which might be
         * the edited value from the previous edit and NOT the actual value.
         */
        private String original;

        private String text;

        /**
         * Construct a new ViewData in editing mode.
         *
         * @param text the text to edit
         */
        public ViewData(String text) {
            this.original = text;
            this.text = text;
            this.isEditing = true;
            this.isEditingAgain = false;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            ViewData vd = (ViewData) o;
            return equalsOrBothNull(original, vd.original)
                    && equalsOrBothNull(text, vd.text) && isEditing == vd.isEditing
                    && isEditingAgain == vd.isEditingAgain;
        }

        public String getOriginal() {
            return original;
        }

        public String getText() {
            return text;
        }

        @Override
        public int hashCode() {
            return original.hashCode() + text.hashCode()
                    + Boolean.valueOf(isEditing).hashCode() * 29
                    + Boolean.valueOf(isEditingAgain).hashCode();
        }

        public boolean isEditing() {
            return isEditing;
        }

        public boolean isEditingAgain() {
            return isEditingAgain;
        }

        public void setEditing(boolean isEditing) {
            boolean wasEditing = this.isEditing;
            this.isEditing = isEditing;

            // This is a subsequent edit, so start from where we left off.
            if (!wasEditing && isEditing) {
                isEditingAgain = true;
                original = text;
            }
        }

        public void setText(String text) {
            this.text = text;
        }

        private boolean equalsOrBothNull(Object o1, Object o2) {
            return (o1 == null) ? o2 == null : o1.equals(o2);
        }
    }

    class SuggestionCallback implements SuggestionDisplay.SuggestionCallback {

        private final Context context;
        private final Element parent;
        private final ValueUpdater<String> valueUpdater;

        public SuggestionCallback(Context context, Element parent, ValueUpdater<String> valueUpdater) {
            this.context = context;
            this.parent = parent;
            this.valueUpdater = valueUpdater;
        }

        public void onSuggestionSelected(Suggestion suggestion) {
            setNewSelection(context, parent, suggestion, valueUpdater, true);
        }
    }

    class OracleCallback implements Callback {

        private final Context context;
        private final Element parent;

        private final SuggestionCallback suggestionCallback;

        public OracleCallback(Context context, Element parent, ValueUpdater<String> valueUpdater) {
            this.context = context;
            this.parent = parent;
            this.suggestionCallback = new SuggestionCallback(context, parent, valueUpdater);
        }

        public void onSuggestionsReady(Request request, Response response) {
            if (isEditing(context, parent, null)) {
                display.setMoreSuggestions(response.hasMoreSuggestions(),
                        response.getMoreSuggestionsCount());
                display.showSuggestions(getInputElement(parent), response.getSuggestions(),
                        oracle.isDisplayStringHTML(), isAutoSelectEnabled(),
                        suggestionCallback);
            }
        }
    }

    private static Template template;

    private final SafeHtmlRenderer<String> renderer;

    private SuggestOracle oracle;
    private int limit = 20;
    private boolean selectsFirstItem = false;
    private boolean sctrictlySuggestions = false;
    private final SuggestionDisplay display;

    /**
     * Construct a new EditSuggestCell that will use a
     * {@link com.google.gwt.text.shared.SimpleSafeHtmlRenderer}.
     */
    public EditSuggestCell(SuggestOracle oracle) {
        this(SimpleSafeHtmlRenderer.getInstance(), oracle);
    }

    /**
     * Construct a new EditSuggestCell that will use a
     * {@link com.google.gwt.text.shared.SimpleSafeHtmlRenderer}.
     */
    public EditSuggestCell(SuggestOracle oracle, boolean sctrictlySuggestions) {
        this(SimpleSafeHtmlRenderer.getInstance(), oracle);
        this.sctrictlySuggestions = sctrictlySuggestions && null != display;
    }

    /**
     * Construct a new EditSuggestCell that will use a given {@link SafeHtmlRenderer}
     * to render the value when not in edit mode.
     *
     * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>}
     *          instance
     */
    public EditSuggestCell(SafeHtmlRenderer<String> renderer, SuggestOracle oracle) {
        super(CLICK, KEYUP, KEYDOWN, BLUR);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        if (renderer == null) {
            throw new IllegalArgumentException("renderer == null");
        }
        this.renderer = renderer;

        this.oracle = oracle;
        this.display = null != oracle ? new BasicSuggestionDisplay() : null;
    }

    public boolean isAutoSelectEnabled() {
        return selectsFirstItem;
    }

    public void setAutoSelectEnabled(boolean selectsFirstItem) {
        this.selectsFirstItem = selectsFirstItem;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean isEditing(Context context, Element parent, String value) {
        ViewData viewData = getViewData(context.getKey());
        return viewData != null && viewData.isEditing();
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
        /*
         * Do not use the renderer in edit mode because the value of a text
         * input element is always treated as text. SafeHtml isn't valid in the
         * context of the value attribute.
         */
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
      /*
       * Render a blank space to force the rendered element to have a height.
       * Otherwise it is not clickable.
       */
            sb.appendHtmlConstant("\u00A0");
        }
    }

    @Override
    public boolean resetFocus(Context context, Element parent, String value) {
        if (isEditing(context, parent, value)) {
            getInputElement(parent).focus();
            return true;
        }
        return false;
    }

    public boolean validateInput(String value, int rowIndex) {
        if (sctrictlySuggestions && null != display) {
            if (display.isValidSuggestion(value)) {
                return true;
            } else {
                NotificationPopupPanel.error("Value '" + value + "' is not permitted. Please select one of the suggestions provided.", true, false);
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * Convert the cell to edit mode.
     *
     * @param context the {@link Context} of the cell
     * @param parent the parent element
     * @param value the current value
     */
    protected void edit(Context context, Element parent, String value) {
        setValue(context, parent, value);
        InputElement input = getInputElement(parent);
        input.focus();
        input.select();
    }

    /**
     * Convert the cell to non-edit mode.
     *
     * @param context the context of the cell
     * @param parent the parent Element
     * @param viewData the {@link ViewData} object
     */
    private void cancel(Context context, Element parent, ViewData viewData) {

        String originalText = viewData.getOriginal();
        if (viewData.isEditingAgain()) {
            viewData.setText(originalText);
            viewData.setEditing(false);
        } else {
            setViewData(context.getKey(), null);
        }
        clearInput(getInputElement(parent));
        setValue(context, parent, originalText);
    }

    /**
     * Clear selected from the input element. Both Firefox and IE fire spurious
     * onblur events after the input is removed from the DOM if selection is not
     * cleared.
     *
     * @param input the input element
     */
    private native void clearInput(Element input) /*-{
        if (input.selectionEnd)
            input.selectionEnd = input.selectionStart;
        else if ($doc.selection)
            $doc.selection.clear();
    }-*/;

    /**
     * Commit the current value.
     *
     * @param context the context of the cell
     * @param parent the parent Element
     * @param viewData the {@link ViewData} object
     * @param valueUpdater the {@link ValueUpdater}
     */
    private void commit(Context context, Element parent, ViewData viewData,
                        ValueUpdater<String> valueUpdater) {
        String value = updateViewData(parent, viewData, false);
        clearInput(getInputElement(parent));
        setValue(context, parent, viewData.getOriginal());
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
    }

    private void editEvent(Context context, Element parent, String value,
                           ViewData viewData, NativeEvent event, ValueUpdater<String> valueUpdater) {
        String type = event.getType();
        int keyCode = event.getKeyCode();

        if (KEYDOWN.equals(type)) {
            NotificationPopupPanel.cancel();

            if (KeyCodes.KEY_TAB == keyCode) {
                event.stopPropagation();
                event.preventDefault();
            } else if (null != display && display.isSuggestionListShowing() && handleSuggestionKeyDown(context, parent, event, valueUpdater))
                return;

            if (KeyCodes.KEY_ESCAPE == keyCode) {
                // Cancel edit mode.
                cancel(context, parent, viewData);
            } else {
                updateViewData(parent, viewData, true);
            }
        } else if (KEYUP.equals(type)) {
            if (KeyCodes.KEY_TAB == keyCode) {
                if (null != display && display.isSuggestionListShowing()) {
                    Suggestion suggestion = display.getCurrentSelection();
                    if (null == suggestion) {
                        display.hideSuggestions();
                    } else {
                        setNewSelection(context, parent, suggestion, valueUpdater, false);
                    }
                }
                event.stopPropagation();
                event.preventDefault();
            } else if (KeyCodes.KEY_ENTER == keyCode) {
                if (null != display && display.isSuggestionListShowing() && null != display.getCurrentSelection()) {
                    setNewSelection(context, parent, display.getCurrentSelection(), valueUpdater, true);
                } else {
                    if (null != display && display.isSuggestionListShowing()) {
                        display.hideSuggestions();
                    }
                    // validate and commit
                    if (validateInput(viewData.getText(), context.getIndex())) {
                        commit(context, parent, viewData, valueUpdater);
                    }
                }
            } else {
                String oldText = viewData.getText();
                String curText = updateViewData(parent, viewData, true);
                if (null != display && !oldText.equals(curText)) {
                    showSuggestions(context, parent, curText, valueUpdater);
                }

            }
        } else if (BLUR.equals(type)) {
            if (null == display || !display.isSuggestionListShowing()) {
                // cancel the change
                EventTarget eventTarget = event.getEventTarget();
                if (Element.is(eventTarget)) {
                    Element target = Element.as(eventTarget);
                    if ("input".equals(target.getTagName().toLowerCase())) {
                        // Cancel edit mode.
                        cancel(context, parent, viewData);
                    }
                }
            }
        }
    }

    /**
     * Get the input element in edit mode.
     */
    private InputElement getInputElement(Element parent) {
        return parent.getFirstChild().cast();
    }

    /**
     * Update the view data based on the current value.
     *
     * @param parent the parent element
     * @param viewData the {@link ViewData} object to update
     * @param isEditing true if in edit mode
     * @return the new value
     */
    private String updateViewData(Element parent, ViewData viewData,
                                  boolean isEditing) {
        InputElement input = (InputElement) parent.getFirstChild();
        String value = input.getValue();
        viewData.setText(value);
        viewData.setEditing(isEditing);
        return value;
    }

    private boolean handleSuggestionKeyDown(Context context, Element parent, NativeEvent event, ValueUpdater<String> valueUpdater) {
        switch (event.getKeyCode()) {
            case KeyCodes.KEY_DOWN:
                display.moveSelectionDown();
                event.preventDefault();
                return true;
            case KeyCodes.KEY_UP:
                display.moveSelectionUp();
                event.preventDefault();
                return true;
            case KeyCodes.KEY_ESCAPE:
                display.hideSuggestions();
                event.preventDefault();
                return true;
        }
        return false;
    }

    private void showSuggestions(Context context, Element parent, String query, ValueUpdater<String> valueUpdater) {
        Callback callback = new OracleCallback(context, parent, valueUpdater);

        if (query.length() == 0) {
            oracle.requestDefaultSuggestions(new SuggestOracle.Request(null, limit), callback);
        } else {
            oracle.requestSuggestions(new SuggestOracle.Request(query, limit), callback);
        }
    }

    private void setNewSelection(Context context, Element parent, Suggestion curSuggestion, ValueUpdater<String> valueUpdater, boolean doCommit) {
        assert curSuggestion != null : "suggestion cannot be null";
        ViewData viewData = getViewData(context.getKey());
        if (null != viewData && viewData.isEditing()) {
            String value = curSuggestion.getReplacementString();
            getInputElement(parent).setValue(value);
            display.hideSuggestions();
            if (doCommit) {
                commit(context, parent, viewData, valueUpdater);
            }
        }
    }
}
