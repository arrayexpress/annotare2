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

package uk.ac.ebi.fg.annotare2.submission.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileColumn10 implements Serializable {

    private FileType type;
    private Map<String, String> leId2FileNameMap;

    public FileColumn10() {
        this.type = null;
        this.leId2FileNameMap = new HashMap<String, String>();
    }

    public FileColumn toFileColumn() {
        FileColumn column = new FileColumn(type);

        for (String leId : leId2FileNameMap.keySet()) {
            column.setFileRef(leId, new FileRef(leId2FileNameMap.get(leId), null));
        }

        return column;
    }
}
