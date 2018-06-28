package uk.ac.ebi.fg.annotare2.submission;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.DummySettingsEditor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.Editor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HasSubmissionSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HighThroughputSeqSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;
import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.SequencingUpdater;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.SEQUENCING;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater.experimentUpdater;

public class SequencingExpProfileType extends ExpProfileType {

    public SequencingExpProfileType(String title){
        super(title);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ExperimentUpdater getExperimentUpdater(ExperimentProfile exp) {
        return new SequencingUpdater(exp);
    }

    @Override
    public HasSubmissionSettings getExperimentSettings(SetupExpSubmissionView view) {

        //view is not being used here

        return new HighThroughputSeqSettings();
    }

    @Override
    public Editor<ExperimentSettings> getExperimentSettingsEditor(ExperimentSettingsPanel panel) {

        //there is no settings editor for sequencing experiments

        return new DummySettingsEditor();
    }

    @Override
    public ExperimentProfile setupExperiment(ExperimentSetupSettings settings) {
        ExperimentProfile exp = new ExperimentProfile(SEQUENCING);

        ExperimentUpdater updater = experimentUpdater(exp);
        updater.updateSettings(settings);
        updater.createSamples(settings.getNumberOfHybs(), "Sample #", 1);
        //updater.updateExtractAttributes(settings.getExtractValues(),settings.getNumberOfHybs());
        exp.setAeExperimentType("RNA-seq of coding RNA");
        return exp;
    }
}
