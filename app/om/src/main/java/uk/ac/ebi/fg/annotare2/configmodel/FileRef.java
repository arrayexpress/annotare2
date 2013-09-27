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

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
class FileRef implements HasProtocolAssignment {

    private String fileName;
    private FileColumn fileColumn;

    public FileRef(String fileName, FileColumn fileColumn) {
        this.fileName = fileName;
        this.fileColumn = fileColumn;
    }

    @Override
    public boolean hasProtocol(Protocol protocol) {
        return fileColumn.isProtocolAssigned2File(protocol, fileName);
    }

    @Override
    public void assignProtocol(Protocol protocol, boolean assigned) {
        fileColumn.assignProtocol2File(protocol, fileName, assigned);
    }

    @Override
    public AssignmentItem getProtocolAssignmentItem() {
        return new AssignmentItem(fileName, fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileRef fileRef = (FileRef) o;

        if (!fileName.equals(fileRef.fileName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }
}
