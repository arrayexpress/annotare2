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

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.UploadedFileInfo;

import java.util.List;

public interface DataFilesServiceAsync {

    void getFiles(long submissionId, AsyncCallback<List<DataFileRow>> async);

    void initSubmissionFtpDirectory(long submissionId, AsyncCallback<String> async);

    void registerFilesBeforeUpload(long submissionId, List<UploadedFileInfo> filesInfo, AsyncCallback<List<Boolean>> async);

    void addUploadedFile(long submissionId, UploadedFileInfo fileInfo, AsyncCallback<Void> async);

    void registerFtpFiles(long submissionId, List<String> filesInfo, AsyncCallback<String> async);

    void renameFile(long submissionId, long fileId, String fileName, AsyncCallback<Void> async);

    void deleteFiles(long submissionId, List<Long> files, AsyncCallback<Void> async);

    void registerGlobusFiles(long submissionId, List<String> filesInfo, AsyncCallback<String> asyncCallback);
}
