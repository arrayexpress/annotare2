/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.services;

import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class UploadedFiles {

    private static final String GWTUPLOAD_ATTRIBUTE_NAME = "LAST_FILES";

    public static FileItem getFirst(HttpSession session) throws FileNotFoundException {
        List<FileItem> items = (List<FileItem>) session.getAttribute(GWTUPLOAD_ATTRIBUTE_NAME);
        if (items.isEmpty()) {
            throw new FileNotFoundException("Can't find the uploaded file.");
        }
        return items.get(0);
    }

    public static FileItem get(HttpSession session, String fileName) throws FileNotFoundException {
        List<FileItem> items = (List<FileItem>) session.getAttribute(GWTUPLOAD_ATTRIBUTE_NAME);
        for (FileItem item : items) {
            if (item.getName().equals(fileName)) {
                return item;
            }
        }
        throw new FileNotFoundException("Can't find the uploaded file.");
    }

}
