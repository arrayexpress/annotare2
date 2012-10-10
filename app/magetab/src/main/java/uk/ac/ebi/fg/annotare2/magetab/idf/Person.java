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

package uk.ac.ebi.fg.annotare2.magetab.idf;

import com.google.common.annotations.GwtCompatible;
import uk.ac.ebi.fg.annotare2.magetab.base.Row;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class Person {

    private Row.Cell<String> firstName;

    private Row.Cell<String> lastName;

    private Row.Cell<String> midInitials;

    private Row.Cell<String> email;

    private Row.Cell<String> phone;

    private Row.Cell<String> fax;

    private Row.Cell<String> affiliation;

    private Row.Cell<String> address;

    private Term roles;

    public Row.Cell<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Row.Cell<String> firstName) {
        this.firstName = firstName;
    }

    public Row.Cell<String> getLastName() {
        return lastName;
    }

    public void setLastName(Row.Cell<String> lastName) {
        this.lastName = lastName;
    }

    public Row.Cell<String> getMidInitials() {
        return midInitials;
    }

    public void setMidInitials(Row.Cell<String> midInitials) {
        this.midInitials = midInitials;
    }

    public Row.Cell<String> getEmail() {
        return email;
    }

    public void setEmail(Row.Cell<String> email) {
        this.email = email;
    }

    public Row.Cell<String> getPhone() {
        return phone;
    }

    public void setPhone(Row.Cell<String> phone) {
        this.phone = phone;
    }

    public Row.Cell<String> getFax() {
        return fax;
    }

    public void setFax(Row.Cell<String> fax) {
        this.fax = fax;
    }

    public Row.Cell<String> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(Row.Cell<String> affiliation) {
        this.affiliation = affiliation;
    }

    public Row.Cell<String> getAddress() {
        return address;
    }

    public void setAddress(Row.Cell<String> address) {
        this.address = address;
    }

    public Term getRoles() {
        return roles;
    }

    public void setRoles(Term roles) {
        this.roles = roles;
    }
}
