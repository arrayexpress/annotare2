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

package uk.ac.ebi.fg.annotare2.db.dao.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.om.User;

/**
 * @author Olga Melnichuk
 */
public class UserDaoDummy implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoDummy.class);

    public User getUserByEmailAndPassword(String email, String password) {
        log.debug("getUser(email={}, password=***)", email);

        User u = getUserByEmail(email);
        if (u == null || !(u.getPassword().equals(password))) {
            return null;
        }
        return u;
    }

    public User getUserByEmail(String email) {
        return DummyData.getUserByEmail(email);
    }

    @Override
    public User get(long id) throws RecordNotFoundException {
        return null; // not supported
    }

    @Override
    public void save(User user) {
        // ignore
    }

    @Override
    public User create(String email, String password) {
        return DummyData.createUser(email, password);
    }
}
