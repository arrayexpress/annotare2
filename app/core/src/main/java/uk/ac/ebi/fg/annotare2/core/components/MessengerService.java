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

import java.util.Map;

public interface MessengerService {

    void instuctProcessMessages();

    void directEmail(String from, String to, String subject, String body) throws Exception;

    void ticketUpdate(Map<String, String> params, String ticketNumber) throws Exception;

    boolean checkRtServerStatus() throws Exception;
}
