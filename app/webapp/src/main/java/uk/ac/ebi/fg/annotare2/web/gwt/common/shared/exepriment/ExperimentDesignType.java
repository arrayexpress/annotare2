package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by haideri on 19/06/2017.
 */
public enum ExperimentDesignType {
    BINDING_SITE_IDENTIFICATION_DESIGN("binding site identification design","EFO:0004664",SampleAttributeTemplate.IMMUNOPRECIPITATE_ORIGIN),
    CASE_CONTROL_DESIGN("case control design","EFO:0001427",SampleAttributeTemplate.DESEASE_ATTRIBUTE),
    CELL_TYPE_COMPARISON_DESIGN("cell type comparison design","EFO:0001745", SampleAttributeTemplate.CELL_TYPE_ORIGIN),
    CLINICAL_HISTORY_DESIGN("clinical history design","EFO:0000352",SampleAttributeTemplate.CLINICAL_HISTORY_ATTRIBUTE),
    COMPOUND_TREATMENT_DESIGN("compound treatment design","EFO:0001755",SampleAttributeTemplate.COMPOUND_ATTRIBUTE,SampleAttributeTemplate.DOSE_ORIGIN),
    DEVELOPMENT_DIFFERENTIAL_DESIGN("development or differentiation design","EFO:0001746",SampleAttributeTemplate.DEVELOPMENTAL_STAGE_ORIGIN),
    DISEASE_STATE_DESIGN("disease state design","EFO:0001756",SampleAttributeTemplate.DESEASE_ATTRIBUTE),
    DOSE_RESPONSE_DESIGN("dose response design","EFO:0001757",SampleAttributeTemplate.COMPOUND_ATTRIBUTE,SampleAttributeTemplate.DOSE_ORIGIN),
    GENETIC_MODIFICATION_DESIGN("genetic modification design","EFO:0001758",SampleAttributeTemplate.GENOTYPE_ATTRIBUTE),
    GENOTYPE_DESIGN("genotype design","EFO:0001748",SampleAttributeTemplate.GENOTYPE_ATTRIBUTE),
    GROWTH_CONDITION_DESIGN("growth condition design","EFO:0001759",SampleAttributeTemplate.GROWTH_CONDITION_ORIGIN),
    INJURY_DESIGN("injury design","EFO:0001760",SampleAttributeTemplate.INJURY_ATTRIBUTE),
    ORGANISM_PART_COMPARISON_DESIGN("organism part comparison design","EFO:0001750",SampleAttributeTemplate.ORGANISM_PART_ATTRIBUTE),
    SEX_DESIGN("sex design","EFO:0001752",SampleAttributeTemplate.SEX_ORIGIN),
    SPECIES_DESIGN("species design","EFO:0001753",SampleAttributeTemplate.ORGANISM_ATTRIBUTE),
    STIMULUS_STRESS_DESIGN("stimulus or stress design","EFO:0001762",SampleAttributeTemplate.STIMULUS_ATTRIBUTE),
    STAIN_LINE_DESIGN("strain or line design","EFO:0001754",SampleAttributeTemplate.STRAIN_ATTRIBUTE),
    TIME_SERIES_DESIGN("time series design","EFO:0001779",SampleAttributeTemplate.TIME_ATTRIBUTE);

    private final String label;
    private final String accession;
    private final List<SampleAttributeTemplate> attributes;

    ExperimentDesignType(String label, String accession, SampleAttributeTemplate... attributes) {
        this.label = label;
        this.accession = accession;
        this.attributes = asList(attributes);
    }

    public String getLabel() {
        return label;
    }

    public String getAccession() {
        return accession;
    }

    public List<SampleAttributeTemplate> getAttributes()
    {
        return new ArrayList<>(this.attributes);
    }
}
