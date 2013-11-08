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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class PublicationView extends ItemView<PublicationDto.Editor> {

    interface Binder extends UiBinder<Widget, PublicationView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox title;

    @UiField
    TextBox authors;

    @UiField
    TextBox pubMedId;

    @UiField
    TextBox doi;

    @UiField
    ListBox statusBox;

    private final List<OntologyTerm> statusList = new ArrayList<OntologyTerm>();

    public PublicationView(PublicationDto publication, Collection<OntologyTerm> statuses) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        this.statusList.addAll(statuses);
        initStatusBox();

        addHeaderField(authors);
        addHeaderField(title);
        addHeaderField(pubMedId);
        addHeaderField(doi);

        addField(new EditableField<PublicationDto.Editor, String>(title) {
            @Override
            protected String getValue(PublicationDto.Editor p) {
                return p.getTitle();
            }

            @Override
            protected void setValue(PublicationDto.Editor p, String value) {
                p.setTitle(value);
            }
        });

        addField(new EditableField<PublicationDto.Editor, String>(authors) {
            @Override
            protected String getValue(PublicationDto.Editor p) {
                return p.getAuthors();
            }

            @Override
            protected void setValue(PublicationDto.Editor p, String value) {
                p.setAuthors(value);
            }
        });

        addField(new EditableField<PublicationDto.Editor, String>(pubMedId) {
            @Override
            protected String getValue(PublicationDto.Editor p) {
                return p.getPubMedId();
            }

            @Override
            protected void setValue(PublicationDto.Editor p, String value) {
                p.setPubMedId(value);
            }
        });

        addField(new EditableField<PublicationDto.Editor, String>(doi) {
            @Override
            protected String getValue(PublicationDto.Editor p) {
                return p.getDoi();
            }

            @Override
            protected void setValue(PublicationDto.Editor p, String value) {
                p.setDoi(value);
            }
        });

        addField(new EditableField<PublicationDto.Editor, Integer>(new ListBoxValueIndex(statusBox)) {
            @Override
            protected Integer getValue(PublicationDto.Editor p) {
                int index = 1;
                for (OntologyTerm term : statusList) {
                    if (term.equals(p.getStatus())) {
                        return index;
                    }
                    index++;
                }
                return 0;
            }

            @Override
            protected void setValue(PublicationDto.Editor p, Integer value) {
                if (value > 0) {
                    OntologyTerm term = statusList.get(value - 1);
                    p.setStatus(term == null ? null : term);
                } else {
                    p.setStatus(null);
                }
            }
        });

        setItem(publication.editor());
    }

    public PublicationDto getPublication() {
        return getItem().copy();
    }

    private void initStatusBox() {
        statusBox.addItem("unknown");
        for (OntologyTerm term : statusList) {
            statusBox.addItem(term.getLabel());
        }
    }
}
