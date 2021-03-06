/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.PublicationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class PublicationListViewImpl extends ListView<PublicationDto.Editor> implements PublicationListView {

    private Presenter presenter;

    public PublicationListViewImpl() {
        removeIcon.setTitle("Remove selected publications");
        addIcon.setTitle("Add a new publication");
        addIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addNewPublication();
            }
        });
        removeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeSelectedPublications();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPublications(final List<PublicationDto> publications, Collection<OntologyTerm> publicationStatuses) {
        clear();
        for (PublicationDto p : publications) {
            addPublicationView(p, publicationStatuses);
        }
    }

    @Override
    public ArrayList<PublicationDto> getPublications() {
        ArrayList<PublicationDto> publications = new ArrayList<>();
        for (DisclosureListItem item : getItems()) {
            PublicationView view = (PublicationView) item.getContent();
            publications.add(view.getPublication());
        }
        return publications;
    }

    private DisclosureListItem addPublicationView(PublicationDto p, Collection<OntologyTerm> statuses) {
        final PublicationView view = new PublicationView(p, statuses);
        view.addContentChangeEventHandler(new ContentChangeEventHandler() {
            @Override
            public void onContentChange(ContentChangeEvent event) {
                presenter.updatePublication(view.getPublication());
            }
        });
        return addListItem(view);
    }

    private void addNewPublication() {
        presenter.createPublication();
    }

    private void removeSelectedPublications() {
        List<Integer> selected = getSelected();
        if (selected.isEmpty()) {
            return;
        }

        ArrayList<PublicationDto> publications = new ArrayList<>();
        for (Integer index : selected) {
            DisclosureListItem item = getItem(index);
            PublicationView view = (PublicationView) item.getContent();
            publications.add(view.getPublication());
        }
        presenter.removePublications(publications);
        removeItems(selected);
    }
}
