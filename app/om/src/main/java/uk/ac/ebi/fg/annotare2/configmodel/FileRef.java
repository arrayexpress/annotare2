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
public class FileRef implements HasProtocolAssignment {

    private long fileId;
    private String fileName;
    private ProtocolAssignment protocolAssignment;

    public FileRef(long fileId, String fileName, ProtocolAssignment protocolAssignment) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.protocolAssignment = protocolAssignment;
    }

    @Override
    public boolean hasProtocol(Protocol protocol) {
        return false;
    }

    @Override
    public void assignProtocol(Protocol protocol, boolean assigned) {

    }

    @Override
    public AssignmentItem getProtocolAssignmentItem() {
        return new AssignmentItem(Long.toString(fileId), fileName);
    }
}
