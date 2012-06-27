/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.MenuButton;

/**
 * @author Olga Melnichuk
 */
public class LeftMenuViewImpl extends Composite implements LeftMenuView {

    interface Binder extends UiBinder<VerticalPanel, LeftMenuViewImpl> {
    }

    private Presenter presenter;

    private Anchor selected;

    @UiField
    MenuButton createButton;

    @UiField
    Anchor allSubmissions;

    @UiField
    Anchor maSubmissions;

    @UiField
    Anchor htsSubmissions;

    @UiField
    Anchor adfSubmissions;

    public LeftMenuViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        createButton.addMenuButtonItem("Microarray Experiment Submission");
        createButton.addMenuButtonItem("HTS Experiment Submission");
        createButton.addMenuButtonItem("ADF Submission");
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("allSubmissions")
    public void onRecentClick(ClickEvent event) {
        selectItem(allSubmissions);
        presenter.onSubmissionFilterClick(Filter.ALL_SUBMISSIONS);
    }

    @UiHandler("maSubmissions")
    public void onMaSubmissionsClick(ClickEvent event) {
        selectItem(maSubmissions);
        presenter.onSubmissionFilterClick(Filter.MA_SUBMISSIONS);
    }

    public void setFilter(Filter filter) {
        switch (filter) {
            case ALL_SUBMISSIONS:
                selectItem(allSubmissions);
                break;

            case MA_SUBMISSIONS:
                selectItem(maSubmissions);
                break;
        }
    }

    private void selectItem(Anchor item) {
        final String styleName = "selectedItem";
        if (selected != null) {
            selected.removeStyleName(styleName);
            selected = null;
        }
        item.addStyleName(styleName);
        selected = item;
    }

}
