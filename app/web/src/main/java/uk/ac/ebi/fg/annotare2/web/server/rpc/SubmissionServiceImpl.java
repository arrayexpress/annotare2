/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.renderer.IDFWriter;
import uk.ac.ebi.arrayexpress2.magetab.renderer.adaptor.SDRFGraphWriter;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.SubmissionFeedbackDao;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.*;
import uk.ac.ebi.fg.annotare2.db.model.enums.DataFileStatus;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.submission.model.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.HttpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator;
import uk.ac.ebi.fg.annotare2.web.server.magetab.tsv.TsvParser;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.*;
import uk.ac.ebi.fg.annotare2.web.server.services.files.DataFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FileAvailabilityChecker;
import uk.ac.ebi.fg.annotare2.web.server.services.files.LocalFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.files.RemoteFileSource;
import uk.ac.ebi.fg.annotare2.web.server.services.utils.URIEncoderDecoder;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Ordering.natural;
import static uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabGenerator.restoreOriginalNameValues;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentBuilderFactory.createExperimentProfile;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.*;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends SubmissionBasedRemoteService implements SubmissionService {

    private static final long serialVersionUID = 6482329782917056447L;

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;
    private final UserDao userDao;
    private final SubmissionFeedbackDao feedbackDao;
    private final EfoSearch efoSearch;
    private final EmailSender email;

    private final static String EMPTY_FILE_MD5 = "d41d8cd98f00b204e9800998ecf8427e";

    @Inject
    public SubmissionServiceImpl(AccountService accountService,
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
    public SubmissionDetails getSubmissionDetails(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = getSubmission(id, Permission.VIEW);
            return uiSubmissionDetails(sb);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Transactional
    @Override
    public ArrayDesignDetailsDto getArrayDesignDetails(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission sb = getArrayDesignSubmission(id, Permission.VIEW);
            return uiArrayDesignDetails(sb);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional
    @Override
    public Table getIdfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return asIdfTable(exp);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional
    @Override
    public Table getSdrfTable(long id) throws NoPermissionException, ResourceNotFoundException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            ExperimentProfile exp = submission.getExperimentProfile();
            return asSdrfTable(exp);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (ParseException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    private Table asIdfTable(ExperimentProfile exp) throws IOException, ParseException {
        MAGETABInvestigation mageTab = (new MageTabGenerator(exp, efoSearch)).generate();
        StringWriter out = new StringWriter();
        new IDFWriter(out).write(mageTab.IDF);
        return new TsvParser().parse(new ByteArrayInputStream(out.toString().getBytes(Charsets.UTF_8)));
    }

    private Table asSdrfTable(ExperimentProfile exp) throws IOException, ParseException {
        MAGETABInvestigation mageTab = (new MageTabGenerator(exp, efoSearch)).generate();
        StringWriter out = new StringWriter();
        new SDRFGraphWriter(out).write(mageTab.SDRF);
        String sdrf = restoreOriginalNameValues(out.toString());
        return new TsvParser().parse(new ByteArrayInputStream(sdrf.getBytes(Charsets.UTF_8)));
    }

    @Transactional
    @Override
    public ExperimentProfile loadExperiment(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            return submission.getExperimentProfile();
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional
    @Override
    public ArrayList<DataFileRow> loadDataFiles(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.VIEW);
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

    @Transactional(rollbackOn = NoPermissionException.class)
    @Override
    public long createExperiment() throws NoPermissionException {
        try {
            return createExperimentSubmission().getId();
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Transactional(rollbackOn = NoPermissionException.class)
    @Override
    public long createArrayDesign() throws NoPermissionException {
        try {
            return createArrayDesignSubmission().getId();
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
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

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void setupExperiment(final long id, final ExperimentSetupSettings settings)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    getExperimentSubmission(id, Permission.UPDATE);
            submission.setExperimentProfile(createExperimentProfile(settings, submission.getCreatedBy()));
            save(submission);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void submitSubmission(final long id)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            if (submission.getStatus().canSubmit()) {
                storeAssociatedFiles(submission);
                submission.setStatus(
                        SubmissionStatus.IN_PROGRESS == submission.getStatus() ?
                                SubmissionStatus.SUBMITTED : SubmissionStatus.RESUBMITTED
                );
                submission.setOwnedBy(userDao.getCuratorUser());
                save(submission);
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        } catch (URISyntaxException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public ExperimentProfile updateExperiment(final long id, final List<ExperimentUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            ExperimentProfile experiment = submission.getExperimentProfile();
            experimentUpdater(experiment).run(commands);
            submission.setExperimentProfile(experiment);
            submission.setTitle(experiment.getTitle());
            save(submission);
            return experiment;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public ArrayDesignUpdateResult updateArrayDesign(final long id, final List<ArrayDesignUpdateCommand> commands) throws ResourceNotFoundException, NoPermissionException {
        try {
            ArrayDesignSubmission submission = getArrayDesignSubmission(id, Permission.UPDATE);
            ArrayDesignHeader header = submission.getHeader();
            ArrayDesignUpdateResult result = new ArrayDesignUpdatePerformerImpl(header).run(commands);
            submission.setHeader(header);
            submission.setTitle(header.getName());
            save(submission);
            return result;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public HashMap<Integer, String> registerHttpFiles(long id, List<HttpFileInfo> filesInfo) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.UPDATE);
            HashMap<Integer, String> errors = new HashMap<Integer, String>();
            int index = 0;
            for (HttpFileInfo info : filesInfo) {
                File uploadedFile = new File(properties.getHttpUploadDir(), info.getFileName());
                FileItem received = UploadedFiles.get(getSession(), info.getFieldName());
                if (checkFileExists(submission, info.getFileName())) {
                    errors.put(index, "file already exists");
                } else if (0L == received.getSize()) {
                    errors.put(index, "empty file");
                } else {
                    received.write(uploadedFile);
                    saveFile(new LocalFileSource(uploadedFile), null, submission);
                }
                index++;
            }
            UploadedFiles.removeSessionFiles(getSession());
            return errors;
        } catch (Exception e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public String registerFtpFiles(long id, List<String> filesInfo) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.UPDATE);

            String ftpRoot = properties.getFtpPickUpDir();
            if (ftpRoot.startsWith("/")) {
                ftpRoot = "file://" + ftpRoot;
            }
            if (!ftpRoot.endsWith("/")) {
                ftpRoot = ftpRoot + "/";
            }

            StringBuilder errors = new StringBuilder();
            Map<String,DataFileSource> files = new HashMap<String, DataFileSource>();
            FileAvailabilityChecker fileChecker = new FileAvailabilityChecker();
            for (String infoStr : filesInfo) {
                FtpFileInfo info = getFtpFileInfo(infoStr);
                if (null != info) {
                    URI fileUri = new URI(ftpRoot + URIEncoderDecoder.encode(info.getFileName()));
                    DataFileSource fileSource = DataFileSource.createFromUri(fileUri);

                    if (fileChecker.isAvailable(fileSource)) {
                        if (checkFileExists(submission, info.getFileName())) {
                            errors.append(" - file \"").append(info.getFileName()).append("\" already exists").append("\n");
                        } else if (EMPTY_FILE_MD5.equals(info.getMd5())) {
                            errors.append("empty file \"").append(info.getFileName()).append("\"").append("\n");
                        } else {
                            files.put(info.getMd5(), fileSource);
                        }
                    } else {
                        errors.append(" - file \"").append(info.getFileName()).append("\" not found").append("\n");
                    }
                } else {
                    errors.append(" - unrecognized format \"").append(infoStr).append("\"").append("\n");
                }
            }
            if (0 == errors.length()) {
                for (Map.Entry<String, DataFileSource> fileToSave : files.entrySet()) {
                    saveFile(fileToSave.getValue(), fileToSave.getKey(), submission);
                }
            }
            return errors.toString();
        } catch (URISyntaxException e) {
            throw unexpected(e);
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public ExperimentProfile renameDataFile(final long id, final long fileId, final String fileName) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.UPDATE);
            ExperimentSubmission experimentSubmission = submission instanceof ExperimentSubmission ?
                    (ExperimentSubmission)submission : null;
            ExperimentProfile experiment = submission instanceof ExperimentSubmission ?
                    experimentSubmission.getExperimentProfile() : null;
            DataFile dataFile = dataFileManager.get(fileId);
            if (submission.getFiles().contains(dataFile) && dataFile.getStatus().isFinal()) {
                if (null != experiment) {
                    experiment.renameFile(new FileRef(dataFile.getName(), dataFile.getDigest()), fileName);
                    experimentSubmission.setExperimentProfile(experiment);
                }
                dataFileManager.renameDataFile(dataFile, fileName);

                save(submission);
            }

            return experiment;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public ExperimentProfile deleteDataFiles(final long id, final List<Long> fileIds) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.UPDATE);
            ExperimentSubmission experimentSubmission = submission instanceof ExperimentSubmission ?
                    (ExperimentSubmission)submission : null;
            ExperimentProfile experiment = submission instanceof ExperimentSubmission ?
                    experimentSubmission.getExperimentProfile() : null;
            for (Long fileId : fileIds) {
                DataFile dataFile = dataFileManager.get(fileId);
                if (submission.getFiles().contains(dataFile)) {
                    if (null != experiment) {
                        experiment.removeFile(new FileRef(dataFile.getName(), dataFile.getDigest()));
                        experimentSubmission.setExperimentProfile(experiment);
                    }

                    submission.getFiles().remove(dataFile);
                    dataFileManager.deleteDataFile(dataFile);
                }
            }
            save(submission);
            return experiment;

        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (DataSerializationException e) {
            throw unexpected(e);
        }
    }

    @Transactional
    @Override
    public void deleteSubmission(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = getSubmission(id, Permission.UPDATE);
            deleteSubmissionSoftly(sb);
            for (DataFile df : sb.getFiles()) {
                dataFileManager.deleteDataFileSoftly(df);
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (IOException e) {
            throw unexpected(e);
        }
    }

    @Transactional
    @Override
    public void postFeedback(long id, Byte score, String comment) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.VIEW);
            SubmissionFeedback feedback = feedbackDao.create(score, submission);
            feedback.setComment(comment);
            feedbackDao.save(feedback);
            sendFeedbackEmail(score, comment);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    private void sendFeedbackEmail(Byte score, String comment) {
        User u = getCurrentUser();
        try {
            email.sendFromTemplate(EmailSender.FEEDBACK_TEMPLATE,
                    ImmutableMap.of(
                            "from.name", u.getName(),
                            "from.email", u.getEmail(),
                            "feedback.rating", null != score ? String.valueOf(score) : "-",
                            "feedback.comment", comment
                    ));
        } catch (MessagingException x) {
            log.error("Unable to send feedback email", x);
        }
    }

    private final Pattern md5Pattern1 = Pattern.compile("^\\s*[mM][dD]5\\s+\\((.+)\\)\\s*=\\s*([0-9a-fA-F]{32})\\s*$");
    private final Pattern md5Pattern2 = Pattern.compile("^\\s*([0-9a-fA-F]{32})\\s+(.+)\\s*$");
    private final Pattern md5Pattern3 = Pattern.compile("^\\s*(.+\\S)\\s+([0-9a-fA-F]{32})\\s*$");

    private FtpFileInfo getFtpFileInfo(final String str) {
        if (null == str || str.isEmpty())
            return null;
        String fileName = null, md5 = null;

        Matcher matcher1 = md5Pattern1.matcher(str);
        Matcher matcher2 = md5Pattern2.matcher(str);
        Matcher matcher3 = md5Pattern3.matcher(str);

        if (matcher1.find()) {
            fileName = matcher1.group(1);
            md5 = matcher1.group(2);
        } else if (matcher2.find()) {
            md5 = matcher2.group(1);
            fileName = matcher2.group(2);
        } else if (matcher3.find()) {
            fileName = matcher3.group(1);
            md5 = matcher3.group(2);
        }
        if (null != md5) {
            return new FtpFileInfo(fileName, md5);
        }
        return null;
    }

    private boolean checkFileExists(final Submission submission, final String fileName) {
        Set<DataFile> files = submission.getFiles();
        for (DataFile dataFile : files) {
            if (fileName.equals(dataFile.getName())) {
                return true;
            }
        }
        return false;
    }

    private void saveFile(final DataFileSource source, final String md5, final Submission submission)
            throws DataSerializationException, IOException {

        boolean shouldStore = !(source instanceof RemoteFileSource &&
                submission instanceof ExperimentSubmission &&
                ((ExperimentSubmission)submission).getExperimentProfile().getType().isSequencing());

        if (source instanceof RemoteFileSource && submission instanceof ImportedExperimentSubmission) {
            shouldStore = false;
        }

        dataFileManager.addFile(source, md5, submission, shouldStore);
        save(submission);
    }

    private void storeAssociatedFiles(ExperimentSubmission submission)
            throws DataSerializationException, URISyntaxException, IOException {

        Set<DataFile> files = dataFileManager.getAssignedFiles(
                submission,
                FileType.RAW_MATRIX_FILE,
                FileType.PROCESSED_FILE,
                FileType.PROCESSED_MATRIX_FILE
        );

        for (DataFile file : files) {
            if (null != file && DataFileStatus.ASSOCIATED == file.getStatus()) {
                dataFileManager.storeAssociatedFile(file);
            }
        }
    }
}
