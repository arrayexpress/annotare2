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

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.om.enums.Role;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class AclTest {

    @Test
    public void testAclHasPermission() {
        Acl acl = new Acl();
        acl.getEntries().addAll(asList(
                new AclEntry(Role.AUTHENTICATED, Permission.VIEW),
                new AclEntry(Role.CURATOR, Permission.VIEW),
                new AclEntry(Role.CURATOR, Permission.UPDATE)));

        assertTrue(acl.hasPermission(asList(Role.AUTHENTICATED), Permission.VIEW));
        assertTrue(acl.hasPermission(asList(Role.CURATOR), Permission.UPDATE));
        assertFalse(acl.hasPermission(asList(Role.AUTHENTICATED), Permission.UPDATE));
    }
}
