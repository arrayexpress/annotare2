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
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.core.AccessControlException;
import uk.ac.ebi.fg.annotare2.core.files.DataFileSource;
import uk.ac.ebi.fg.annotare2.core.files.LocalFileSource;
import uk.ac.ebi.fg.annotare2.core.transaction.Transactional;
import uk.ac.ebi.fg.annotare2.core.utils.URIEncoderDecoder;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataFilesService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.OperationFailedException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSenderImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManagerImpl;
import uk.ac.ebi.fg.annotare2.web.server.services.files.AnnotareUploadStorage;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FileAvailabilityChecker;
import uk.ac.ebi.fg.annotare2.web.server.services.files.FtpManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Ordering.natural;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiDataFileRows;

//import org.apache.commons.fileupload.FileItem;

public class DataFilesServiceImpl extends SubmissionBasedRemoteService implements DataFilesService {

    private static final long serialVersionUID = -1656426466008261462L;

    private final static String EMPTY_FILE_MD5 = "d41d8cd98f00b204e9800998ecf8427e";

    private final DataFileManagerImpl dataFileManager;
    private final FtpManager ftpManager;
    private final AnnotareUploadStorage uploadStorage;

    @Inject
    public DataFilesServiceImpl(AccountService accountService,
                                SubmissionManagerImpl submissionManager,
                                DataFileManagerImpl dataFileManager,
                                FtpManager ftpManager,
                                AnnotareUploadStorage uploadStorage,
                                EmailSenderImpl emailSender) {
        super(accountService, submissionManager, emailSender);
        this.dataFileManager = dataFileManager;
        this.ftpManager = ftpManager;
        this.uploadStorage = uploadStorage;
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
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

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public String initSubmissionFtpDirectory(long submissionId)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(submissionId, Permission.VIEW);
            String submissionDirectory = nullToEmpty(submission.getFtpSubDirectory());
            if (!submissionDirectory.isEmpty() && !ftpManager.doesExist(submissionDirectory)) {
                ftpManager.createDirectory(submissionDirectory);
            }
            return submissionDirectory;
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class, OperationFailedException.class})
    @Override
    public void registerUploadedFile(long submissionId, UploadedFileInfo fileInfo)
            throws ResourceNotFoundException, NoPermissionException, OperationFailedException {
        try {
            Submission submission = getSubmission(submissionId, Permission.UPDATE);
            long userId = getCurrentUser().getId();

            File uploadedFile = uploadStorage.getUploadedFile(userId, fileInfo);
                if (checkFileExists(submission, fileInfo.getFileName())) {
                    throw new OperationFailedException("File " + fileInfo + " already exists");
                } else if (0L == fileInfo.getFileSize()) {
                    throw new OperationFailedException("Empty file " + fileInfo.getFileName());
                } else {
                    saveFile(new LocalFileSource(uploadedFile), null, submission);
                    uploadStorage.removeUploadedFile(userId, fileInfo, false);
                }
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (OperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public String registerFtpFiles(long submissionId, List<String> filesInfo)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(submissionId, Permission.UPDATE);
            String ftpRoot = ftpManager.getRoot();
            String subDirectory = nullToEmpty(submission.getFtpSubDirectory());

            StringBuilder errors = new StringBuilder();
            Map<String,DataFileSource> files = new HashMap<>();
            FileAvailabilityChecker fileChecker = new FileAvailabilityChecker();
            for (String infoStr : filesInfo) {
                FtpFileInfo info = getFtpFileInfo(infoStr);
                if (null != info) {
                    URI fileUri = new URI(
                            ftpRoot + subDirectory + (subDirectory.isEmpty() ? "" : "/")
                                    + URIEncoderDecoder.encode(info.getFileName())
                    );
                    DataFileSource fileSource = DataFileSource.createFromUri(fileUri);

                    if (fileChecker.isAvailable(fileSource)) {
                        String digest = info.getMd5().toLowerCase();
                        if (checkFileExists(submission, info.getFileName())) {
                            errors.append(" - file \"").append(info.getFileName()).append("\" already exists").append("\n");
                        } else if (EMPTY_FILE_MD5.equals(digest)) {
                            errors.append("empty file \"").append(info.getFileName()).append("\"").append("\n");
                        } else {
                            files.put(digest, fileSource);
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
        } catch (URISyntaxException | IOException | DataSerializationException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void renameFile(long submissionId, long fileId, String fileName)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(submissionId, Permission.UPDATE);
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
    public void deleteFiles(long submissionId, List<Long> fileIds)
            throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(submissionId, Permission.UPDATE);
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
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (IOException | DataSerializationException e) {
            throw unexpected(e);
        }
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

//        boolean shouldStore = source instanceof LocalFileSource;
//        if (!shouldStore) {
//            if (submission instanceof ExperimentSubmission) {
//                shouldStore = !((ExperimentSubmission) submission).getExperimentProfile().getType().isSequencing();
//            } else if (submission instanceof ImportedExperimentSubmission) {
//                shouldStore = source.getName().matches("(?i)^.*[.]?(idf|sdrf)[.]txt$");
//            }
//        }
//
        dataFileManager.addFile(source, md5, submission, true);
        save(submission);
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
}
