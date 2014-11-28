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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.LeftMenuItem;

import java.util.HashMap;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType.EXPERIMENT;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType.IMPORTED_EXPERIMENT;
import static uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.Utils.getEditorUrl;
import static uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.Utils.getPlaceholderUrl;

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

    final ImportSubmissionDialog importDialog;

    private HashMap<SubmissionListFilter, Widget> filters = new HashMap<SubmissionListFilter, Widget>();

    public LeftMenuViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
        importDialog = new ImportSubmissionDialog();

        filters.put(SubmissionListFilter.COMPLETED_SUBMISSIONS, completed);
        filters.put(SubmissionListFilter.INCOMPLETE_SUBMISSIONS, incomplete);
        filters.put(SubmissionListFilter.ALL_SUBMISSIONS, allSubmissions);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("createButton")
    public void onCreateButtonClick(ClickEvent event) {
        final NewWindow window = NewWindow.open(getPlaceholderUrl(), "_blank", null);
        presenter.onSubmissionCreateClick(EXPERIMENT, new ReportingAsyncCallback<Long>(FailureMessage.UNABLE_TO_CREATE_SUBMISSION) {
            @Override
            public void onFailure(Throwable x) {
                super.onFailure(x);
                window.close();
            }

            @Override
            public void onSuccess(final Long result) {
                window.setUrl(getEditorUrl(result));
            }
        });
    }

    @UiHandler("importButton")
    public void onImportButtonClick(ClickEvent event) {
        presenter.onSubmissionImportClick(IMPORTED_EXPERIMENT, new ReportingAsyncCallback<Long>(FailureMessage.UNABLE_TO_CREATE_SUBMISSION) {
            @Override
            public void onSuccess(Long result) {

            }
        });
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
