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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.om.enums.Role;

import javax.annotation.Nullable;
import java.util.Collection;

import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.Lists.transform;

/**
 * @author Olga Melnichuk
 */
public class EffectiveAcl {

    private Acl acl;

    private Optional<User> owner;

    public EffectiveAcl(Acl acl, Optional<User> owner) {
        this.acl = acl;
        this.owner = owner;
    }

    public boolean hasPermission(User user, Permission permission) {
        return acl.hasPermission(getEffectiveRoles(user), permission);
    }

    private Collection<? extends Role> getEffectiveRoles(User user) {
        ImmutableList.Builder<Role> roles = builder();
        roles.addAll(transform(user.getRoles(), new Function<UserRole, Role>() {
            @Nullable
            @Override
            public Role apply(@Nullable UserRole input) {
                return input.getRole();
            }
        }));
        if (owner.isPresent() && user.equals(owner.get())) {
            roles.add(Role.OWNER);
        }
        return roles.build();
    }
}
