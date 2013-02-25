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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ArrayDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.AdfTabToolBarView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.ArrayDesignTab;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class AdfTabToolBarActivity extends AbstractActivity implements AdfTabToolBarView.Presenter {

    private AdfTabToolBarView view;
    private AdfServiceAsync adfService;
    private ArrayDesignTab tab;

    @Inject
    public AdfTabToolBarActivity(AdfTabToolBarView view,
                                 AdfServiceAsync adfService) {
        this.view = view;
        this.adfService = adfService;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        view.hideImportButtons(this.tab == ArrayDesignTab.Header);
        containerWidget.setWidget(view.asWidget());
    }

    public AdfTabToolBarActivity withPlace(ArrayDesignPlace place) {
        this.tab = place.getSelectedTab();
        return this;
    }

    @Override
    public void importFile(final AsyncCallback<Void> callback) {
        AsyncCallback<Void> wrap = new AsyncCallbackWrapper<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(result);
            }
        }.wrap();

        switch(tab) {
            case Header:
               adfService.importHeaderData(getSubmissionId(), wrap);
               break;
            case Table:
                adfService.importBodyData(getSubmissionId(), wrap);
                break;
            default:
                Window.alert("Unknown Array Design Tab: " + tab);
        }
    }
}
