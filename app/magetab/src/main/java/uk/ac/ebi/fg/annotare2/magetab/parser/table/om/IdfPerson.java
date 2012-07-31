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

import uk.ac.ebi.fg.annotare2.magetab.parser.table.TableCell;

/**
 * @author Olga Melnichuk
 */
public class IdfPerson {

    private TableCell firstName;

    private TableCell lastName;

    private TableCell midInitials;

    private TableCell email;

    private IdfTerm roles;

    public TableCell getFirstName() {
        return firstName;
    }

    public void setFirstName(TableCell firstName) {
        this.firstName = firstName;
    }

    public TableCell getLastName() {
        return lastName;
    }

    public void setLastName(TableCell lastName) {
        this.lastName = lastName;
    }

    public TableCell getMidInitials() {
        return midInitials;
    }

    public void setMidInitials(TableCell midInitials) {
        this.midInitials = midInitials;
    }

    public TableCell getEmail() {
        return email;
    }

    public void setEmail(TableCell email) {
        this.email = email;
    }

    public IdfTerm getRoles() {
        return roles;
    }

    public void setRoles(IdfTerm roles) {
        this.roles = roles;
    }
}
