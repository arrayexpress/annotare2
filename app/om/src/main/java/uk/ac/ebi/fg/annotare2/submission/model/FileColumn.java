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

package uk.ac.ebi.fg.annotare2.submission.model;


import java.io.Serializable;
import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class FileColumn implements Serializable {

    private FileType type;

    private Map<String, String> leId2FileNameMap;
    private Map<LabeledExtract, String> le2FileNameMap;

    FileColumn() {
        /* used by GWT serialization */
        this(null);
    }

    public FileColumn(FileType type) {
        this.type = type;
        le2FileNameMap = new HashMap<LabeledExtract, String>();
    }

    public FileType getType() {
        return type;
    }

    public String getFileName(LabeledExtract labeledExtract) {
        return le2FileNameMap.get(labeledExtract);
    }

    public void setFileName(LabeledExtract labeledExtract, String fileName) {
        if (fileName == null) {
            removeFileName(labeledExtract);
        } else {
            le2FileNameMap.put(labeledExtract, fileName);
        }
    }

    public void removeFileName(LabeledExtract labeledExtract) {
       le2FileNameMap.remove(labeledExtract);
    }

    public void removeFileName(String fileName) {
        List<LabeledExtract> keys = new ArrayList<LabeledExtract>(le2FileNameMap.keySet());
        for (LabeledExtract labeledExtract : keys) {
            if (fileName.equals(le2FileNameMap.get(labeledExtract))) {
                removeFileName(labeledExtract);
            }
        }
    }

    public Collection<LabeledExtract> getLabeledExtracts() {
        return new ArrayList<LabeledExtract>(le2FileNameMap.keySet());
    }

    void restoreObjects(ExperimentProfile exp) {
        for (String leId : leId2FileNameMap.keySet()) {
            LabeledExtract labeledExtract = exp.getLabeledExtract(leId);
            le2FileNameMap.put(labeledExtract, leId2FileNameMap.get(leId));
        }
        this.leId2FileNameMap = null;
    }
}


