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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.db.model.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.FeedbackDialog;

/**
 * @author Olga Melnichuk
 */
public interface EditorTitleBarView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitle(SubmissionType type, String accession);

    void setCurator(boolean isCurator);

    void setSubmissionType(SubmissionType type);

    void setSubmissionStatus(SubmissionStatus status);

    void setOwnedByCreator(boolean isOwnedbyCreator);

    void autoSaveStarted();

    void autoSaveStopped(String errorMessage);

    void criticalUpdateStarted();

    void criticalUpdateStopped();

    interface Presenter extends SetupExpSubmissionView.Presenter, FeedbackDialog.Presenter {

        void assignSubmissionToMe(AsyncCallback<Void> callback);

        void assignSubmissionToCreator(AsyncCallback<Void> callback);

        void validateSubmission(ValidationHandler handler);

        void submitSubmission(SubmissionHandler handler);

        String getSubmissionExportUrl();
    }

    interface ValidationHandler {

        void onSuccess(ValidationResult result);

        void onFailure();
    }

    interface SubmissionHandler {

        void onSuccess();

        void onFailure();
    }
}
