package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import uk.ac.ebi.fg.annotare2.web.gwt.common.model.ExpProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.PlantOneColorMicroarrayExpProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.PlantSequencingExpProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.PlantTwoColorMicroarrayExpProfileType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by haideri on 17/05/2017.
 */
public enum OptionalProtocols {

    GROWTH_PROTOCOL("growth protocol",new PlantOneColorMicroarrayExpProfileType("Plant - One-color microarray"),new PlantTwoColorMicroarrayExpProfileType("Plant - Two-color microarray"),new PlantSequencingExpProfileType("Plant - High-throughput sequencing")),
    TREATMENT_PROTOCOL("treatment protocol"),
    NORMALIZATION_DATA_TRANSFORMATION_PROTOCOL("normalization data transformation protocol"),
    CONVERSION_PROTOCOL("conversion protocol"),
    DISSECTION_PROTOCOL("dissection protocol"),
    HIGH_THROUGHPUT_SEQUENCE_ALIGNMENT_PROTOCOL("high throughput sequence alignment protocol");

    private final String name;

    private final List<ExpProfileType> experimentProfileTypes;

    OptionalProtocols(String name, ExpProfileType... experimentProfileTypes) {

        this.name = name;
        this.experimentProfileTypes = asList(experimentProfileTypes);
    }

    public String getName() {
        return name;
    }
    public List<ExpProfileType> getExperimentProfileTypes()
    {
        return new ArrayList<>(this.experimentProfileTypes);
    }
}