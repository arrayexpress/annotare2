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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ChangeableValues.hasChangeableValue;

/**
 * @author Olga Melnichuk
 */
public class PublicationView extends ItemView<PublicationDto.Editor> {

    @UiField
    TextBox title;

    @UiField
    TextBox authors;

    @UiField
    TextBox pubMedId;

    interface Binder extends UiBinder<Widget, PublicationView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public PublicationView(PublicationDto publication) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        addHeaderField(hasChangeableValue(title));
        addHeaderField(hasChangeableValue(authors));
        addHeaderField(hasChangeableValue(pubMedId));

        addField(new EditableField<PublicationDto.Editor, String>(hasChangeableValue(title)) {
            @Override
            protected String getValue(PublicationDto.Editor p) {
                return p.getTitle();
            }

            @Override
            protected void setValue(PublicationDto.Editor p, String value) {
                p.setTitle(value);
            }
        });

        addField(new EditableField<PublicationDto.Editor, String>(hasChangeableValue(authors)) {
            @Override
            protected String getValue(PublicationDto.Editor p) {
                return p.getAuthors();
            }

            @Override
            protected void setValue(PublicationDto.Editor p, String value) {
                p.setAuthors(value);
            }
        });

        addField(new EditableField<PublicationDto.Editor, String>(hasChangeableValue(pubMedId)) {
            @Override
            protected String getValue(PublicationDto.Editor p) {
                return p.getPubMedId();
            }

            @Override
            protected void setValue(PublicationDto.Editor p, String value) {
                p.setPubMedId(value);
            }
        });

        setItem(publication.editor());
    }
}
