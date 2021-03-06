package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;

import java.util.*;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.*;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate.*;

/**
 * Created by haideri on 09/10/2017.
 */
public enum ExperimentProfileTypeToAttributesMapping {

    PLANT_ONE_COLOR_EXPERIMENT                  (PLANT_ONE_COLOR_MICROARRAY, ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE),
    PLANT_TWO_COLOR_EXPERIMENT                  (PLANT_TWO_COLOR_MICROARRAY, ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE),
    PLANT_METHYLATION_MICROARRAY_EXPERIMENT     (PLANT_METHYLATION_MICROARRAY, ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE),
    PLANT_SEQUENCING_EXPERIMENT                 (PLANT_SEQUENCING, ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE),
    PLANT_SINGLE_CELL_SEQUENCING_EXPERIMENT     (SINGLE_CELL_PLANT_SEQUENCING, ORGANISM_ATTRIBUTE,CULTIVAR_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,CELL_TYPE_ORIGIN),
    HUMAN_ONE_COLOR_EXPERIMENT                  (HUMAN_ONE_COLOR_MICROARRAY,ORGANISM_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,DESEASE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    HUMAN_TWO_COLOR_EXPERIMENT                  (HUMAN_TWO_COLOR_MICROARRAY,ORGANISM_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,DESEASE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    HUMAN_METHYLATION_MICROARRAY_EXPERIMENT     (HUMAN_METHYLATION_MICROARRAY, ORGANISM_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,DESEASE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    HUMAN_SEQUENCING_EXPERIMENT                 (HUMAN_SEQUENCING,ORGANISM_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,DESEASE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    HUMAN_SINGLE_CELL_EXPERIMENT                (SINGLE_CELL_HUMAN_SEQUENCING,ORGANISM_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,DESEASE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN,CELL_TYPE_ORIGIN),
    ANIMAL_ONE_COLOR_EXPERIMENT                 (ANIMAL_ONE_COLOR_MICROARRAY,ORGANISM_ATTRIBUTE,STRAIN_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    ANIMAL_TWO_COLOR_EXPERIMENT                 (ANIMAL_TWO_COLOR_MICROARRAY,ORGANISM_ATTRIBUTE,STRAIN_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    ANIMAL_METHYLATION_MICROARRAY_EXPERIMENT    (ANIMAL_METHYLATION_MICROARRAY, ORGANISM_ATTRIBUTE,STRAIN_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    ANIMAL_SEQUENCING_EXPERIMENT                (ANIMAL_SEQUENCING,ORGANISM_ATTRIBUTE,STRAIN_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN),
    ANIMAL_SINGLE_CELL_EXPERIMENT               (SINGLE_CELL_ANIMAL_SEQUENCING,ORGANISM_ATTRIBUTE,STRAIN_ATTRIBUTE,AGE_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,SEX_ORIGIN,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,INDIVIDUAL_ORIGIN,CELL_TYPE_ORIGIN),
    CELL_LINE_ONE_COLOR_EXPERIMENT              (CELL_LINE_ONE_COLOR_MICROARRAY,ORGANISM_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,DESEASE_ATTRIBUTE,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,CELL_TYPE_ORIGIN,CELL_LINE_ORIGIN),
    CELL_LINE_TWO_COLOR_EXPERIMENT              (CELL_LINE_TWO_COLOR_MICROARRAY,ORGANISM_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,DESEASE_ATTRIBUTE,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,CELL_TYPE_ORIGIN,CELL_LINE_ORIGIN),
    CELL_LINE_METHYLATION_MICROARRAY_EXPERIMENT (CELL_LINE_METHYLATION_MICROARRAY, ORGANISM_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,DESEASE_ATTRIBUTE,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,CELL_TYPE_ORIGIN,CELL_LINE_ORIGIN),
    CELL_LINE_SEQUENCING_EXPERIMENT             (CELL_LINE_SEQUENCING,ORGANISM_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,DESEASE_ATTRIBUTE,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,CELL_TYPE_ORIGIN,CELL_LINE_ORIGIN),
    CELL_LINE_SINGLE_CELL_EXPERIMENT            (SINGLE_CELL_CELL_LINE_SEQUENCING, ORGANISM_ATTRIBUTE,DEVELOPMENTAL_STAGE_ORIGIN,DESEASE_ATTRIBUTE,GENOTYPE_ATTRIBUTE,ORGANISM_PART_ATTRIBUTE,CELL_TYPE_ORIGIN,CELL_LINE_ORIGIN),
    SINGLE_CELL_EXPERIMENT                      (SINGLE_CELL_SEQUENCING, SINGLE_CELL_WELL_QUALITY_ATTRIBUTE, POST_ANALYSIS_WELL_QUALITY_ATTRIBUTE, INFERRED_CELL_TYPE, SINGLE_CELL_IDENTIFIER,CELL_TYPE_ORIGIN); //Template has been changed for this type to include these attribute as optional not mandatory

    private final List<SampleAttributeTemplate> attributes;
    private ExperimentProfileType expProfileType;

    ExperimentProfileTypeToAttributesMapping(ExperimentProfileType expProfieType, SampleAttributeTemplate... attributes) {
        this.attributes = asList(attributes);
        this.expProfileType = expProfieType;

    }

    public List<SampleAttributeTemplate> getAttributes()
    {
        return new ArrayList<>(this.attributes);
    }

    public List<SampleAttributeTemplate> getSingleCellAttributes(){
        return new ArrayList<>(SINGLE_CELL_EXPERIMENT.attributes);
    }

    public boolean isSingleCellExperiment(ExperimentProfileType experimentProfileType){

        return PLANT_SINGLE_CELL_SEQUENCING_EXPERIMENT.expProfileType == experimentProfileType ||
                HUMAN_SINGLE_CELL_EXPERIMENT.expProfileType == experimentProfileType ||
                ANIMAL_SINGLE_CELL_EXPERIMENT.expProfileType == experimentProfileType ||
                CELL_LINE_SINGLE_CELL_EXPERIMENT.expProfileType == experimentProfileType ||
                SINGLE_CELL_SEQUENCING == experimentProfileType;
    }

    public ExperimentProfileType getExpProfileType() {

        return expProfileType;
    }

}
