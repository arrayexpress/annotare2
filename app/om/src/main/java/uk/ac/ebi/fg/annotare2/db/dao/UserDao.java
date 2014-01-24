/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.db.dao;

import uk.ac.ebi.fg.annotare2.db.model.User;

/**
 * @author Olga Melnichuk
 */
public interface UserDao {

    User getUserByEmailAndPassword(String email, String password);

    User getUserByEmail(String email);

    User getCuratorUser();

    User get(long id) throws RecordNotFoundException;

    void save(User user);
}
