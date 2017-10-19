package uk.ac.ebi.fg.annotare2.web.server.rpc.updates;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.Extract;
import uk.ac.ebi.fg.annotare2.submission.model.Sample;

/**
 * Created by haideri on 17/10/2017.
 */
public class PlantSequencingUpdater extends BasicExperimentUpdater {

    public PlantSequencingUpdater(ExperimentProfile exp) {
        super(exp);
    }

    @Override
    protected Sample createSample(String name) {
        Sample sample  = super.createSample(name);

        Extract extract = exp().createExtract(sample);
        extract.setName(sample.getName());

        exp().createLabeledExtract(extract, null);
        return sample;
    }
}
