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

package uk.ac.ebi.fg.annotare2.db.om;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.db.om.User;
import uk.ac.ebi.fg.annotare2.db.om.UserRole;
import uk.ac.ebi.fg.annotare2.db.om.enums.Role;

import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class UserTest {

    @Test
    public void defaultInitializationTest() {
        User user = new User("email", "password");

        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());

        user.getRoles().add(new UserRole(user, Role.CURATOR));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void equalsAndHashCodeTest() {
        User user1 = new User("email", "password");
        user1.setId(1L);

        User user2 = new User("email", "password");
        user2.setId(1L);

        User user3 = new User("Email", "Password");
        user3.setId(1L);

        User user4 = new User("email", "password");
        user4.setId(2L);

        assertTrue(user1.equals(user2));
        assertTrue(user1.equals(user3));

        assertEquals(user1.hashCode(), user2.hashCode());
        assertEquals(user1.hashCode(), user3.hashCode());

        assertFalse(user1.equals(user4));
    }
}
