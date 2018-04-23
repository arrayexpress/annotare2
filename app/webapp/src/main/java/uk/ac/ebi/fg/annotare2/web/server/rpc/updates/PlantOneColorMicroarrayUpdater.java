package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;
import uk.ac.ebi.fg.annotare2.submission.model.Sample;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;

import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.PLANT_ONE_COLOR_MICROARRAY;

/**
 * Created by haideri on 17/10/2017.
 */
public class PlantOneColorMicroarrayUpdater extends BasicExperimentUpdater{

    private static final String DEFAULT_LABEL = "cy3";

    public PlantOneColorMicroarrayUpdater(ExperimentProfile exp) {
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
        if (settings.getExperimentType() != PLANT_ONE_COLOR_MICROARRAY) {
            return;
        }
        exp().setArrayDesign(settings.getArrayDesign());

        Collection<String> labels = exp().getLabelNames();
        String oldLabel = labels.isEmpty() ? null : labels.iterator().next();
        String newLabel = settings.getLabel();
        newLabel = isNullOrEmpty(newLabel) ? DEFAULT_LABEL : newLabel;
        exp().addOrReLabel(oldLabel, newLabel);
        super.updateSettings(settings);
    }
}
