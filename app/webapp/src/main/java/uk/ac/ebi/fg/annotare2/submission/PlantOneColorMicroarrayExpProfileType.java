package uk.ac.ebi.fg.annotare2.submission;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.Editor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.PlantOneColorMicroarraySettingsEditor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HasSubmissionSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.PlantOneColorMicroarraySettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.PlantOneColorMicroarrayUpdater;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.PLANT_ONE_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

public class PlantOneColorMicroarrayExpProfileType extends ExpProfileType {

    public PlantOneColorMicroarrayExpProfileType(String title){
        super(title);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ExperimentUpdater getExperimentUpdater(ExperimentProfile exp) {
        return new PlantOneColorMicroarrayUpdater(exp);
    }

    @Override
    public HasSubmissionSettings getExperimentSettings(SetupExpSubmissionView view) {
        return new PlantOneColorMicroarraySettings(view);
    }

    @Override
    public Editor<ExperimentSettings> getExperimentSettingsEditor(ExperimentSettingsPanel panel) {
        return new PlantOneColorMicroarraySettingsEditor(panel);
    }

    @Override
    public ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
        ExperimentProfile exp = new ExperimentProfile(PLANT_ONE_COLOR_MICROARRAY);
        ExperimentUpdater updater = experimentUpdater(exp);
        updater.updateSettings(settings);
        updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
        exp.setAeExperimentType("transcription profiling by array");
        return exp;
    }
}
