/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.magetab.parser.table.om;

import uk.ac.ebi.fg.annotare2.magetab.parser.table.IdfCell;

/**
 * @author Olga Melnichuk
 */
public class IdfPerson {

    private IdfCell firstName;

    private IdfCell lastName;

    private IdfCell midInitials;

    private IdfCell email;

    private IdfTerm roles;

    public IdfCell getFirstName() {
        return firstName;
    }

    public void setFirstName(IdfCell firstName) {
        this.firstName = firstName;
    }

    public IdfCell getLastName() {
        return lastName;
    }

    public void setLastName(IdfCell lastName) {
        this.lastName = lastName;
    }

    public IdfCell getMidInitials() {
        return midInitials;
    }

    public void setMidInitials(IdfCell midInitials) {
        this.midInitials = midInitials;
    }

    public IdfCell getEmail() {
        return email;
    }

    public void setEmail(IdfCell email) {
        this.email = email;
    }

    public IdfTerm getRoles() {
        return roles;
    }

    public void setRoles(IdfTerm roles) {
        this.roles = roles;
    }
}
