package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.*;

/**
 * Created by haideri on 17/05/2017.
 */
public enum OptionalProtocols {

    GROWTH_PROTOCOL("growth protocol", PLANT_ONE_COLOR_MICROARRAY, PLANT_TWO_COLOR_MICROARRAY, PLANT_SEQUENCING,
                                        PLANT_METHYLATION_MICROARRAY, SINGLE_CELL_PLANT_SEQUENCING),
    TREATMENT_PROTOCOL("treatment protocol"),
    NORMALIZATION_DATA_TRANSFORMATION_PROTOCOL("normalization data transformation protocol"),
    CONVERSION_PROTOCOL("conversion protocol"),
    DISSECTION_PROTOCOL("dissection protocol"),
    HIGH_THROUGHPUT_SEQUENCE_ALIGNMENT_PROTOCOL("high throughput sequence alignment protocol");

    private final String name;

    private final List<ExperimentProfileType> experimentProfileTypes;

    OptionalProtocols(String name, ExperimentProfileType... experimentProfileTypes) {

        this.name = name;
        this.experimentProfileTypes = asList(experimentProfileTypes);
    }

    public String getName() {
        return name;
    }
    public List<ExperimentProfileType> getExperimentProfileTypes()
    {
        return new ArrayList<>(this.experimentProfileTypes);
    }
}