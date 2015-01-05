/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfileUpdates;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface ProtocolsView extends IsWidget {

    void setData(List<ProtocolRow> rows);

    void setPresenter(Presenter presenter);

    public interface Presenter extends AddProtocolDialog.Presenter {

        void createProtocol(ProtocolType protocolType);

        void updateProtocol(ProtocolRow row);

        void removeProtocols(ArrayList<ProtocolRow> protocolRows);

        void getAssignmentProfileAsync(int protocolId, AsyncCallback<ProtocolAssignmentProfile> asyncCallback);

        void updateProtocolAssignments(ProtocolAssignmentProfileUpdates updates);

        void moveProtocolDown(ProtocolRow row);

        void moveProtocolUp(ProtocolRow row);

        void getSequencingHardwareAsync(AsyncCallback<List<String>> callback);
    }
}
