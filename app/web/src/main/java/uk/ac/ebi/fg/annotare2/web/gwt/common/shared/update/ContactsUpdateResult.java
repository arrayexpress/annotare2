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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ContactsUpdateResult implements IsSerializable {

    private List<ContactDto> created;
    private List<ContactDto> updated;
    private List<ContactDto> removed;

    public ContactsUpdateResult() {
        created = new ArrayList<ContactDto>();
        updated = new ArrayList<ContactDto>();
        removed = new ArrayList<ContactDto>();
    }

    public List<ContactDto> getCreatedContacts() {
        return created;
    }

    public List<ContactDto> getUpdatedContacts() {
        return updated;
    }

    public List<ContactDto> getRemovedContacts() {
        return removed;
    }

    public void create(ContactDto contact) {
        if (contact != null) {
            created.add(contact);
        }
    }

    public void update(ContactDto contact) {
        if (contact != null) {
            updated.add(contact);
        }
    }

    public void removeAll(List<ContactDto> contacts) {
        removed.addAll(contacts);
    }
}
