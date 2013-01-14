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
import uk.ac.ebi.fg.annotare2.magetab.idf.TermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.TermSourceTemplatesDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.TermSourceView;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class IdfTermSourceListViewImpl extends IdfListView<TermSource> implements IdfTermSourceListView {


    public IdfTermSourceListViewImpl() {
        addIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showTermSourceTemplates();
            }
        });
    }

    @Override
    public void setTermSources(ArrayList<TermSource> termSources) {
        for (TermSource ts : termSources) {
            addTermSourceView(ts);
        }
    }

    private DisclosureListItem addTermSourceView(TermSource ts) {
        return addListItem(new TermSourceView(ts));
    }

    private void showTermSourceTemplates() {
        //TODO load templates properly
        ArrayList<UITermSource> templates = new ArrayList<UITermSource>();
        templates.add(new UITermSource("ArrayExpress", "", "", "AE description"));
        templates.add(new UITermSource("EFO", "", "", "EFO description"));
        templates.add(new UITermSource("MGED Ontology", "", "", " MGED Ontology description"));

        final TermSourceTemplatesDialog dialog = new TermSourceTemplatesDialog(templates);
        dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
               if (!dialog.isCancelled()) {
                   addTermSources(dialog.getSelection());
               }
            }
        });
    }

    private void addTermSources(ArrayList<UITermSource> selected) {
        //TODO
    }

}
