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

package uk.ac.ebi.fg.annotare2.web.server.servlets;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.files.AnnotareFileChunkInfo;
import uk.ac.ebi.fg.annotare2.web.server.services.files.AnnotareUploadStorage;
import uk.ac.ebi.fg.gwt.resumable.server.FileChunkInfo;
import uk.ac.ebi.fg.gwt.resumable.server.ResumableUploadServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class UploadServlet extends ResumableUploadServlet {

    private final AccountService accountService;

    @Inject
    public UploadServlet(AnnotareUploadStorage storage, AccountService accountService) {
        this.accountService = accountService;
        setStorage(storage);
    }

    @Override
    protected FileChunkInfo buildChunkInfo(HttpServletRequest request) throws IOException, ServletException {
        Long userId = accountService.getCurrentUser(request.getSession()).getId();
        return AnnotareFileChunkInfo.createFrom(super.buildChunkInfo(request), userId);
    }
}
