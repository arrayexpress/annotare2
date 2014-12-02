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

package uk.ac.ebi.fg.annotare2.web.server.services.validation;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ValidatedDataFileMap {

    private final Map<Long, Map<String, ValidatedDataFile>> mapImpl;

    public ValidatedDataFileMap() {
        mapImpl = Collections.synchronizedMap(new HashMap<Long, Map<String, ValidatedDataFile>>());
    }

    public void put(Long submissionId, ValidatedDataFile file) {
        if (!mapImpl.containsKey(submissionId)) {
            mapImpl.put(submissionId, Collections.synchronizedMap(new HashMap<String, ValidatedDataFile>()));
        }
        mapImpl.get(submissionId).put(file.getName(), file);
    }

    public boolean containsFile(Long submissionId, String name) {
        return mapImpl.containsKey(submissionId) &&
                mapImpl.get(submissionId).containsKey(name);
    }

    public ValidatedDataFile get(Long submssionId, String name) {
        if (containsFile(submssionId, name)) {
            return mapImpl.get(submssionId).get(name);
        }

        return null;
    }

    public void remove(Long submissionId) {
        mapImpl.remove(submissionId);
    }
}
