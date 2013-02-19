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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.magetab.table.operation.Operation;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UIGeneralInfo;

public interface IdfServiceAsync {

    void getGeneralInfo(int submissionId, AsyncCallback<UIGeneralInfo> async);

    void loadInvestigation(int submissionId, AsyncCallback<Table> async);

    void updateInvestigation(int submissionId, Operation operation, AsyncCallback<Void> async);

    void importInvestigation(int submissionId, AsyncCallback<Void> async);
}
