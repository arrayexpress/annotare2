package uk.ac.ebi.fg.annotare2.submission;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.Editor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HasSubmissionSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;

public abstract class ExpProfileType {

    protected String title;

   public ExpProfileType(String title){
       this.title = title;
   }

   public abstract String getTitle();

   public abstract ExperimentUpdater getExperimentUpdater(ExperimentProfile exp);

   public abstract HasSubmissionSettings getExperimentSettings(SetupExpSubmissionView view);

   public abstract Editor<ExperimentSettings> getExperimentSettingsEditor(ExperimentSettingsPanel panel);

   public abstract ExperimentProfile setupExperiment(ExperimentSetupSettings settings);
}
