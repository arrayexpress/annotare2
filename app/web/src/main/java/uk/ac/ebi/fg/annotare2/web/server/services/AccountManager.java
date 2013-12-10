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
import uk.ac.ebi.fg.annotare2.db.dao.UserRoleDao;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.UserRole;
import uk.ac.ebi.fg.annotare2.db.model.enums.Role;

import java.math.BigInteger;
import java.security.SecureRandom;

import static uk.ac.ebi.fg.annotare2.web.server.services.utils.DigestUtil.md5Hex;
import static com.google.common.base.Strings.nullToEmpty;

/**
 * @author Olga Melnichuk
 */
public class AccountManager {

    private UserDao userDao;
    private UserRoleDao userRoleDao;
    private SecureRandom random;

    @Inject
    public AccountManager(UserDao userDao, UserRoleDao userRoleDao) {
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
        this.random = new SecureRandom();
    }

    public boolean doesExist(final String email) {
        return null != userDao.getUserByEmail(email);
    }

    public boolean isValid(final String email, final String password) {
        return null != userDao.getUserByEmailAndPassword(email, md5Hex(password));
    }

    public boolean isEmailVerified(final String email) {
        User user = getByEmail(email);

        return null != user && user.isEmailVerified();
    }

    public boolean isPasswordChangeRequested(final String email) {
        User user = getByEmail(email);

        return null != user && user.isPasswordChangeRequested();
    }

    public boolean isVerificationTokenValid(final String email, final String token) {
        User user = getByEmail(email);

        return null != user && nullToEmpty(user.getVerificationToken()).equals(token);
    }


    public User getByEmail(final String email) {
        return userDao.getUserByEmail(email);
    }

    public User createUser(final String name, final String email, final String password) {
        User user = new User(name, email, md5Hex(password));
        user.setEmailVerified(false);
        user.setVerificationToken(generateToken());
        userDao.save(user);

        UserRole userRole = new UserRole(user, Role.AUTHENTICATED);
        userRoleDao.save(userRole);
        return user;
    }

    public User setEmailVerified(final String email) {
        User user = getByEmail(email);
        if (null != user) {
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            userDao.save(user);
        }
        return user;
    }

    public User requestChangePassword(final String email) {
        User user = getByEmail(email);
        if (null != user) {
            user.setPasswordChangeRequested(true);
            user.setVerificationToken(generateToken());
            userDao.save(user);
        }
        return user;
    }

    public User processChangePassword(final String email, final String password) {
        User user = getByEmail(email);
        if (null != user) {
            user.setPasswordChangeRequested(false);
            user.setPassword(md5Hex(password));
            user.setVerificationToken(null);
            userDao.save(user);
        }
        return user;
    }

    private String generateToken() {
        return new BigInteger(130, random).toString(36).toLowerCase();
    }
}
