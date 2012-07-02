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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class EffectiveAcl {

    private Acl acl;
    
    private User owner;

    public EffectiveAcl(Acl acl, User owner) {
        this.acl = acl;
        this.owner = owner;
    }
    
    public boolean hasPermission(User user, Permission permission) {
        return acl.hasPermission(getEffectiveRoles(user), permission);
    }
    
    private Collection<? extends Role> getEffectiveRoles(User user) {
        List<Role> roles = new ArrayList<Role>();
        roles.addAll(user.getRoles());
        if (user.equals(owner)) {
            roles.add(Role.OWNER);
        }
        return roles;
    }
}
