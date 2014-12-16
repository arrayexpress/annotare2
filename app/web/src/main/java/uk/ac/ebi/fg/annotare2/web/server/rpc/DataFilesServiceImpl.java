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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.base.Function;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataFilesService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.HttpFileInfo;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.*;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Ordering.natural;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiDataFileRows;

public class DataFilesServiceImpl extends SubmissionBasedRemoteService implements DataFilesService {

    private static final long serialVersionUID = -1656426466008261462L;

    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;
    private final UserDao userDao;
    private final SubmissionFeedbackDao feedbackDao;
    private final EfoSearch efoSearch;
    private final EmailSender email;

    @Inject
    public DataFilesServiceImpl(AccountService accountService,
                                SubmissionManager submissionManager,
                                DataFileManager dataFileManager,
                                AnnotareProperties properties,
                                UserDao userDao,
                                SubmissionFeedbackDao feedbackDao,
                                EfoSearch efoSearch,
                                EmailSender emailSender) {
        super(accountService, submissionManager, emailSender);
        this.dataFileManager = dataFileManager;
        this.properties = properties;
        this.userDao = userDao;
        this.feedbackDao = feedbackDao;
        this.efoSearch = efoSearch;
        this.email = emailSender;
    }

    @Transactional
    @Override
    public List<DataFileRow> getFiles(long submissionId)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(submissionId, Permission.VIEW);
            Collection<DataFile> filesSortedByName = natural().onResultOf(new Function<DataFile, String>() {
                @Nullable
                @Override
                public String apply(@Nullable DataFile input) {
                    return (null != input && null != input.getName()) ? input.getName().toLowerCase() : null;
                }
            }).immutableSortedCopy(submission.getFiles());
            return uiDataFileRows(filesSortedByName);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Override
    public Map<Integer, String> registerHttpFiles(long submissionId, List<HttpFileInfo> filesInfo)
            throws ResourceNotFoundException, NoPermissionException {

        return null;
    }

    @Override
    public String registerFtpFiles(long submissionId, List<String> filesInfo)
            throws ResourceNotFoundException, NoPermissionException {

        return null;
    }

    @Override
    public void renameFile(long submissionId, long fileId, String fileName)
            throws ResourceNotFoundException, NoPermissionException {

    }

    @Override
    public void deleteFiles(long submissionId, List<Long> fileIds)
            throws ResourceNotFoundException, NoPermissionException {

    }
}
