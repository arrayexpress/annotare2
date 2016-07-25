/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services.files;

import uk.ac.ebi.fg.gwt.resumable.server.FileChunkInfo;

public class AnnotareFileChunkInfo extends FileChunkInfo {

    Long userId;

    public static AnnotareFileChunkInfo createFrom(FileChunkInfo original, Long userId) {

        AnnotareFileChunkInfo info = new AnnotareFileChunkInfo();

        info.chunkNumber = original.chunkNumber;
        info.chunkSize = original.chunkSize;
        info.currentChunkSize = original.currentChunkSize;
        info.fileSize = original.fileSize;
        info.id = original.id;
        info.fileName = original.fileName;
        info.relativePath = original.relativePath;
        info.userId = userId;

        return info;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && null != userId;
    }
}