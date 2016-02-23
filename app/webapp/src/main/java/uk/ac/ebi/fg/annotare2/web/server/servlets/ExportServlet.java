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

import com.google.common.io.Files;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.User;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.server.ProtocolTypes;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabFiles;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.EfoSearch;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class ExportServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ExportServlet.class);

    @Inject
    private AccountService accountService;

    @Inject
    private SubmissionManager submissionManager;

    @Inject
    private ProtocolTypes protocolTypes;

    @Inject
    private EfoSearch efoSearch;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ExperimentSubmission submission = getSubmission(request);

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "inline; filename=submission.zip;");

        ServletOutputStream outputStream = response.getOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            exportMageTab(submission, zip);
        }
    }

    private void exportMageTab(ExperimentSubmission submission, ZipOutputStream zip) throws IOException, ServletException {
        try {
            MageTabFiles mageTab = MageTabFiles.createMageTabFiles(submission.getExperimentProfile(), efoSearch, true);
            File idfFile = mageTab.getIdfFile();
            zip.putNextEntry(new ZipEntry(idfFile.getName()));
            Files.asByteSource(idfFile).copyTo(zip);
            zip.closeEntry();
            File sdrfFile = mageTab.getSdrfFile();
            zip.putNextEntry(new ZipEntry(sdrfFile.getName()));
            Files.asByteSource(sdrfFile).copyTo(zip);
            zip.closeEntry();
            zip.finish();
        } catch (ParseException e) {
            throw servletException(e);
        } catch (DataSerializationException e) {
            throw servletException(e);
        }
    }

    private ExperimentSubmission getSubmission(HttpServletRequest request) throws ServletException {
        try {
            User currentUser = accountService.getCurrentUser(request.getSession());
            return submissionManager.getSubmission(currentUser, getSubmissionId(request), ExperimentSubmission.class, Permission.VIEW);
        } catch (RecordNotFoundException e) {
            throw servletException(e);
        } catch (AccessControlException e) {
            throw servletException(e);
        }
    }

    private long getSubmissionId(HttpServletRequest request) throws ServletException {
        String value = request.getParameter("submissionId");
        try {
            if (!isNullOrEmpty(value)) {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        throw servletException(new IllegalStateException("submission ID was not specified"));
    }

    private ServletException servletException(Throwable e) {
        log.error("MageTab export servlet failure", e);
        return new ServletException(e);
    }
}
