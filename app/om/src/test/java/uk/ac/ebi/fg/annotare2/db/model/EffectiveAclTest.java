/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.db.model;

import com.google.common.base.Optional;
import org.junit.Test;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.Role;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class EffectiveAclTest {

    private long id = 0;

    @Test
    public void testEffectiveAclHasPermission() {
        Acl acl = new Acl();
        acl.getEntries().addAll(
                asList(new AclEntry(Role.AUTHENTICATED, Permission.CREATE),
                        new AclEntry(Role.CREATOR, Permission.VIEW),
                        new AclEntry(Role.OWNER, Permission.VIEW),
                        new AclEntry(Role.OWNER, Permission.UPDATE)));

        User creator = createUser(asList(Role.AUTHENTICATED));
        User owner = createUser(asList(Role.AUTHENTICATED));
        User other = createUser(asList(Role.AUTHENTICATED));

        EffectiveAcl effectiveAcl = new EffectiveAcl(acl, Optional.of(creator), Optional.of(owner));

        assertTrue(effectiveAcl.hasPermission(creator, Permission.CREATE));
        assertTrue(effectiveAcl.hasPermission(creator, Permission.VIEW));
        assertFalse(effectiveAcl.hasPermission(creator, Permission.UPDATE));
        assertTrue(effectiveAcl.hasPermission(owner, Permission.VIEW));
        assertTrue(effectiveAcl.hasPermission(owner, Permission.UPDATE));
        assertTrue(effectiveAcl.hasPermission(other, Permission.CREATE));
        assertFalse(effectiveAcl.hasPermission(other, Permission.VIEW));
        assertFalse(effectiveAcl.hasPermission(other, Permission.UPDATE));
    }

    private User createUser(Collection<? extends Role> roles) {
        User user = new User("email", "password");
        user.setId(id++);
        for(Role role : roles) {
            user.getRoles().add(new UserRole(user, role));
        }
        return user;
    }
}
