package uk.ac.ebi.fg.annotare2.web.gwt.common.model;

import com.google.gwt.safehtml.shared.SafeHtml;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.Editor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.TwoColorMicroarraySettingsEditor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HasSubmissionSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.TwoColorMicroarraySettings;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;
//import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.TwoColorMicroarrayUpdater;

import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

public class TwoColorMicroarrayExpProfileType extends ExpProfileType{

    public TwoColorMicroarrayExpProfileType(String title){
        super(title);
    }

    public TwoColorMicroarrayExpProfileType() {
    }

    @Override
    public String getTitle() {
        return title;
    }

    /*@Override
    public ExperimentUpdater getExperimentUpdater(ExperimentProfile exp) {
        return new TwoColorMicroarrayUpdater(exp);
    }
*/
    @Override
    public HasSubmissionSettings getExperimentSettings(SetupExpSubmissionView view) {
        return new TwoColorMicroarraySettings(view);
    }

    @Override
    public Editor<ExperimentSettings> getExperimentSettingsEditor(ExperimentSettingsPanel panel) {
        return new TwoColorMicroarraySettingsEditor(panel);
    }

    /*@Override
    public ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
        ExperimentProfile exp = new ExpProfile(this);
        exp.addLabel("Cy3");
        exp.addLabel("Cy5");

        ExperimentUpdater updater = experimentUpdater(exp);
        updater.updateSettings(settings);
        updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
        exp.setAeExperimentType("transcription profiling by array");
        return exp;
    }*/

    @Override
    public SafeHtml getSettingDetails(ExperimentSettings settings) {
        return null;
    }
}
