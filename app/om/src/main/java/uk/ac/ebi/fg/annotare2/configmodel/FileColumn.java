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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class FileColumn implements Serializable {

    @JsonProperty("type")
    private FileType type;

    @JsonProperty("assay2FileId")
    private Map<String, Long> assayId2FileId;
    private Map<Assay, Long> assay2FileId;

    FileColumn() {
        /* used by GWT serialization */
    }

    @JsonCreator
    public FileColumn(@JsonProperty("type") FileType type) {
        this.type = type;
        assay2FileId = new HashMap<Assay, Long>();
    }

    @JsonProperty("assay2FileId")
    Map<String, Long> getAssayId2FileId() {
        if (assayId2FileId != null) {
            return assayId2FileId;
        }

        Map<String, Long> map = new HashMap<String, Long>();
        for (Assay assay : assay2FileId.keySet()) {
            Long fileId = assay2FileId.get(assay);
            map.put(assay.getId(), fileId);
        }
        return map;
    }

    @JsonProperty("assay2FileId")
    void setAssayId2FileId(Map<String, Long> assayId2FileId) {
        this.assayId2FileId = assayId2FileId;
    }

    public FileType getType() {
        return type;
    }

    public Long getFileId(Assay assay) {
        return assay2FileId.get(assay);
    }

    public void setFileId(Assay assay, Long fileId) {
        if (fileId == null) {
            removeFileId(assay);
        } else {
            assay2FileId.put(assay, fileId);
        }
    }

    public void removeFileId(Assay assay) {
        assay2FileId.remove(assay);
    }

    public void removeFileId(long fileId) {
        List<Assay> keys = new ArrayList<Assay>(assay2FileId.keySet());
        for (Assay assay : keys) {
            if (assay2FileId.get(assay) == fileId) {
                assay2FileId.remove(assay);
            }
        }
    }

    void fixMe(ExperimentProfile exp) {
        Map<String, Long> assayId2File = getAssayId2FileId();
        for (String assayId : assayId2File.keySet()) {
            Assay assay = exp.getAssay(assayId);
            assay2FileId.put(assay, assayId2File.get(assayId));
        }
        this.assayId2FileId = null;
    }

    public void clearFileRefs() {
        assay2FileId = new HashMap<Assay, Long>();
    }
}


