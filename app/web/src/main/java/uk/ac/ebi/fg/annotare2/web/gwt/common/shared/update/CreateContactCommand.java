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

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ContactDto;

/**
 * @author Olga Melnichuk
 */
public class CreateContactCommand implements UpdateCommand {

    private  ContactDto contact;

    CreateContactCommand() {
        /*used by GWT serialization only*/
    }

    public CreateContactCommand(ContactDto contact) {
        this.contact = contact;
    }

    @Override
    public void execute(UpdatePerformer performer) {
        performer.createContact(contact);
    }

    @Override
    public boolean isCritical() {
        return false;
    }
}
