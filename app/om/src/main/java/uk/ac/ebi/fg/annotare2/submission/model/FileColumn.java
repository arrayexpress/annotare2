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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class FileColumn implements Serializable {

    private static final long serialVersionUID = 2462330722506905893L;

    private FileType type;

    private Map<String, FileRef> leId2FileRefMap;

    @SuppressWarnings("unused")
    FileColumn() {
        /* used by GWT serialization */
        this(null);
    }

    public FileColumn(FileType type) {
        this.type = type;
        leId2FileRefMap = new HashMap<String, FileRef>();
    }

    public FileType getType() {
        return type;
    }

    public FileRef getFileRef(String labeledExtractId) {
        return leId2FileRefMap.get(labeledExtractId);
    }

    public void setFileRef(String labeledExtractId, FileRef fileRef) {
        if (fileRef == null) {
            removeFileByLabeledExtractId(labeledExtractId);
        } else {
            leId2FileRefMap.put(labeledExtractId, fileRef);
        }
    }

    public void removeFileByLabeledExtractId(String labeledExtractId) {
        leId2FileRefMap.remove(labeledExtractId);
    }


    public void removeFileByName(String fileName) {
        Collection<String> leIds = getLabeledExtractIds();
        for (String leId : leIds) {
            if (fileName.equals(leId2FileRefMap.get(leId).getName())) {
                removeFileByLabeledExtractId(leId);
            }
        }
    }

    public Collection<String> getLabeledExtractIds() {
        return Collections.unmodifiableCollection(leId2FileRefMap.keySet());
    }

    public Collection<FileRef> getFileRefs() {
        return Collections.unmodifiableCollection(leId2FileRefMap.values());
    }
}


