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

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemChangeEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemChangeEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.PublicationView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class PublicationListViewImpl extends ListView<PublicationDto.Editor> implements PublicationListView {

    private Presenter presenter;

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPublications(List<PublicationDto> publications) {
        for (PublicationDto p : publications) {
            addPublicationView(p);
        }
    }

    @Override
    public List<PublicationDto> getPublications() {
        List<PublicationDto> publications = new ArrayList<PublicationDto>();
        for (DisclosureListItem item : getItems()) {
            PublicationView view = (PublicationView) item.getContent();
            publications.add(view.getPublication());
        }
        return publications;
    }

    private DisclosureListItem addPublicationView(PublicationDto p) {
        final PublicationView view = new PublicationView(p);
        view.addItemChangeEventHandler(new ItemChangeEventHandler() {
            @Override
            public void onItemChange(ItemChangeEvent event) {
                presenter.updatePublication(view.getPublication());
            }
        });
        return addListItem(view);
    }
}
