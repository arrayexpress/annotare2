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

package uk.ac.ebi.fg.annotare2.submission.model;


import java.io.Serializable;
import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class FileColumn implements Serializable {

    private FileType type;

    private Map<String, String> assayId2FileNameMap;
    private Map<Assay, String> assay2FileNameMap;

    private Map<String, ProtocolAssignment> fileName2ProtocolAssignmentMap;

    FileColumn() {
        /* used by GWT serialization */
        this(null);
    }

    public FileColumn(FileType type) {
        this.type = type;
        assay2FileNameMap = new HashMap<Assay, String>();
        fileName2ProtocolAssignmentMap = new HashMap<String, ProtocolAssignment>();
    }

    public FileType getType() {
        return type;
    }

    public String getFileName(Assay assay) {
        return assay2FileNameMap.get(assay);
    }

    public void setFileName(Assay assay, String fileName) {
        if (fileName == null) {
            removeFileName(assay);
        } else {
            assay2FileNameMap.put(assay, fileName);
        }
    }

    public void removeFileName(Assay assay) {
        String fileName = assay2FileNameMap.remove(assay);
        if (!assay2FileNameMap.containsValue(fileName)) {
            fileName2ProtocolAssignmentMap.remove(fileName);
        }
    }

    public void removeFileName(String fileName) {
        List<Assay> keys = new ArrayList<Assay>(assay2FileNameMap.keySet());
        for (Assay assay : keys) {
            if (fileName.equals(assay2FileNameMap.get(assay))) {
                removeFileName(assay);
            }
        }
    }

    public Collection<FileRef> getFileRefs() {
        Set<FileRef> fileRefs = new HashSet<FileRef>();
        for (String fileName : assay2FileNameMap.values()) {
            fileRefs.add(getFileRef(fileName));
        }
        return fileRefs;
    }

    public Collection<Assay> getAssays() {
        return new ArrayList<Assay>(assay2FileNameMap.keySet());
    }

    boolean isProtocolAssigned2File(Protocol protocol, String fileName) {
        ProtocolAssignment assignment = fileName2ProtocolAssignmentMap.get(fileName);
        return assignment != null && assignment.contains(protocol);
    }

    void assignProtocol2File(Protocol protocol, String fileName, boolean assigned) {
        ProtocolAssignment assignment = fileName2ProtocolAssignmentMap.get(fileName);
        if (assignment == null) {
            assignment = new ProtocolAssignment();
            fileName2ProtocolAssignmentMap.put(fileName, assignment);
        }
        assignment.set(protocol, assigned);
    }

    public FileRef getFileRef(String fileName) {
        return new FileRef(fileName, this);
    }

    void fixMe(ExperimentProfile exp) {
        for (String assayId : assayId2FileNameMap.keySet()) {
            Assay assay = exp.getAssay(assayId);
            assay2FileNameMap.put(assay, assayId2FileNameMap.get(assayId));
        }
        this.assayId2FileNameMap = null;
    }
}


