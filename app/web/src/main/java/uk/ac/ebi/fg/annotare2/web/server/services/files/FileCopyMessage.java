package uk.ac.ebi.fg.annotare2.web.server.services.files;

/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


import uk.ac.ebi.fg.annotare2.db.model.DataFile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FileCopyMessage implements Serializable {

    private static final long serialVersionUID = 7526471155622776147L;

    private long destinationId;
    private DataFileSource source;
    private boolean shouldRemoveSource;

    public FileCopyMessage(DataFileSource source, DataFile destination, boolean shouldRemoveSource) {
        this(source, destination.getId(), shouldRemoveSource);
    }

    private FileCopyMessage(DataFileSource source, long destinationId, boolean shouldRemoveSource) {
        this.destinationId = destinationId;
        this.source = source;
        this.shouldRemoveSource = shouldRemoveSource;
    }

    public DataFileSource getSource() {
        return source;
    }

    public long getDestinationId() {
        return destinationId;
    }

    public boolean shouldRemoveSource() {
        return shouldRemoveSource;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    @Override
    public String toString() {
        return getClass().getName() + "@{" +
                "destinationId=" + destinationId +
                ", source='" + source.toString() + '\'' +
                ", shouldRemove=" + shouldRemoveSource +
                '}';
    }
}
