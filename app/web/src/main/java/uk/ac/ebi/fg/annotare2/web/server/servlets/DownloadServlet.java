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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.servlets;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ExportServlet.class);

    @Inject
    private AccountService accountService;

    @Inject
    private SubmissionManager submissionManager;

    @Inject
    private DataFileStore dataFileStore;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataFile file = getDataFile(request);

        if (null != file && DataFileStatus.STORED == file.getStatus()) {
            File f = dataFileStore.get(file.getDigest());

            OutputStream out = response.getOutputStream();
            response.setContentType(getServletContext().getMimeType(f.getName()));
            response.setContentLength((int) f.length());
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s\"", file.getName()));

            FileInputStream in = new FileInputStream(f);
            try {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } finally {
                 in.close();
            }
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private DataFile getDataFile(HttpServletRequest request) throws ServletException {
        long fileId = getId(request, "fileId");
        Submission submission = getSubmission(request);
        Set<DataFile> files = submission.getFiles();
        for (DataFile file : files) {
            if (file.getId() == fileId) {
                return file;
            }
        }
        return null;
    }

    private Submission getSubmission(HttpServletRequest request) throws ServletException {
        try {
            User currentUser = accountService.getCurrentUser(request.getSession());
            return submissionManager.getSubmission(currentUser, getId(request, "submissionId"), Submission.class, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw servletException(e);
        } catch (AccessControlException e) {
            throw servletException(e);
        }
    }

    private long getId(HttpServletRequest request, String paramName) throws ServletException {
        String value = request.getParameter(paramName);
        try {
            if (!isNullOrEmpty(value)) {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        throw servletException(new IllegalStateException("ID was not specified"));
    }

    private ServletException servletException(Throwable e) {
        log.error("MAGE-TAB Download Servlet failure", e);
        return new ServletException(e);
    }
}