package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;
import uk.ac.ebi.fg.annotare2.submission.model.Sample;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.PLANT_TWO_COLOR_MICROARRAY;

/**
 * Created by haideri on 17/10/2017.
 */
public class PlantTwoColorMicroarrayUpdater extends BasicExperimentUpdater {

    public PlantTwoColorMicroarrayUpdater(ExperimentProfile exp) {
        super(exp);
    }

    @Override
    protected Sample createSample(String name) {
        Sample sample = super.createSample(name);

        Extract extract = exp().createExtract(sample);
        extract.setName(sample.getName());

        for (String label : exp().getLabelNames()) {
            exp().createLabeledExtract(extract, label);
        }

        return sample;
    }

    @Override
    public void updateSettings(ExperimentSettings settings) {
        if (!settings.getExperimentType().isPlantTwoColorMicroarray()) {
            return;
        }
        exp().setArrayDesign(settings.getArrayDesign());
        super.updateSettings(settings);
    }
}
