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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class UpdateResult implements IsSerializable {

    private List<ContactDto> createdContacts;
    private List<ContactDto> updatedContacts;
    private List<ContactDto> removedContacts;
    private List<PublicationDto> createdPublications;
    private List<PublicationDto> updatedPublications;
    private List<PublicationDto> removedPublications;

    private List<SampleColumn> updatedSampleColumns;
    private List<SampleRow> updatedSampleRows;
    private List<SampleRow> createdSampleRows;

    private DetailsDto updatedDetails;

    public UpdateResult() {
        createdContacts = new ArrayList<ContactDto>();
        updatedContacts = new ArrayList<ContactDto>();
        removedContacts = new ArrayList<ContactDto>();

        createdPublications = new ArrayList<PublicationDto>();
        updatedPublications = new ArrayList<PublicationDto>();
        removedPublications = new ArrayList<PublicationDto>();

        updatedSampleColumns = new ArrayList<SampleColumn>();
        updatedSampleRows = new ArrayList<SampleRow>();
        createdSampleRows = new ArrayList<SampleRow>();
    }

    public void created(ContactDto dto) {
        createdContacts.add(dto);
    }

    public void updated(ContactDto dto) {
        updatedContacts.add(dto);
    }

    public void removed(ContactDto dto) {
        removedContacts.add(dto);
    }

    public void created(PublicationDto dto) {
        createdPublications.add(dto);
    }

    public void updated(PublicationDto dto) {
        updatedPublications.add(dto);
    }

    public void removed(PublicationDto dto) {
        removedPublications.add(dto);
    }

    public void updated(DetailsDto details) {
        updatedDetails = details;
    }

    public void created(SampleRow row) {
        createdSampleRows.add(row);
    }

    public void updated(SampleRow row) {
        updatedSampleRows.add(row);
    }

    public void updatedAll(Collection<SampleColumn> columns) {
        updatedSampleColumns.addAll(columns);
    }

    public List<ContactDto> getCreatedContacts() {
        return new ArrayList<ContactDto>(createdContacts);
    }

    public List<ContactDto> getUpdatedContacts() {
        return new ArrayList<ContactDto>(updatedContacts);
    }

    public List<ContactDto> getRemovedContacts() {
        return new ArrayList<ContactDto>(removedContacts);
    }

    public DetailsDto getUpdatedDetails() {
        return updatedDetails;
    }

    public List<PublicationDto> getCreatedPublications() {
        return new ArrayList<PublicationDto>(createdPublications);
    }

    public List<PublicationDto> getUpdatedPublications() {
        return new ArrayList<PublicationDto>(updatedPublications);
    }

    public List<PublicationDto> getRemovedPublications() {
        return new ArrayList<PublicationDto>(removedPublications);
    }

    public List<SampleRow> getUpdatedSampleRows() {
        return new ArrayList<SampleRow>(updatedSampleRows);
    }

    public List<SampleColumn> getUpdatedSampleColumns() {
        return updatedSampleColumns;
    }

    public  List<SampleRow> getCreatedSampleRows() {
        return createdSampleRows;
    }
}
