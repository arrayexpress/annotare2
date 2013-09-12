/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.configmodel;

import com.google.common.annotations.GwtCompatible;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class FileColumn implements Serializable {

    @JsonProperty("type")
    private FileType type;

    @JsonProperty("assay2File")
    private Map<String, FileRef> assayId2File;
    private Map<Assay, FileRef> assay2File;

    FileColumn() {
        /* used by GWT serialization */
    }

    @JsonCreator
    public FileColumn(@JsonProperty("type") FileType type) {
        this.type = type;
        assay2File = new HashMap<Assay, FileRef>();
    }

    @JsonProperty("assay2File")
    Map<String, FileRef> getAssayId2File() {
        if (assayId2File != null) {
            return assayId2File;
        }

        Map<String, FileRef> map = new HashMap<String, FileRef>();
        for (Assay assay : assay2File.keySet()) {
            FileRef fileRef = assay2File.get(assay);
            map.put(assay.getId(), fileRef);
        }
        return map;
    }

    @JsonProperty("assay2File")
    void setAssayId2File(Map<String, FileRef> assayId2File) {
        this.assayId2File = assayId2File;
    }

    public FileType getType() {
        return type;
    }

    public FileRef getFileRef(Assay assay) {
        return assay2File.get(assay);
    }

    public void removeFileRefs(Assay assay) {
        assay2File.remove(assay);
    }

    void fixMe(ExperimentProfile exp) {
        Map<String, FileRef> assayId2File = getAssayId2File();
        for (String assayId : assayId2File.keySet()) {
            Assay assay = exp.getAssay(assayId);
            assay2File.put(assay, assayId2File.get(assayId));
        }
        this.assayId2File = null;
    }
}


