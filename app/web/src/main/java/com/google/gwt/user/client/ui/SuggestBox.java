
/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.SuggestionDisplay.SuggestionCallback;

/**
 * A {@link SuggestBox} is a text box or text area which displays a
 * pre-configured set of selections that match the user's input.
 *
 * Each {@link SuggestBox} is associated with a single {@link com.google.gwt.user.client.ui.SuggestOracle}.
 * The {@link com.google.gwt.user.client.ui.SuggestOracle} is used to provide a set of selections given a
 * specific query string.
 *
 * <p>
 * By default, the {@link SuggestBox} uses a {@link com.google.gwt.user.client.ui.MultiWordSuggestOracle} as
 * its oracle. Below we show how a {@link com.google.gwt.user.client.ui.MultiWordSuggestOracle} can be
 * configured:
 * </p>
 *
 * <pre>
 *   MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
 *   oracle.add("Cat");
 *   oracle.add("Dog");
 *   oracle.add("Horse");
 *   oracle.add("Canary");
 *
 *   SuggestBox box = new SuggestBox(oracle);
 * </pre>
 *
 * Using the example above, if the user types "C" into the text widget, the
 * oracle will configure the suggestions with the "Cat" and "Canary"
 * suggestions. Specifically, whenever the user types a key into the text
 * widget, the value is submitted to the <code>MultiWordSuggestOracle</code>.
 *
 * <p>
 * Note that there is no method to retrieve the "currently selected suggestion"
 * in a SuggestBox, because there are points in time where the currently
 * selected suggestion is not defined. For example, if the user types in some
 * text that does not match any of the SuggestBox's suggestions, then the
 * SuggestBox will not have a currently selected suggestion. It is more useful
 * to know when a suggestion has been chosen from the SuggestBox's list of
 * suggestions. A SuggestBox fires {@link com.google.gwt.user.client.ui.SuggestionEvent SuggestionEvents}
 * whenever a suggestion is chosen, and handlers for these events can be added
 * using the {@link #addSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)} method.
 * </p>
 *
 * <p>
 * <img class='gallery' src='doc-files/SuggestBox.png'/>
 * </p>
 *
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>.gwt-SuggestBox</dt>
 * <dd>the suggest box itself</dd>
 * </dl>
 *
 * @see com.google.gwt.user.client.ui.SuggestOracle
 * @see com.google.gwt.user.client.ui.MultiWordSuggestOracle
 * @see com.google.gwt.user.client.ui.ValueBoxBase
 */
public class SuggestBox extends Composite implements HasText, HasFocus,
        HasEnabled, SourcesClickEvents, SourcesChangeEvents,
        SourcesKeyboardEvents, FiresSuggestionEvents, HasAllKeyHandlers,
        HasValue<String>, HasSelectionHandlers<Suggestion>,
        IsEditor<LeafValueEditor<String>> {



    private static final String STYLENAME_DEFAULT = "gwt-SuggestBox";

    /**
     * Creates a {@link SuggestBox} widget that wraps an existing &lt;input
     * type='text'&gt; element.
     *
     * This element must already be attached to the document. If the element is
     * removed from the document, you must call
     * {@link RootPanel#detachNow(Widget)}.
     *
     * @param oracle the suggest box oracle to use
     * @param element the element to be wrapped
     */
    public static SuggestBox wrap(SuggestOracle oracle, Element element) {
        // Assert that the element is attached.
        assert Document.get().getBody().isOrHasChild(element);

        TextBox textBox = new TextBox(element);
        SuggestBox suggestBox = new SuggestBox(oracle, textBox);

        // Mark it attached and remember it for cleanup.
        suggestBox.onAttach();
        RootPanel.detachOnWindowClose(suggestBox);

        return suggestBox;
    }

    private int limit = 20;
    private boolean selectsFirstItem = true;
    private SuggestOracle oracle;
    private String currentText;
    private LeafValueEditor<String> editor;
    private final SuggestionDisplay display;
    private final ValueBoxBase<String> box;
    private final Callback callback = new Callback() {
        public void onSuggestionsReady(Request request, Response response) {
            // If disabled while request was in-flight, drop it
            if (!isEnabled()) {
                return;
            }
            display.setMoreSuggestions(response.hasMoreSuggestions(),
                    response.getMoreSuggestionsCount());
            display.showSuggestions(getElement().<InputElement> cast(), response.getSuggestions(),
                    oracle.isDisplayStringHTML(), isAutoSelectEnabled(),
                    suggestionCallback);
        }
    };
    private final SuggestionCallback suggestionCallback = new SuggestionCallback() {
        public void onSuggestionSelected(Suggestion suggestion) {
            box.setFocus(true);
            setNewSelection(suggestion);
        }
    };

    /**
     * Constructor for {@link SuggestBox}. Creates a
     * {@link MultiWordSuggestOracle} and {@link TextBox} to use with this
     * {@link SuggestBox}.
     */
    public SuggestBox() {
        this(new MultiWordSuggestOracle());
    }

    /**
     * Constructor for {@link SuggestBox}. Creates a {@link TextBox} to use with
     * this {@link SuggestBox}.
     *
     * @param oracle the oracle for this <code>SuggestBox</code>
     */
    public SuggestBox(SuggestOracle oracle) {
        this(oracle, new TextBox());
    }

    /**
     * Constructor for {@link SuggestBox}. The text box will be removed from it's
     * current location and wrapped by the {@link SuggestBox}.
     *
     * @param oracle supplies suggestions based upon the current contents of the
     *          text widget
     * @param box the text widget
     */
    public SuggestBox(SuggestOracle oracle, ValueBoxBase<String> box) {
        this(oracle, box, new BasicSuggestionDisplay());
    }

    /**
     * Constructor for {@link SuggestBox}. The text box will be removed from it's
     * current location and wrapped by the {@link SuggestBox}.
     *
     * @param oracle supplies suggestions based upon the current contents of the
     *          text widget
     * @param box the text widget
     * @param suggestDisplay the class used to display suggestions
     */
    public SuggestBox(SuggestOracle oracle, ValueBoxBase<String> box,
                      SuggestionDisplay suggestDisplay) {
        this.box = box;
        this.display = suggestDisplay;
        initWidget(box);

        addEventsToTextBox();

        setOracle(oracle);
        setStyleName(STYLENAME_DEFAULT);
    }

    /**
     *
     * Adds a listener to receive change events on the SuggestBox's text box. The
     * source Widget for these events will be the SuggestBox.
     *
     * @param listener the listener interface to add
     * @deprecated use {@link #getTextBox}().addChangeHandler instead
     */
    @Deprecated
    public void addChangeListener(final ChangeListener listener) {
        ListenerWrapper.WrappedLogicalChangeListener.add(box, listener).setSource(
                this);
    }

    /**
     * Adds a listener to receive click events on the SuggestBox's text box. The
     * source Widget for these events will be the SuggestBox.
     *
     * @param listener the listener interface to add
     * @deprecated use {@link #getTextBox}().addClickHandler instead
     */
    @Deprecated
    public void addClickListener(final ClickListener listener) {
        ListenerWrapper.WrappedClickListener legacy = ListenerWrapper.WrappedClickListener.add(
                box, listener);
        legacy.setSource(this);
    }

    /**
     * Adds an event to this handler.
     *
     * @deprecated use {@link #addSelectionHandler} instead.
     */
    @Deprecated
    public void addEventHandler(final SuggestionHandler handler) {
        ListenerWrapper.WrappedOldSuggestionHandler.add(this, handler);
    }

    /**
     * Adds a listener to receive focus events on the SuggestBox's text box. The
     * source Widget for these events will be the SuggestBox.
     *
     * @param listener the listener interface to add
     * @deprecated use {@link #getTextBox}().addFocusHandler/addBlurHandler()
     *             instead
     */
    @Deprecated
    public void addFocusListener(final FocusListener listener) {
        ListenerWrapper.WrappedFocusListener focus = ListenerWrapper.WrappedFocusListener.add(
                box, listener);
        focus.setSource(this);
    }

    /**
     * @deprecated Use {@link #addKeyDownHandler}, {@link #addKeyUpHandler} and
     *             {@link #addKeyPressHandler} instead
     */
    @Deprecated
    public void addKeyboardListener(KeyboardListener listener) {
        ListenerWrapper.WrappedKeyboardListener.add(this, listener);
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return addDomHandler(handler, KeyPressEvent.getType());
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

    public HandlerRegistration addSelectionHandler(
            SelectionHandler<Suggestion> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Returns a {@link TakesValueEditor} backed by the SuggestBox.
     */
    public LeafValueEditor<String> asEditor() {
        if (editor == null) {
            editor = TakesValueEditor.of(this);
        }
        return editor;
    }

    /**
     * Gets the limit for the number of suggestions that should be displayed for
     * this box. It is up to the current {@link SuggestOracle} to enforce this
     * limit.
     *
     * @return the limit for the number of suggestions
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Get the {@link SuggestionDisplay} used to display suggestions.
     *
     * @return the {@link SuggestionDisplay}
     */
    public SuggestionDisplay getSuggestionDisplay() {
        return display;
    }

    /**
     * Gets the suggest box's {@link com.google.gwt.user.client.ui.SuggestOracle}.
     *
     * @return the {@link SuggestOracle}
     */
    public SuggestOracle getSuggestOracle() {
        return oracle;
    }

    public int getTabIndex() {
        return box.getTabIndex();
    }

    public String getText() {
        return box.getText();
    }

    /**
     * Get the text box associated with this suggest box.
     *
     * @return this suggest box's text box
     * @throws ClassCastException if this suggest box's value box is not an
     *     instance of TextBoxBase
     * @deprecated in favour of getValueBox
     */
    @Deprecated
    public TextBoxBase getTextBox() {
        return (TextBoxBase) box;
    }

    public String getValue() {
        return box.getValue();
    }

    /**
     * Get the ValueBoxBase associated with this suggest box.
     *
     * @return this suggest box's value box
     */
    public ValueBoxBase<String> getValueBox() {
        return box;
    }

    /**
     * Returns whether or not the first suggestion will be automatically selected.
     * This behavior is on by default.
     *
     * @return true if the first suggestion will be automatically selected
     */
    public boolean isAutoSelectEnabled() {
        return selectsFirstItem;
    }

    /**
     * Gets whether this widget is enabled.
     *
     * @return <code>true</code> if the widget is enabled
     */
    public boolean isEnabled() {
        return box.isEnabled();
    }

    /**
     * Refreshes the current list of suggestions.
     */
    public void refreshSuggestionList() {
        if (isAttached()) {
            refreshSuggestions();
        }
    }

    /**
     * @deprecated Use the {@link HandlerRegistration#removeHandler} method on the
     *             object returned by {@link #getTextBox}().addChangeHandler
     *             instead
     */
    @Deprecated
    public void removeChangeListener(ChangeListener listener) {
        ListenerWrapper.WrappedChangeListener.remove(box, listener);
    }

    /**
     * @deprecated Use the {@link HandlerRegistration#removeHandler} method on the
     *             object returned by {@link #getTextBox}().addClickHandler
     *             instead
     */
    @Deprecated
    public void removeClickListener(ClickListener listener) {
        ListenerWrapper.WrappedClickListener.remove(box, listener);
    }

    /**
     * @deprecated Use the {@link HandlerRegistration#removeHandler} method no the
     *             object returned by {@link #addSelectionHandler} instead
     */
    @Deprecated
    public void removeEventHandler(SuggestionHandler handler) {
        ListenerWrapper.WrappedOldSuggestionHandler.remove(this, handler);
    }

    /**
     * @deprecated Use the {@link HandlerRegistration#removeHandler} method on the
     *             object returned by {@link #getTextBox}().addFocusListener
     *             instead
     */
    @Deprecated
    public void removeFocusListener(FocusListener listener) {
        ListenerWrapper.WrappedFocusListener.remove(this, listener);
    }

    /**
     * @deprecated Use the {@link HandlerRegistration#removeHandler} method on the
     *             object returned by {@link #getTextBox}().add*Handler instead
     */
    @Deprecated
    public void removeKeyboardListener(KeyboardListener listener) {
        ListenerWrapper.WrappedKeyboardListener.remove(this, listener);
    }

    public void setAccessKey(char key) {
        box.setAccessKey(key);
    }

    /**
     * Turns on or off the behavior that automatically selects the first suggested
     * item. This behavior is on by default.
     *
     * @param selectsFirstItem Whether or not to automatically select the first
     *          suggestion
     */
    public void setAutoSelectEnabled(boolean selectsFirstItem) {
        this.selectsFirstItem = selectsFirstItem;
    }

    /**
     * Sets whether this widget is enabled.
     *
     * @param enabled <code>true</code> to enable the widget, <code>false</code>
     *          to disable it
     */
    public void setEnabled(boolean enabled) {
        box.setEnabled(enabled);
        if (!enabled) {
            display.hideSuggestions();
        }
    }

    public void setFocus(boolean focused) {
        box.setFocus(focused);
    }

    /**
     * Sets the limit to the number of suggestions the oracle should provide. It
     * is up to the oracle to enforce this limit.
     *
     * @param limit the limit to the number of suggestions provided
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setTabIndex(int index) {
        box.setTabIndex(index);
    }

    public void setText(String text) {
        box.setText(text);
    }

    public void setValue(String newValue) {
        box.setValue(newValue);
    }

    public void setValue(String value, boolean fireEvents) {
        box.setValue(value, fireEvents);
    }

    /**
     * Show the current list of suggestions.
     */
    public void showSuggestionList() {
        if (isAttached()) {
            currentText = null;
            refreshSuggestions();
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        display.onEnsureDebugId(baseID);
    }

    void showSuggestions(String query) {
        if (query.length() == 0) {
            oracle.requestDefaultSuggestions(new Request(null, limit), callback);
        } else {
            oracle.requestSuggestions(new Request(query, limit), callback);
        }
    }

    private void addEventsToTextBox() {
        class TextBoxEvents implements KeyDownHandler, KeyUpHandler, ValueChangeHandler<String> {

            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_DOWN:
                        if (display.isSuggestionListShowing()) {
                            display.moveSelectionDown();
                            event.preventDefault();
                        }
                        break;
                    case KeyCodes.KEY_UP:
                        if (display.isSuggestionListShowing()) {
                            display.moveSelectionUp();
                            event.preventDefault();
                        }
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        if (display.isSuggestionListShowing()) {
                            display.hideSuggestions();
                            event.preventDefault();
                        }
                        break;
                    case KeyCodes.KEY_ENTER:
                    case KeyCodes.KEY_TAB:
                        Suggestion suggestion = display.getCurrentSelection();
                        if (null == suggestion) {
                            display.hideSuggestions();
                        } else {
                            setNewSelection(suggestion);
                        }
                        break;
                }
            }

            public void onKeyUp(KeyUpEvent event) {
                // After every user key input, refresh the popup's suggestions.
                refreshSuggestions();
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                delegateEvent(SuggestBox.this, event);
            }
        }

        TextBoxEvents events = new TextBoxEvents();
        box.addKeyDownHandler(events);
        box.addKeyUpHandler(events);
        box.addValueChangeHandler(events);
    }

    private void fireSuggestionEvent(Suggestion selectedSuggestion) {
        SelectionEvent.fire(this, selectedSuggestion);
    }

    private void refreshSuggestions() {
        // Get the raw text.
        String text = getText();
        if (text.equals(currentText)) {
            return;
        } else {
            currentText = text;
        }
        showSuggestions(text);
    }

    /**
     * Set the new suggestion in the text box.
     *
     * @param curSuggestion the new suggestion
     */
    private void setNewSelection(Suggestion curSuggestion) {
        assert curSuggestion != null : "suggestion cannot be null";
        currentText = curSuggestion.getReplacementString();
        setText(currentText);
        display.hideSuggestions();
        fireSuggestionEvent(curSuggestion);
    }

    /**
     * Sets the suggestion oracle used to create suggestions.
     *
     * @param oracle the oracle
     */
    private void setOracle(SuggestOracle oracle) {
        this.oracle = oracle;
    }
}