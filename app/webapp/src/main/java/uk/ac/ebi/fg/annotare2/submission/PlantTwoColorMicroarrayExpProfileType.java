package uk.ac.ebi.fg.annotare2.submission;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.Editor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.PlantTwoColorMicroarraySettingsEditor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HasSubmissionSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.PlantTwoColorMicroarraySettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.PlantTwoColorMicroarrayUpdater;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.PLANT_TWO_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

public class PlantTwoColorMicroarrayExpProfileType extends ExpProfileType {

    public PlantTwoColorMicroarrayExpProfileType(String title){
        super(title);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ExperimentUpdater getExperimentUpdater(ExperimentProfile exp) {
        return new PlantTwoColorMicroarrayUpdater(exp);
    }

    @Override
    public HasSubmissionSettings getExperimentSettings(SetupExpSubmissionView view) {
        return new PlantTwoColorMicroarraySettings(view);
    }

    @Override
    public Editor<ExperimentSettings> getExperimentSettingsEditor(ExperimentSettingsPanel panel) {
        return new PlantTwoColorMicroarraySettingsEditor(panel);
    }

    @Override
    public ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
        ExperimentProfile exp = new ExperimentProfile(PLANT_TWO_COLOR_MICROARRAY);
        exp.addLabel("Cy3");
        exp.addLabel("Cy5");

        ExperimentUpdater updater = experimentUpdater(exp);
        updater.updateSettings(settings);
        updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
        exp.setAeExperimentType("transcription profiling by array");
        return exp;
    }
}
