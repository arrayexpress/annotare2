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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class UpdateResult implements IsSerializable {

    private List<ContactDto> createdContacts;
    private List<ContactDto> updatedContacts;
    private List<ContactDto> removedContacts;
    private DetailsDto updatedDetails;

    public UpdateResult() {
        createdContacts = new ArrayList<ContactDto>();
        updatedContacts = new ArrayList<ContactDto>();
        removedContacts = new ArrayList<ContactDto>();
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

    public void updated(DetailsDto details) {
        updatedDetails = details;
    }
}
