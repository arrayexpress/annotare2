package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;


public enum ExperimentDesignToAttributesMapping {

    CASE_CONTROL_DESIGN("case control design","disease"),
    SPECIES_DESIGN("species design","organism"),
    CELL_TYPE_COMPARISON_DESIGN("cell type comparison design","cell type"),
    DEVELOPMENT_OR_DIFFERENTIATION_DESIGN("development or differentiation design","developmental stage"),
    DISEASE_STATE_DESIGN("disease state design","disease"),
    GENETIC_MODIFICATION_DESIGN("genetic modification design","genotype"),
    GENOTYPE_DESIGN("genotype design","genotype"),
    GROWTH_CONDITION_DESIGN("growth condition design","growth condition"),
    ORGANISM_PART_COMPARISON_DESIGN("organism part comparison design","organism part"),
    SEX_DESIGN("sex design","sex"),
    STRAIN_OR_LINE_DESIGN("strain or line design","strain"),
    CLINICAL_HISTORY_DESIGN("clinical history design","clinical history"),
    INJURY_DESIGN("injury design","injury"),
    PATHOGENICITY_DESIGN("pathogenicity design","infect"),
    STIMULUS_OR_STRESS_DESIGN("stimulus or stress design","stimulus");

    private String experimentDesign;
    private String attribute;

    ExperimentDesignToAttributesMapping(String experimentDesign, String attribute)
    {
        this.experimentDesign = experimentDesign;
        this.attribute = attribute;
    }

    public boolean isOkay(String experimentDesign)
    {
        return this.experimentDesign.equalsIgnoreCase(experimentDesign);
    }
}
