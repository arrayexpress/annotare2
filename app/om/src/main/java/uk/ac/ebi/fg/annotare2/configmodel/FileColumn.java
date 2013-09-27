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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.*;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileColumn implements Serializable {

    @JsonProperty("type")
    private FileType type;

    @JsonProperty("assay2FileName")
    private Map<String, String> assayId2FileName;
    private Map<Assay, String> assay2FileName;

    FileColumn() {
        /* used by GWT serialization */
    }

    @JsonCreator
    public FileColumn(@JsonProperty("type") FileType type) {
        this.type = type;
        assay2FileName = new HashMap<Assay, String>();
    }

    @JsonProperty("assay2FileName")
    Map<String, String> getAssayId2FileName() {
        if (assayId2FileName != null) {
            return assayId2FileName;
        }

        Map<String, String> map = new HashMap<String, String>();
        for (Assay assay : assay2FileName.keySet()) {
            String fileName = assay2FileName.get(assay);
            map.put(assay.getId(), fileName);
        }
        return map;
    }

    @JsonProperty("assay2FileName")
    void setAssayId2FileName(Map<String, String> assayId2FileName) {
        this.assayId2FileName = assayId2FileName;
    }

    public FileType getType() {
        return type;
    }

    public String getFileName(Assay assay) {
        return assay2FileName.get(assay);
    }

    public void setFileName(Assay assay, String fileName) {
        if (fileName == null) {
            removeFileName(assay);
        } else {
            assay2FileName.put(assay, fileName);
        }
    }

    public void removeFileName(Assay assay) {
        assay2FileName.remove(assay);
    }

    public void removeFileName(String fileName) {
        List<Assay> keys = new ArrayList<Assay>(assay2FileName.keySet());
        for (Assay assay : keys) {
            if (fileName.equals(assay2FileName.get(assay))) {
                assay2FileName.remove(assay);
            }
        }
    }

    void fixMe(ExperimentProfile exp) {
        Map<String, String> assayId2FileName = getAssayId2FileName();
        for (String assayId : assayId2FileName.keySet()) {
            Assay assay = exp.getAssay(assayId);
            assay2FileName.put(assay, assayId2FileName.get(assayId));
        }
        this.assayId2FileName = null;
    }

    public void clear() {
        assay2FileName = new HashMap<Assay, String>();
    }

    @JsonIgnore
    public Collection<? extends String> getFileNames() {
        return assay2FileName.values();
    }
}


