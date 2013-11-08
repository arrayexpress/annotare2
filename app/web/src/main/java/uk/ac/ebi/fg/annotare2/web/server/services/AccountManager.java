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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.User;

import static uk.ac.ebi.fg.annotare2.web.server.services.utils.DigestUtil.md5Hex;

/**
 * @author Olga Melnichuk
 */
public class AccountManager {

    private UserDao userDao;

    @Inject
    public AccountManager(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isValid(final String email, final String password) {
        return userDao.getUserByEmailAndPassword(email, md5Hex(password)) != null;
    }

    public User getByEmail(final String email) {
        return userDao.getUserByEmail(email);
    }
}
