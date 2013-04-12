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
import uk.ac.ebi.fg.annotare2.om.enums.Role;

import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class UserTest {

    @Test
    public void defaultInitializationTest() {
        User user = new User(1, "email", "password");

        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());

        try {
            user.getRoles().add(Role.CURATOR);
            fail("Collection of user roles should be unmodifiable");
        } catch (Exception e) {
            // ok
        }
    }

    @Test
    public void equalsAndHashCodeTest() {
        User user1 = new User(1, "email", "password");
        User user2 = new User(1, "email", "password");
        User user3 = new User(1, "Email", "Password");
        User user4 = new User(2, "email", "password");

        assertTrue(user1.equals(user2));
        assertTrue(user1.equals(user3));

        assertEquals(user1.hashCode(), user2.hashCode());
        assertEquals(user1.hashCode(), user3.hashCode());

        assertFalse(user1.equals(user4));
    }
}
