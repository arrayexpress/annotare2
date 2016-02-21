/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public interface RemoteFileAccess {

    boolean isSupported(URI file);

    boolean isAccessible(URI file) throws IOException;

    String getDigest(URI file) throws IOException;

    void copyTo(URI file, File destination) throws IOException;

    URI rename(URI file, String newName) throws IOException;

    void delete(URI file) throws IOException;
}
