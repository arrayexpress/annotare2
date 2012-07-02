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

package uk.ac.ebi.fg.annotare2.om;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public class AclEntry {

    private int id;

    private Role role;

    private Permission permission;

    public AclEntry(int id, Role role, Permission permission) {
        this.id  = id;
        this.role = role;
        this.permission = permission;
    }

    public boolean complies(Collection<? extends Role> roles, Permission permission) {
        if (!this.permission.equals(permission)) {
            return false;
        }
        for (Role r : roles) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
