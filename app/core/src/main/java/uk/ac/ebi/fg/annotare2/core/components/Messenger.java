/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.core.components;

import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;

import java.util.Map;

public interface Messenger {

    void send(String template, Map<String, String> parameters);

    void send(String template, Map<String, String> parameters, User user);

    void send(String template, Map<String, String> parameters, User user, Submission submission);

    void send(String note, Throwable x);

    void send(String note, Throwable x, User user);

    void updateTicket(Map<String, String> params) throws Exception;
}
