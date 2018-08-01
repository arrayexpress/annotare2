package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import uk.ac.ebi.fg.annotare2.web.gwt.common.model.ExpProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.PlantOneColorMicroarrayExpProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.PlantSequencingExpProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.PlantTwoColorMicroarrayExpProfileType;

import java.util.*;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate.*;

/**
 * Created by haideri on 09/10/2017.
 */
public enum ExperimentProfileTypeToAttributesMapping {

    PLANT_SEQUENCING_EXPERIMENT(new PlantSequencingExpProfileType("Plant - High-throughput sequencing"), ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE),
    PLANT_ONE_COLOR_EXPERIMENT(new PlantOneColorMicroarrayExpProfileType("Plant - One-color microarray"), ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE),
    PLANT_TWO_COLOR_EXPERIMENT(new PlantTwoColorMicroarrayExpProfileType("Plant - Two-color microarray"), ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE);

    private final List<SampleAttributeTemplate> attributes;
    private ExpProfileType expProfileType;

    ExperimentProfileTypeToAttributesMapping(ExpProfileType expProfieType, SampleAttributeTemplate... attributes) {
        this.attributes = asList(attributes);
        this.expProfileType = expProfieType;

    }

    public List<SampleAttributeTemplate> getAttributes()
    {
        return new ArrayList<>(this.attributes);
    }

    public ExpProfileType getExpProfileType() {
        return expProfileType;
    }

}
