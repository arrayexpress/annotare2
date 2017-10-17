package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.*;

/**
 * Created by haideri on 09/10/2017.
 */
public enum ExperimentProfileTypeToAttributesMapping {

    PLANT_EXPERIMENT(of(ExperimentProfileType.PLANT_SEQUENCING), SampleAttributeTemplate.ORGANISM_ATTRIBUTE,SampleAttributeTemplate.CULTIVAR_ATTRIBUTE,SampleAttributeTemplate.AGE_ATTRIBUTE,SampleAttributeTemplate.DEVELOPMENTAL_STAGE_ORIGIN,SampleAttributeTemplate.GENOTYPE_ATTRIBUTE,SampleAttributeTemplate.ORGANISM_PART_ATTRIBUTE);

    private final List<SampleAttributeTemplate> attributes;
    private EnumSet<ExperimentProfileType> expProfileTypes;

    ExperimentProfileTypeToAttributesMapping(EnumSet<ExperimentProfileType> expProfieType, SampleAttributeTemplate... attributes) {
        this.attributes = asList(attributes);
        this.expProfileTypes = expProfieType;

    }

    public List<SampleAttributeTemplate> getAttributes()
    {
        return new ArrayList<>(this.attributes);
    }

    public Collection<ExperimentProfileType> getExpProfileTypes() {

        expProfileTypes.add(ExperimentProfileType.PLANT_ONE_COLOR_MICROARRAY);
        expProfileTypes.add(ExperimentProfileType.PLANT_TWO_COLOR_MICROARRAY);
        return unmodifiableSet(expProfileTypes);}

}
