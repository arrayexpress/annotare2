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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import uk.ac.ebi.fg.annotare2.magetab.idf.ExperimentalDesign;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ExpDesignTemplatesDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ExperimentalDesignView;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class IdfExperimentalDesignListViewImpl extends IdfListView<ExperimentalDesign>
        implements IdfExperimentalDesignListView {

    private Presenter presenter;

    public IdfExperimentalDesignListViewImpl() {
        addIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showExperimentDesignTemplates();
            }
        });
    }

    @Override
    public void setExperimentalDesigns(ArrayList<ExperimentalDesign> designs) {
        for (ExperimentalDesign d : designs) {
            addExperimentalDesignView(d);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private DisclosureListItem addExperimentalDesignView(ExperimentalDesign d) {
        ExperimentalDesignView view = new ExperimentalDesignView();
        view.setItem(d);
        return addListItem(view);
    }

    private void showExperimentDesignTemplates() {
        final ExpDesignTemplatesDialog dialog = new ExpDesignTemplatesDialog(
                presenter.getExperimentalDesignTerms());
        dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                if (!dialog.isCancelled()) {
                    addExperimentDesigns(dialog.getSelection());
                }
            }
        });
    }

    private void addExperimentDesigns(ArrayList<UITerm> designs) {
        //TODO
    }
}
