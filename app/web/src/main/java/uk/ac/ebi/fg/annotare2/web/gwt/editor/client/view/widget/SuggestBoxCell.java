/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

import java.util.Collection;
import java.util.List;

import static com.google.gwt.dom.client.BrowserEvents.*;

/**
 * @author Olga Melnichuk
 */
public class SuggestBoxCell extends AbstractEditableCell<String, SuggestBoxCell.ViewData> {

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

    public static class SuggestionDisplay
            implements HasSelectionHandlers<SuggestOracle.Suggestion>, HasCloseHandlers<PopupPanel> {

        private final PopupPanel popup;
        private final SuggestionList suggestionList;
        private final SuggestOracle oracle;

        private SuggestOracle.Suggestion current;

        private final HandlerManager handlerManager = new HandlerManager(this);

        public SuggestionDisplay(SuggestOracle oracle) {
            this.oracle = oracle;

            suggestionList = new SuggestionList();

            popup = new PopupPanel(true, false);
            popup.setPreviewingAllNativeEvents(true);
            popup.add(suggestionList);
       }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            handlerManager.fireEvent(event);
        }

        @Override
        public HandlerRegistration addSelectionHandler(SelectionHandler<SuggestOracle.Suggestion> handler) {
            return handlerManager.addHandler(SelectionEvent.getType(), handler);
        }

        @Override
        public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler) {
            return popup.addCloseHandler(handler);
        }

        public void suggest(String query) {
            oracle.requestSuggestions(new SuggestOracle.Request(query, 20), new SuggestOracle.Callback() {
                @Override
                public void onSuggestionsReady(SuggestOracle.Request request, SuggestOracle.Response response) {
                    renderSuggestions(response.getSuggestions());
                }
            });
        }

        private void renderSuggestions(Collection<? extends SuggestOracle.Suggestion> suggestions) {
            suggestionList.clearItems();
            for (final SuggestOracle.Suggestion suggestion : suggestions) {
                suggestionList.addItem(new SuggestionItem(suggestion, true, new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        onSelect(suggestion);
                    }
                }));
            }
        }

        private void onSelect(SuggestOracle.Suggestion suggestion) {
            current = suggestion;
            SelectionEvent.fire(this, suggestion);
        }

        public void attach(final InputElement input) {
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

        public void detach(InputElement input) {
            popup.removeAutoHidePartner(input);
        }

        public SuggestOracle.Suggestion getCurrentSuggestion() {
            return current;
        }

        public void moveSelectionUp() {
            if (suggestionList.getSelectedItemIndex() == -1) {
                suggestionList.selectItem(suggestionList.getNumItems() - 1);
            } else {
                suggestionList.selectItem(suggestionList.getSelectedItemIndex() - 1);
            }
        }

        public void moveSelectionDown() {
            suggestionList.selectItem(suggestionList.getSelectedItemIndex() + 1);
        }
    }

    private static class SuggestionList extends MenuBar {
        private SuggestionList() {
            super(true);
            setFocusOnHoverEnabled(false);
        }

        protected int getSelectedItemIndex() {
            return getSelectedItemIndex();
        }

        protected void selectItem(int index) {
            List<MenuItem> items = getItems();
            if (index > -1 && index < items.size()) {
                //TODO itemOver(items.get(index), false);
            }
        }

        protected int getNumItems() {
            return getNumItems();
        }
    }

    private static class SuggestionItem extends MenuItem {
        private SuggestOracle.Suggestion suggestion;

        public SuggestionItem(SuggestOracle.Suggestion suggestion, boolean asHTML, Scheduler.ScheduledCommand command) {
            super(suggestion.getDisplayString(), asHTML, command);
            DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
            this.suggestion = suggestion;
        }

        public SuggestOracle.Suggestion getSuggestion() {
            return suggestion;
        }
    }

    private static Template template;

    private final SafeHtmlRenderer<String> renderer;

    private SuggestionDisplay suggestionDisplay;

    private Element lastParent;
    private Context lastContext;
    private ValueUpdater<String> valueUpdater;

    public SuggestBoxCell(SuggestOracle oracle) {
        this(SimpleSafeHtmlRenderer.getInstance(), oracle);
    }

    public SuggestBoxCell(SafeHtmlRenderer<String> renderer, SuggestOracle oracle) {
        super(CLICK, KEYUP, KEYDOWN, BLUR);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        if (renderer == null) {
            throw new IllegalArgumentException("renderer == null");
        }
        this.renderer = renderer;
        suggestionDisplay = new SuggestionDisplay(oracle);
        suggestionDisplay.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                SuggestOracle.Suggestion suggestion = event.getSelectedItem();
                setSelectionAndClose(suggestion);
            }
        });
        suggestionDisplay.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if (event.isAutoClosed()) {
                    cancel();
                }
                popupClosed();
            }
        });
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
                setSelectionAndClose(suggestionDisplay.getCurrentSuggestion());
            } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
                cancelAndClose();
            } else if (keyCode == KeyCodes.KEY_UP) {
                suggestionDisplay.moveSelectionUp();
            } else if (keyCode == KeyCodes.KEY_DOWN) {
                suggestionDisplay.moveSelectionDown();
            } else {
                suggest(updateViewData(lastParent, viewData, true));
            }
        }
    }

    private void cancelAndClose() {
        cancel();
        hide();
    }

    private void hide() {
        suggestionDisplay.popup.hide();
    }

    private void cancel() {
        String oldValue = getViewData(lastContext.getKey()).getOriginal();
        setViewData(lastContext.getKey(), null);
        setValue(lastContext, lastParent, oldValue);
    }

    private void setSelectionAndClose(SuggestOracle.Suggestion suggestion) {
        InputElement input = getInputElement(lastParent);
        input.setValue(suggestion.getReplacementString());
        commit(lastContext, lastParent, getViewData(lastContext.getKey()), valueUpdater);
        hide();
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
        suggest("");
    }

    private void suggest(String query) {
        suggestionDisplay.suggest(query);
    }

    private void showPopup(Element parent) {
        suggestionDisplay.attach(getInputElement(parent));
    }

    private void popupClosed() {
        InputElement input = getInputElement(lastParent);
        suggestionDisplay.detach(input);
        clearInput(input);
        lastContext = null;
        lastParent = null;
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
