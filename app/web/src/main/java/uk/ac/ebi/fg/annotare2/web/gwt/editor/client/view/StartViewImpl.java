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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.dialog.SetupExpSubmissionDialog;

/**
 * @author Olga Melnichuk
 */
public class StartViewImpl extends Composite implements StartView {

    interface Binder extends UiBinder<Widget, StartViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private Presenter presenter;

    @UiField
    Anchor setupSubmission;

    public StartViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        setupSubmission.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setupSubmission();
            }
        });
    }

    private void setupSubmission() {
        (new SetupExpSubmissionDialog(false)).setPresenter(presenter);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void start() {
        setupSubmission();
    }
}
