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
        if (null == fileRef) {
            removeFileByLabeledExtractId(labeledExtractId);
        } else {
            leId2FileRefMap.put(labeledExtractId, fileRef);
        }
    }

    public void removeFileByLabeledExtractId(String labeledExtractId) {
        leId2FileRefMap.remove(labeledExtractId);
    }

    public void replaceFile(FileRef oldFileRef, FileRef newFileRef) {
        for (String leId : leId2FileRefMap.keySet()) {
            if (oldFileRef.equals(leId2FileRefMap.get(leId))) {
                leId2FileRefMap.put(leId, newFileRef);
            }
        }
    }

    public void removeFile(FileRef fileRef) {
        Iterator<Map.Entry<String,FileRef>> iterator = leId2FileRefMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,FileRef> file = iterator.next();
            if (file.getValue().equals(fileRef)){
                iterator.remove();
            }
        }
    }

    public void removeAll() {
        leId2FileRefMap.clear();
    }

    public Collection<String> getLabeledExtractIds() {
        return Collections.unmodifiableCollection(leId2FileRefMap.keySet());
    }

    public Collection<FileRef> getFileRefs() {
        return Collections.unmodifiableCollection(leId2FileRefMap.values());
    }
}


