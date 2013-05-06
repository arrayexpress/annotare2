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

import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateCommandType.CREATE;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateCommandType.REMOVE;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateCommandType.UPDATE;

/**
 * @author Olga Melnichuk
 */
public class ContactsUpdateCommand implements IsSerializable {

    private UpdateCommandType type;
    private ContactDto contact;
    private List<Integer> contactIds;

    private ContactsUpdateCommand() {
    }

    private ContactsUpdateCommand(UpdateCommandType type) {
        this.type = type;
    }

    public UpdateCommandType getType() {
        return type;
    }

    public ContactDto getContact() {
        return contact;
    }

    public List<Integer> getContactIds() {
        return contactIds;
    }

    public static ContactsUpdateCommand updateContactCommand(ContactDto contact) {
        ContactsUpdateCommand command = new ContactsUpdateCommand(UPDATE);
        command.contact = contact;
        return command;
    }

    public static ContactsUpdateCommand createContactCommand(ContactDto contact) {
        ContactsUpdateCommand command = new ContactsUpdateCommand(CREATE);
        command.contact = contact;
        return command;
    }

    public static ContactsUpdateCommand removeContactsCommand(List<ContactDto> contacts) {
        ContactsUpdateCommand command = new ContactsUpdateCommand(REMOVE);
        command.contactIds = extractId(contacts);
        return command;
    }

    private static List<Integer> extractId(List<ContactDto> contacts) {
        List<Integer> ids = new ArrayList<Integer>();
        for (ContactDto contact : contacts) {
            ids.add(contact.getId());
        }
        return ids;
    }
}
