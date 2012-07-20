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

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class Person {

    private String firstName;
    private String lastName;
    private String midInitials;
    private String email;
    private String phone;
    private String fax;
    private String address;
    private String affiliation;
    private TermList roles;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMidInitials() {
        return midInitials;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }

    public String getAddress() {
        return address;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public TermList getRoles() {
        return roles;
    }

    public static class Builder {
        private final Person person = new Person();

        public Builder setFirstName(String name) {
            person.firstName = name;
            return this;
        }

        public Builder setLastName(String name) {
            person.lastName = name;
            return this;
        }

        public Builder setMidInitials(String str) {
            person.midInitials = str;
            return this;
        }

        public Builder setAddress(String str) {
            person.address = str;
            return this;
        }

        public Builder setAffiliation(String str) {
            person.affiliation = str;
            return this;
        }

        public Builder setEmail(String str) {
            person.email = str;
            return this;
        }

        public Builder setPhone(String str) {
            person.phone = str;
            return this;
        }

        public Builder setFax(String str) {
            person.fax = str;
            return this;
        }

        public Builder setRoles(TermList roles) {
            person.roles = roles;
            return this;
        }

        public Person build() {
            return person;
        }
    }
}
