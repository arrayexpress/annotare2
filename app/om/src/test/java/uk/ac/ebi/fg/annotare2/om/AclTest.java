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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class AclTest {

    @Test
    public void testAclHasPermission() {
        Acl acl = new Acl(1, AclType.SUBMISSION);
        acl.add(new AclEntry(1, Role.AUTHENTICATED, Permission.VIEW));
        acl.add(new AclEntry(2, Role.CURATOR, Permission.VIEW));
        acl.add(new AclEntry(3, Role.CURATOR, Permission.UPDATE));

        assertTrue(acl.hasPermission(asList(Role.AUTHENTICATED), Permission.VIEW));
        assertTrue(acl.hasPermission(asList(Role.CURATOR), Permission.UPDATE));
        assertFalse(acl.hasPermission(asList(Role.AUTHENTICATED), Permission.UPDATE));
    }
}
