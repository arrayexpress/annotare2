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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileRef implements Serializable {

    @JsonProperty("id")
    private Long fileId;

    @JsonProperty("type")
    private DataFileType type;

    FileRef() {
        /* used by GWT serialization */
    }

    @JsonCreator
    public FileRef(@JsonProperty("id") Long fileId, @JsonProperty("id") DataFileType type) {
        this.fileId = fileId;
        this.type = type;
    }

    public Long getFileId() {
        return fileId;
    }

    public DataFileType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileRef fileRef = (FileRef) o;

        if (!fileId.equals(fileRef.fileId)) return false;
        if (type != fileRef.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileId.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
