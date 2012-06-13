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

package uk.ac.ebi.fg.annotare2.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.om.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class UserDaoDummy implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoDummy.class);

    private final Map<String, User> map = new HashMap<String, User>() {{
        put("user@ebi.ac.uk", new User("user@ebi.ac.uk", "ee11cbb19052e40b07aac0ca060c23ee"));
    }};

    public User getUser(String email, String password) throws RecordNotFoundException {
        log.debug("getUser(email={}, password=***)", email);

        User u = map.get(email);
        if (u == null || !(u.getPassword().equals(password))) {
            throw new RecordNotFoundException("User not found: password and email " + email);
        }
        return u;
    }

}
