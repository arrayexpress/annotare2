package uk.ac.ebi.fg.annotare2.web.server.servlets;

/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import uk.ac.ebi.fg.annotare2.web.server.servlets.utils.SessionAttribute;

public class SessionInformation {
    public static final SessionAttribute EMAIL_SESSION_ATTRIBUTE = new SessionAttribute("email");
    public static final SessionAttribute LOGGED_IN_SESSION_ATTRIBUTE = new SessionAttribute("loggedin");
    public static final SessionAttribute INFO_SESSION_ATTRIBUTE = new SessionAttribute("info");
}
