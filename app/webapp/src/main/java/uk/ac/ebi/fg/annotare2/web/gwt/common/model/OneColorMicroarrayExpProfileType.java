package uk.ac.ebi.fg.annotare2.web.gwt.common.model;

import com.google.gwt.safehtml.shared.SafeHtml;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.Editor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.OneColorMicroarraySettingsEditor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HasSubmissionSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.OneColorMicroarraySettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;
//import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.OneColorMicroarrayUpdater;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromTrustedString;
//import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

public class OneColorMicroarrayExpProfileType extends ExpProfileType {

    public OneColorMicroarrayExpProfileType(String title){
        super(title);
    }

    public OneColorMicroarrayExpProfileType() {
    }

    @Override
    public String getTitle() {
        return title;
    }

   /* @Override
    public ExperimentUpdater getExperimentUpdater(ExperimentProfile exp) {
        return new OneColorMicroarrayUpdater(exp);
    }*/

    @Override
    public HasSubmissionSettings getExperimentSettings(SetupExpSubmissionView view) {
        return new OneColorMicroarraySettings(view);
    }

    @Override
    public Editor<ExperimentSettings> getExperimentSettingsEditor(ExperimentSettingsPanel panel) {
        return new OneColorMicroarraySettingsEditor(panel);
    }

    /*@Override
    public ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
        ExperimentProfile exp = new ExpProfile(this);
        ExperimentUpdater updater = experimentUpdater(exp);
        updater.updateSettings(settings);
        updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
        exp.setAeExperimentType("transcription profiling by array");
        return exp;
    }*/

    @Override
    public SafeHtml getSettingDetails(ExperimentSettings settings){
        return templates.div(fromTrustedString(
                settings.getExperimentType().getTitle() +
                        "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()) +
                        "<br/> - label: " + valueOrNone(settings.getLabel())));
    }
}
