/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.LeftMenuItem;

import java.util.HashMap;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType.EXPERIMENT;

/**
 * @author Olga Melnichuk
 */
public class LeftMenuViewImpl extends Composite implements LeftMenuView {

    interface Binder extends UiBinder<HTMLPanel, LeftMenuViewImpl> {
    }

    private Presenter presenter;

    private Widget selected;

    @UiField
    Button createButton;

    @UiField
    Button importButton;

    @UiField
    LeftMenuItem allSubmissions;

    @UiField
    LeftMenuItem completed;

    @UiField
    LeftMenuItem incomplete;

    private HashMap<SubmissionListFilter, Widget> filters = new HashMap<SubmissionListFilter, Widget>();

    public LeftMenuViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
        /*
        createButton
                .addMenuButtonItem(EXPERIMENT.getTitle() + " Submission")
                .addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        presenter.onSubmissionCreateClick(EXPERIMENT);
                    }
                });
        */
        /*** disabled in first release ***
        createButton
                .addMenuButtonItem(ARRAY_DESIGN.getTitle() + " Submission")
                .addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        presenter.onSubmissionCreateClick(ARRAY_DESIGN);
                    }
                });
         ***/

        filters.put(SubmissionListFilter.COMPLETED_SUBMISSIONS, completed);
        filters.put(SubmissionListFilter.INCOMPLETE_SUBMISSIONS, incomplete);
        filters.put(SubmissionListFilter.ALL_SUBMISSIONS, allSubmissions);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("createButton")
    public void onCreateButtonClick(ClickEvent event) {
        presenter.onSubmissionCreateClick(EXPERIMENT);
    }

    @UiHandler("importButton")
    public void onImportButtonClick(ClickEvent event) {
        NotificationPopupPanel.message("This functionality is not available at the moment.", true);
    }

    @UiHandler("allSubmissions")
    public void onRecentClick(ClickEvent event) {
        filterClick(SubmissionListFilter.ALL_SUBMISSIONS);
    }

    @UiHandler("completed")
    public void onCompletedSubmissionsClick(ClickEvent event) {
        filterClick(SubmissionListFilter.COMPLETED_SUBMISSIONS);
    }

    @UiHandler("incomplete")
    public void onIncompleteSubmissionsClick(ClickEvent event) {
        filterClick(SubmissionListFilter.INCOMPLETE_SUBMISSIONS);
    }

    private void filterClick(SubmissionListFilter filter) {
        selectItem(filters.get(filter));
        presenter.onSubmissionFilterClick(filter);
    }

    public void setFilter(SubmissionListFilter filter) {
        selectItem(filters.get(filter));
    }

    private void selectItem(Widget item) {
        final String styleName = "selectedItem";
        if (selected != null) {
            selected.removeStyleName(styleName);
            selected = null;
        }
        item.addStyleName(styleName);
        selected = item;
    }

}
