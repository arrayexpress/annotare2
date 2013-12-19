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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.inject.Inject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.submission.model.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.submission.transform.DataSerializationException;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.dao.UserDao;
import uk.ac.ebi.fg.annotare2.db.model.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.db.model.DataFile;
import uk.ac.ebi.fg.annotare2.db.model.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.db.model.enums.Permission;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.server.magetab.tsv.TsvParser;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ExperimentUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.server.magetab.MageTabFiles;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileManager;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.UploadedFiles;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import javax.jms.JMSException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.Files.hash;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentBuilderFactory.createExperimentProfile;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.*;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends SubmissionBasedRemoteService implements SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    private final DataFileManager dataFileManager;
    private final AnnotareProperties properties;
    private final UserDao userDao;

    @Inject
    public SubmissionServiceImpl(AccountService accountService,
                                 SubmissionManager submissionManager,
                                 DataFileManager dataFileManager,
                                 AnnotareProperties properties,
                                 UserDao userDao) {
        super(accountService, submissionManager);
        this.dataFileManager = dataFileManager;
        this.properties = properties;
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public SubmissionDetails getSubmission(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission sb = getSubmission(id, Permission.VIEW);
            SubmissionDetails sd = uiSubmissionDetails(sb);
            return sd;
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
        MageTabFiles mageTab = MageTabFiles.createMageTabFiles(exp, false);
        return new TsvParser().parse(new FileInputStream(mageTab.getIdfFile()));
        //TODO: delete temporary file ?
    }

    private Table asSdrfTable(ExperimentProfile exp) throws IOException, ParseException {
        MageTabFiles mageTab = MageTabFiles.createMageTabFiles(exp, false);
        return new TsvParser().parse(new FileInputStream(mageTab.getSdrfFile()));
        //TODO: delete temporary file ?
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
    public List<DataFileRow> loadDataFiles(long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.VIEW);
            return uiDataFileRows(submission.getFiles());
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

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void setupExperiment(final long id, final ExperimentSetupSettings settings) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission =
                    getExperimentSubmission(id, Permission.UPDATE);
            submission.setExperimentProfile(createExperimentProfile(settings));
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
    public void submitSubmission(final long id) throws ResourceNotFoundException, NoPermissionException {
        try {
            Submission submission = getSubmission(id, Permission.UPDATE);
            submission.setStatus(SubmissionStatus.SUBMITTED);
            submission.setOwnedBy(userDao.getCuratorUser());
            save(submission);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
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
    public void uploadDataFile(long id, String fileNameOrPath) throws ResourceNotFoundException, NoPermissionException {
        try {
            String fileName = getFileNameOnly(fileNameOrPath);
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            FileItem fileItem = UploadedFiles.get(getSession(), fileName);
            File file = new File(properties.getHttpUploadDir(), fileName);
            Streams.copy(fileItem.getInputStream(), new FileOutputStream(file), true);
            saveFile(file, submission);
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (JMSException e) {
            throw unexpected(e);
        }
    }

    /**
     * Extracts file name from a string.
     *
     * Different browsers return file name of uploaded file differently: chrome and safary return "C:\fakepath\fileName",
     * but firefox returns just a fileName.
     *
     * @param str file name or path
     * @return a string containing just a file name
     */
    private static String getFileNameOnly(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        str = str.replaceAll("\\\\", "/");
        String[] parts = str.split("/");
        return parts[parts.length - 1];
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public Map<Integer, String> registryFtpFiles(long id, List<FtpFileInfo> details) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            File ftpRoot = properties.getFilePickUpDir();
            Map<Integer, String> errors = new HashMap<Integer, String>();
            int index = 0;
            for (FtpFileInfo info : details) {
                File file = new File(ftpRoot, info.getFileName());
                if (fileExists(file, info.getMd5())) {
                    saveFile(file, submission);
                } else {
                    errors.put(index, "File not found");
                }
                index++;
            }
            return errors;
        } catch (FileNotFoundException e) {
            throw unexpected(e);
        } catch (IOException e) {
            throw unexpected(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (JMSException e) {
            throw unexpected(e);
        }
    }

    @Transactional(rollbackOn = {NoPermissionException.class, ResourceNotFoundException.class})
    @Override
    public void deleteDataFile(final long id, final long fileId) throws ResourceNotFoundException, NoPermissionException {
        try {
            ExperimentSubmission submission = getExperimentSubmission(id, Permission.UPDATE);
            DataFile dataFile = dataFileManager.get(fileId);
            if (!submission.getFiles().contains(dataFile)) {
                return;
            }

            String fileName = dataFile.getName();
            ExperimentProfile expProfile = submission.getExperimentProfile();
            expProfile.removeFile(fileName);
            submission.setExperimentProfile(expProfile);

            submission.getFiles().remove(dataFile);
            dataFileManager.deleteDataFile(dataFile);

            save(submission);
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

    private void saveFile(final File file, final ExperimentSubmission submission) throws JMSException {
        String fileName = file.getName();
        Set<DataFile> files = submission.getFiles();
        for (DataFile dataFile : files) {
            if (fileName.equals(dataFile.getName())) {
                // TODO: show message: file with such name already exists
                return;
            }
        }

        dataFileManager.upload(file, submission);
        save(submission);
    }

    private static boolean fileExists(File file, String md5) throws IOException {
        return file.exists() && md5.equals(hash(file, md5()).toString());
    }
}
