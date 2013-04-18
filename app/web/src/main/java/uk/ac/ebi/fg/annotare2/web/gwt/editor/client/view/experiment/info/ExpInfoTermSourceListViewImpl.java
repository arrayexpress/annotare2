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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.TermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.TermSourceTemplatesDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.TermSourceView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ExpInfoTermSourceListViewImpl extends ListView<TermSource> implements IdfTermSourceListView {

    private Presenter presenter;

    public ExpInfoTermSourceListViewImpl() {
        addIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showTermSourceTemplates();
            }
        });

        removeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedTermSources();
            }
        });
    }

    @Override
    public void setTermSources(List<TermSource> termSources) {
        for (TermSource ts : termSources) {
            addTermSourceView(ts);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private DisclosureListItem addTermSourceView(TermSource ts) {
        return addListItem(new TermSourceView(ts));
    }

    private void showTermSourceTemplates() {
        presenter.getTermSourceTemplates(new AsyncCallback<List<UITermSource>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Server error: can't load term source templates");
            }

            @Override
            public void onSuccess(List<UITermSource> result) {
                if (result.isEmpty()) {
                    addTermSources(new ArrayList<UITermSource>());
                    return;
                }

                final TermSourceTemplatesDialog dialog = new TermSourceTemplatesDialog(result);
                dialog.addSelectionHandler(new SelectionHandler<List<UITermSource>>() {
                    @Override
                    public void onSelection(SelectionEvent<List<UITermSource>> event) {
                        addTermSources(event.getSelectedItem());                    }
                });
            }
        });
    }

    private void addTermSources(List<UITermSource> templates) {
        if (templates.isEmpty()) {
            addTermSourceView(presenter.createTermSource());
            return;
        }

        for (UITermSource t : templates) {
            TermSource ts = presenter.createTermSource();
            ts.getName().setValue(t.getName());
            ts.getVersion().setValue(t.getVersion());
            ts.getFile().setValue(t.getUrl());
            addTermSourceView(ts);
        }
    }

    private void removeSelectedTermSources() {
        List<Integer> selected = getSelected();
        if (selected.isEmpty()) {
            return;
        }
        presenter.removeTermSources(selected);
        removeItems(selected);
    }

}
