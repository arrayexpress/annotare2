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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;

import java.util.List;

@RemoteServiceRelativePath(DataFilesService.NAME)
public interface DataFilesService extends RemoteService {

    String NAME = "dataFileService";

    List<DataFileRow> getFiles(long submissionId) throws ResourceNotFoundException, NoPermissionException;

    String initSubmissionFtpDirectory(long submissionId) throws ResourceNotFoundException, NoPermissionException;

    List<Boolean> registerFilesBeforeUpload(long submissionId, List<UploadedFileInfo> filesInfo) throws ResourceNotFoundException, NoPermissionException;

    void addUploadedFile(long submissionId, UploadedFileInfo fileInfo) throws ResourceNotFoundException, NoPermissionException, OperationFailedException;

    String registerFtpFiles(long submissionId, List<String> filesInfo) throws ResourceNotFoundException, NoPermissionException;

    void renameFile(long submissionId, long fileId, String fileName) throws ResourceNotFoundException, NoPermissionException;

    void deleteFiles(long submissionId, List<Long> fileIds) throws ResourceNotFoundException, NoPermissionException;
}
