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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import uk.ac.ebi.fg.annotare2.magetab.idf.Term;
import uk.ac.ebi.fg.annotare2.magetab.idf.TermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.DynamicList;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ExpDesignTemplatesDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ExperimentalDesignView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class IdfExperimentalDesignListViewImpl extends IdfListView<Term>
        implements IdfExperimentalDesignListView {

    private Presenter presenter;

    private final DynamicList<TermSource> termSources;

    public IdfExperimentalDesignListViewImpl() {
        addIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showExperimentDesignTemplates();
            }
        });

        removeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedExperimentalDesigns();
            }
        });

        termSources = new DynamicList<TermSource>() {
            @Override
            public List<TermSource> loadValues() {
                return presenter.getTermSources();
            }
        };
    }

    @Override
    public void setExperimentalDesigns(List<Term> designs) {
        for (Term d : designs) {
            addExperimentalDesignView(d);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private DisclosureListItem addExperimentalDesignView(Term d) {
        return addListItem(new ExperimentalDesignView(d, termSources));
    }

    private void showExperimentDesignTemplates() {
        presenter.getExperimentalDesignTemplatesAsync(new AsyncCallback<ArrayList<UITerm>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error: can't load experimental design templates");
            }

            @Override
            public void onSuccess(ArrayList<UITerm> result) {
                final ExpDesignTemplatesDialog dialog = new ExpDesignTemplatesDialog(result);
                dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                        if (!dialog.isCancelled()) {
                            addExperimentDesigns(dialog.getSelection());
                        }
                    }
                });
            }
        });
    }

    private void addExperimentDesigns(List<UITerm> terms) {
        if (terms.isEmpty()) {
            addExperimentalDesignView(presenter.createExperimentalDesign());
            return;
        }

        for (UITerm term : terms) {
            Term design = presenter.createExperimentalDesign();
            TermSource termSource = presenter.getOrCreateTermSource(term.getTermSource());
            design.getName().setValue(term.getName());
            design.getAccession().setValue(term.getAccession());
            design.setTermSource(termSource);
            addExperimentalDesignView(design);
        }

        termSources.fireUpdate();
    }

    private void removeSelectedExperimentalDesigns() {
        List<Integer> selected = getSelected();
        if (selected.isEmpty()) {
            return;
        }
        presenter.removeExperimentalDesigns(selected);
        removeItems(selected);
    }

}
