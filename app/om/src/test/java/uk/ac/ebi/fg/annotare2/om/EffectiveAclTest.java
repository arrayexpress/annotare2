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

import com.google.common.base.Optional;
import org.junit.Test;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.om.enums.Role;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class EffectiveAclTest {

    private int id = 0;

    @Test
    public void testEffectiveAclHasPermission() {
        Acl acl = new Acl();
        acl.add(new AclEntry(Role.AUTHENTICATED, Permission.VIEW));
        acl.add(new AclEntry(Role.OWNER, Permission.VIEW));
        acl.add(new AclEntry(Role.OWNER, Permission.UPDATE));

        User owner = createUser(asList(Role.AUTHENTICATED));
        User other = createUser(asList(Role.AUTHENTICATED));

        EffectiveAcl effectiveAcl = new EffectiveAcl(acl, Optional.of(owner));

        assertTrue(effectiveAcl.hasPermission(owner, Permission.VIEW));
        assertTrue(effectiveAcl.hasPermission(owner, Permission.UPDATE));
        assertFalse(effectiveAcl.hasPermission(other, Permission.UPDATE));
    }

    private User createUser(Collection<? extends Role> roles) {
        User user = new User(id++, "email", "password");
        user.setRoles(roles);
        return user;
    }
}
