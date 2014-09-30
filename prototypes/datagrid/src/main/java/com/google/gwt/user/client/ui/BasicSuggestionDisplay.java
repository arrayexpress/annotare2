/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import java.util.Collection;

/**
 * <p>
 * Basic implementation of {@link SuggestionDisplay} displays
 * suggestions in a {@link com.google.gwt.user.client.ui.PopupPanel} beneath the {@link com.google.gwt.user.client.ui.SuggestBox}.
 * </p>
 *
 * <h3>CSS Style Rules</h3>
 * <dl>
 * <dt>.gwt-SuggestionPopup</dt>
 * <dd>the suggestion popup</dd>
 * <dt>.gwt-SuggestionPopup .item</dt>
 * <dd>an unselected suggestion</dd>
 * <dt>.gwt-SuggestionPopup .item-selected</dt>
 * <dd>a selected suggestion</dd>
 */

public class BasicSuggestionDisplay extends SuggestionDisplay
        implements HasAnimation {

    private final SuggestionMenu suggestionMenu;
    private final PopupPanel suggestionPopup;

    /**
     * We need to keep track of the last {@link SuggestBox} because it acts as
     * an autoHide partner for the {@link PopupPanel}. If we use the same
     * display for multiple {@link SuggestBox}, we need to switch the autoHide
     * partner.
     */
    private InputElement lastInputElement = null;

    /**
     * Sub-classes making use of {@link BasicSuggestionDisplay#decorateSuggestionList(Widget)} to add
     * elements to the suggestion popup _may_ want those elements to show even
     * when there are 0 suggestions. An example would be showing a "No
     * matches" message.
     */
    private boolean hideWhenEmpty = true;

    /**
     * Object to position the suggestion display next to, instead of the
     * associated suggest box.
     */
    private UIObject positionRelativeTo;

    /**
     * Construct a new {@link BasicSuggestionDisplay}.
     */
    public BasicSuggestionDisplay() {
        suggestionMenu = new SuggestionMenu();
        suggestionPopup = createPopup();
        suggestionPopup.setWidget(decorateSuggestionList(suggestionMenu));
    }

    @Override
    public void hideSuggestions() {
        suggestionPopup.hide();
    }

    public boolean isAnimationEnabled() {
        return suggestionPopup.isAnimationEnabled();
    }

    /**
     * Check whether or not the suggestion list is hidden when there are no
     * suggestions to display.
     *
     * @return true if hidden when empty, false if not
     */
    public boolean isSuggestionListHiddenWhenEmpty() {
        return hideWhenEmpty;
    }

    /**
     * Check whether or not the list of suggestions is being shown.
     *
     * @return true if the suggestions are visible, false if not
     */
    public boolean isSuggestionListShowing() {
        return suggestionPopup.isShowing();
    }

    public void setAnimationEnabled(boolean enable) {
        suggestionPopup.setAnimationEnabled(enable);
    }

    /**
     * Sets the style name of the suggestion popup.
     *
     * @param style the new primary style name
     * @see UIObject#setStyleName(String)
     */
    public void setPopupStyleName(String style) {
        suggestionPopup.setStyleName(style);
    }

    /**
     * Sets the UI object where the suggestion display should appear next to.
     *
     * @param uiObject the uiObject used for positioning, or null to position
     *     relative to the suggest box
     */
    public void setPositionRelativeTo(UIObject uiObject) {
        positionRelativeTo = uiObject;
    }

    /**
     * Set whether or not the suggestion list should be hidden when there are
     * no suggestions to display. Defaults to true.
     *
     * @param hideWhenEmpty true to hide when empty, false not to
     */
    public void setSuggestionListHiddenWhenEmpty(boolean hideWhenEmpty) {
        this.hideWhenEmpty = hideWhenEmpty;
    }

    /**
     * Create the PopupPanel that will hold the list of suggestions.
     *
     * @return the popup panel
     */
    protected PopupPanel createPopup() {
        PopupPanel p = new PopupPanel(true, false);
        p.setStyleName("gwt-SuggestionPopup");
        PopupPanel.setStyleName(p.getContainerElement(), "");
        p.setPreviewingAllNativeEvents(true);
        p.setAnimationType(AnimationType.ROLL_DOWN);
        return p;
    }

    /**
     * Wrap the list of suggestions before adding it to the popup. You can
     * override this method if you want to wrap the suggestion list in a
     * decorator.
     *
     * @param suggestionList the widget that contains the list of suggestions
     * @return the suggestList, optionally inside of a wrapper
     */
    public Widget decorateSuggestionList(Widget suggestionList) {
        return suggestionList;
    }

    @Override
    public Suggestion getCurrentSelection() {
        if (!isSuggestionListShowing()) {
            return null;
        }
        SuggestionMenuItem item = suggestionMenu.getSelectedItem();
        return null == item ? null : item.getSuggestion();
    }

    /**
     * Get the {@link PopupPanel} used to display suggestions.
     *
     * @return the popup panel
     */
    public PopupPanel getPopupPanel() {
        return suggestionPopup;
    }

    @Override
    public void moveSelectionDown() {
        // Make sure that the menu is actually showing. These keystrokes
        // are only relevant when choosing a suggestion.
        if (isSuggestionListShowing()) {
            // If nothing is selected, getSelectedItemIndex will return -1 and we
            // will select index 0 (the first item) by default.
            suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() + 1);
        }
    }

    @Override
    public void moveSelectionUp() {
        // Make sure that the menu is actually showing. These keystrokes
        // are only relevant when choosing a suggestion.
        if (isSuggestionListShowing()) {
            // if nothing is selected, then we should select the last suggestion by
            // default. This is because, in some cases, the suggestions menu will
            // appear above the text box rather than below it (for example, if the
            // text box is at the bottom of the window and the suggestions will not
            // fit below the text box). In this case, users would expect to be able
            // to use the up arrow to navigate to the suggestions.
            if (suggestionMenu.getSelectedItemIndex() == -1) {
                suggestionMenu.selectItem(suggestionMenu.getNumItems() - 1);
            } else {
                suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() - 1);
            }
        }
    }

    /**
     * <b>Affected Elements:</b>
     * <ul>
     * <li>-popup = The popup that appears with suggestions.</li>
     * <li>-item# = The suggested item at the specified index.</li>
     * </ul>
     *
     * @see UIObject#onEnsureDebugId(String)
     */
    @Override
    public void onEnsureDebugId(String baseID) {
        suggestionPopup.ensureDebugId(baseID + "-popup");
        suggestionMenu.setMenuItemDebugIds(baseID);
    }

    @Override
    public void showSuggestions(final InputElement inputElement,
                                   final Collection<? extends Suggestion> suggestions,
                                   boolean isDisplayStringHTML, boolean isAutoSelectEnabled,
                                   final SuggestionCallback callback) {
        // Hide the popup if there are no suggestions to display.
        boolean anySuggestions = (suggestions != null && suggestions.size() > 0);
        if (!anySuggestions && hideWhenEmpty) {
            hideSuggestions();
            return;
        }

        // Hide the popup before we manipulate the menu within it. If we do not
        // do this, some browsers will redraw the popup as items are removed
        // and added to the menu.
        if (suggestionPopup.isAttached()) {
            suggestionPopup.hide();
        }

        suggestionMenu.clearItems();

        for (final Suggestion curSuggestion : suggestions) {
            final SuggestionMenuItem menuItem = new SuggestionMenuItem(
                    curSuggestion, isDisplayStringHTML);
            //menuItem.setScheduledCommand(new ScheduledCommand() {
            //    public void execute() {
            //        callback.onSuggestionSelected(curSuggestion);
            //    }
            //});

            suggestionMenu.addItem(menuItem);
        }

        if (isAutoSelectEnabled && anySuggestions) {
            // Select the first item in the suggestion menu.
            suggestionMenu.selectItem(0);
        }

        // Link the popup autoHide to the TextBox.
        if (lastInputElement != inputElement) {
            // If the suggest box has changed, free the old one first.
            if (lastInputElement != null) {
                suggestionPopup.removeAutoHidePartner(lastInputElement);
            }
            lastInputElement = inputElement;
            suggestionPopup.addAutoHidePartner(inputElement);
        }

        // Show the popup under the input element
        suggestionPopup.setPopupPositionAndShow(new PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                suggestionPopup.setPopupPosition(inputElement.getAbsoluteLeft(),
                        inputElement.getAbsoluteBottom());
                suggestionPopup.setWidth(
                        (inputElement.getAbsoluteRight() - inputElement.getAbsoluteLeft())
                                + Style.Unit.PX.getType());
            }
        });
    }

    private static class SuggestionMenu extends ComplexPanel {

        private int selectedItem = -1;

        public SuggestionMenu() {
            setElement(Document.get().createULElement());
            //setFocusOnHoverEnabled(false);
        }

        public void clearItems() {
            super.clear();
            selectedItem = -1;
        }

        public void addItem(SuggestionMenuItem item) {
            super.add(item, (com.google.gwt.dom.client.Element)getElement());
        }

        public int getNumItems() {
            return getChildren().size();
        }

        public int getSelectedItemIndex() {
            return selectedItem;
        }

        public SuggestionMenuItem getSelectedItem() {
            if (0 <= selectedItem && getChildren().size() < selectedItem) {
                return (SuggestionMenuItem)getChildren().get(selectedItem);
            }
            return null;
        }

        public void selectItem(int index) {
            if (index >= 0 && index < getChildren().size()) {
                if (-1 != selectedItem) {
                    getChildren().get(selectedItem).removeStyleName("selected");
                }
                getChildren().get(index).setStyleName("selected");
                selectedItem = index;
            }
        }

        void setMenuItemDebugIds(String baseID) {
            int itemCount = 0;
            for (Widget item : getChildren()) {
                item.ensureDebugId(baseID + "-item" + itemCount);
                itemCount++;
            }
        }

        public void setId(String id) {
            // Set an attribute common to all tags
            getElement().setId(id);
        }

        public void setDir(String dir) {
            // Set an attribute specific to this tag
            ((UListElement) getElement().cast()).setDir(dir);
        }
    }

    private static class SuggestionMenuItem extends SimplePanel {

        private Suggestion suggestion;

        private SuggestionMenuItem() {
            super((Element) Document.get().createLIElement().cast());
            getElement().getStyle().setProperty("whiteSpace", "nowrap");
        }

        private SuggestionMenuItem(String s, boolean asHTML) {
            this();
            if (asHTML) {
                getElement().setInnerHTML(s);
            } else {
                getElement().setInnerText(s);
            }
        }

        public SuggestionMenuItem(Suggestion suggestion, boolean asHTML) {
            this(suggestion.getDisplayString(), asHTML);
            setSuggestion(suggestion);
        }

        public Suggestion getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(Suggestion suggestion) {
            this.suggestion = suggestion;
        }
    }

}
