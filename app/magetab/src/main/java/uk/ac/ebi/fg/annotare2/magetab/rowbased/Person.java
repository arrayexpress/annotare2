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

package uk.ac.ebi.fg.annotare2.magetab.rowbased;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.table.Cell;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Person {

    private Cell<String> firstName;

    private Cell<String> lastName;

    private Cell<String> midInitials;

    private Cell<String> email;

    private Cell<String> phone;

    private Cell<String> fax;

    private Cell<String> affiliation;

    private Cell<String> address;

    private Term roles;

    public Cell<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Cell<String> firstName) {
        this.firstName = firstName;
    }

    public Cell<String> getLastName() {
        return lastName;
    }

    public void setLastName(Cell<String> lastName) {
        this.lastName = lastName;
    }

    public Cell<String> getMidInitials() {
        return midInitials;
    }

    public void setMidInitials(Cell<String> midInitials) {
        this.midInitials = midInitials;
    }

    public Cell<String> getEmail() {
        return email;
    }

    public void setEmail(Cell<String> email) {
        this.email = email;
    }

    public Cell<String> getPhone() {
        return phone;
    }

    public void setPhone(Cell<String> phone) {
        this.phone = phone;
    }

    public Cell<String> getFax() {
        return fax;
    }

    public void setFax(Cell<String> fax) {
        this.fax = fax;
    }

    public Cell<String> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(Cell<String> affiliation) {
        this.affiliation = affiliation;
    }

    public Cell<String> getAddress() {
        return address;
    }

    public void setAddress(Cell<String> address) {
        this.address = address;
    }

    public Term getRoles() {
        return roles;
    }

    public void setRoles(Term roles) {
        this.roles = roles;
    }
}
