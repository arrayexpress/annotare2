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

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import java.util.Collection;

public abstract class SuggestionDisplay {

    public static interface SuggestionCallback {
        void onSuggestionSelected(Suggestion suggestion);
    }

    /**
     * Get the currently selected {@link com.google.gwt.user.client.ui.SuggestOracle.Suggestion} in the display.
     *
     * @return the current suggestion, or null if none selected
     */
    public abstract Suggestion getCurrentSelection();

    /**
     * Hide the list of suggestions from view.
     */
    public abstract void hideSuggestions();

    /**
     * Check if suggestion list is showing
     */
    public abstract boolean isSuggestionListShowing();

    /**
     * Highlight the suggestion directly below the current selection in the
     * list.
     */
    public abstract void moveSelectionDown();

    /**
     * Highlight the suggestion directly above the current selection in the
     * list.
     */
    public abstract void moveSelectionUp();

    /**
     * Checks if supplied string is a valid suggestion.
     */
    public abstract boolean isValidSuggestion(String suggestionValue);

    /**
     * Set the debug id of widgets used in the SuggestionDisplay.
     *
     * @param suggestBoxBaseID the baseID of the {@link com.google.gwt.user.client.ui.SuggestBox}
     * @see com.google.gwt.user.client.ui.UIObject#onEnsureDebugId(String)
     */
    public void onEnsureDebugId(String suggestBoxBaseID) {
    }

    /**
     * Accepts information about whether there were more suggestions matching
     * than were provided to {@link #showSuggestions}.
     *
     * @param hasMoreSuggestions true if more matches were available
     * @param numMoreSuggestions number of more matches available. If the
     *     specific number is unknown, 0 will be passed.
     */
    public void setMoreSuggestions(boolean hasMoreSuggestions,
                                   int numMoreSuggestions) {
        // Subclasses may optionally implement.
    }

    /**
     * Update the list of visible suggestions.
     *
     * Use care when using isDisplayStringHtml; it is an easy way to expose
     * script-based security problems.
     *
     * @param suggestBox the suggest box where the suggestions originated
     * @param suggestions the suggestions to show
     * @param isDisplayStringHTML should the suggestions be displayed as HTML
     * @param isAutoSelectEnabled if true, the first item should be selected
     *          automatically
     * @param callback the callback used when the user makes a suggestion
     */
    public abstract void showSuggestions(InputElement suggestBox,
                                         Collection<? extends Suggestion> suggestions,
                                         boolean isDisplayStringHTML, boolean isAutoSelectEnabled,
                                         SuggestionCallback callback);

}
