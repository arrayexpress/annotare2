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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.parser.IDFParser;
import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.core.components.EfoSearch;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ImportedExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.submission.model.ImportedExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataImportException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ImportSubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSenderImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileConnector;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class ImportSubmissionServiceImpl extends SubmissionBasedRemoteService implements ImportSubmissionService {

    private static final long serialVersionUID = 23916206047841933L;

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final DataFileManagerImpl dataFileManager;
    private final DataFileConnector dataFileConnector;
    private final AnnotareProperties properties;
    private final UserDao userDao;
    private final SubmissionFeedbackDao feedbackDao;
    private final EfoSearch efoSearch;
    private final EmailSenderImpl email;

    @Inject
    public ImportSubmissionServiceImpl(AccountService accountService,
                                       SubmissionManagerImpl submissionManager,
                                       DataFileManagerImpl dataFileManager,
                                       DataFileConnector dataFileConnector,
                                       AnnotareProperties properties,
                                       UserDao userDao,
                                       SubmissionFeedbackDao feedbackDao,
                                       EfoSearch efoSearch,
                                       EmailSenderImpl emailSender) {
        super(accountService, submissionManager, emailSender);
        this.dataFileManager = dataFileManager;
        this.dataFileConnector = dataFileConnector;
        this.properties = properties;
        this.userDao = userDao;
        this.feedbackDao = feedbackDao;
        this.efoSearch = efoSearch;
        this.email = emailSender;
    }

    @Transactional(rollbackOn = NoPermissionException.class)
    @Override
    public long createImportedExperiment() throws NoPermissionException {
        try {
            return createImportedExperimentSubmission().getId();
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Transactional
    @Override
    public ImportedExperimentProfile getExperimentProfile(long id)
            throws ResourceNotFoundException, NoPermissionException, DataImportException {

        try {
            ImportedExperimentSubmission submission = getImportedExperimentSubmission(id, Permission.VIEW);
            ImportedExperimentProfile profile = submission.getExperimentProfile();

            Collection<DataFile> idfFiles = submission.getIdfFiles();
            if (idfFiles.isEmpty()) {
                throw new DataImportException(
                        "IDF file was not found. Please ensure to have one IDF file (*.idf.txt or idf.txt)" +
                        " uploaded before proceeding.");
            } else if (idfFiles.size() > 1) {
                throw new DataImportException(
                        "Multiple IDF files found (" +
                                Joiner.on(", ").join(
                                        Collections2.transform(
                                                idfFiles,
                                                new Function<DataFile, String>() {
                                                        @Override
                                                        public String apply(DataFile f) {
                                                            return f.getName();
                                                        }
                                                }
                                        )
                                )
                        + "). Please ensure to have just one IDF file (*.idf.txt or idf.txt) uploaded before proceeding.");
            } else {
                ImportedExperimentProfile readProfile = readProfileFromIdf(submission, idfFiles.iterator().next());
                if (null != profile) {
                    profile.populate(readProfile);
                } else {
                    profile = readProfile;
                }
            }
            return profile;
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void updateExperimentProfile(long id, ImportedExperimentProfile profile)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            ImportedExperimentSubmission submission = getImportedExperimentSubmission(id, Permission.UPDATE);
            submission.setExperimentProfile(profile);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Override
    public ValidationResult validateSubmission(long id)
            throws ResourceNotFoundException, NoPermissionException {

        return null;
    }

    @Override
    public void submitSubmission(long id)
            throws ResourceNotFoundException, NoPermissionException {

    }

    @Override
    public void deleteSubmission(long id)
            throws ResourceNotFoundException, NoPermissionException {

    }

    @Override
    public void postFeedback(long id, Byte score, String comment)
            throws ResourceNotFoundException, NoPermissionException {

    }

    private ImportedExperimentProfile readProfileFromIdf(Submission submission, DataFile idfFile)
            throws DataImportException {

        ImportedExperimentProfile profile = new ImportedExperimentProfile();
        try {
            URL idfLocation = dataFileConnector.getFileUrl(getCurrentUser().getId(), submission.getId(), idfFile.getName());
            IDF idf = new IDF();
            idf.setLocation(idfLocation);
            IDFParser parser = new IDFParser();
            parser.parse(idfLocation.openStream(), idf);

            profile.setTitle(idf.investigationTitle);
            profile.setDescription(idf.experimentDescription);
            if (null != idf.getComments().get("AEExperimentType")) {
                profile.setAeExperimentType(idf.getComments().get("AEExperimentType").iterator().next());
            }
            if (null != idf.publicReleaseDate) {
                profile.setPublicReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse(idf.publicReleaseDate));
            }
        } catch (Exception x) {
            throw new DataImportException("There was an error processing IDF: " + x.getMessage());
        }
        return profile;
    }
}
