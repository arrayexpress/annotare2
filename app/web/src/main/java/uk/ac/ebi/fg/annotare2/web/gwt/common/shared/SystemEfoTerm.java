/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

/**
 * @author Olga Melnichuk
 */
public enum SystemEfoTerm {

    PROTOCOL("Protocol", "protocol"),
    STUDY_DESIGN("Experiment Design", "study_design"),
    UNIT("Unit", "unit"),
    PUBLICATION_STATUS("Publication Status", "publication_status"),

    ORGANISM("Organism", "organism"),
    ORGANISM_PART("Organism Part", "organism_part"),
    STRAIN("Strain", "strain"),
    DISEASE("Disease", "disease"),
    GENOTYPE("Genotype", "genotype"),
    AGE("Age", "age"),
    CELL_LINE("Cell Line", "cell_line"),
    CELL_TYPE("Cell Type", "cell_type"),
    DEVELOPMENTAL_STAGE("Developmental Stage", "developmental_stage"),
    GENETIC_MODIFICATION("Genetic Modification", "genetic_modification"),
    ENVIRONMENTAL_HISTORY("Environmental History", "environmental_history"),
    INDIVIDUAL("Individual", "individual"),
    SEX("Sex", "sex"),
    SPECIMEN_WITH_KNOWN_STORAGE_STATE("Specimen With Known Storage State", "specimen_with_known_storage_state"),
    GROWTH_CONDITION("Growth Condition", "growth_condition"),
    DOSE("Dose", "dose"),
    IMMUNOPRECIPITATE("Immunoprecipitate", "immunoprecipitate"),
    CLINICAL_HISTORY("Clinical History", "clinical_history"),
    DISEASE_STAGING("Disease Staging", "disease_staging"),
    ECOTYPE("Ecotype", "ecotype"),
    IRRADIATE("Irradiate", "irradiate"),
    FRACTION("Fraction", "fraction"),
    KARYOTYPE("Karyotype", "karyotype"),
    PHENOTYPE("Phenotype", "phenotype"),
    TUMOR_GRADING("Tumor grading", "tumor_grading"),
    AE_EXPERIMENT_TYPE("AE Experiment Type", "ae_experiment_type"),
    ARRAY_ASSAY("Array Assay", "array_assay"),
    SEQUENCING_ASSAY("Sequencing Assay", "sequencing_assay");

    private final String name;
    private final String propertyName;

    private SystemEfoTerm(String name, String propertyName) {
        this.name = name;
        this.propertyName = propertyName;
    }

    public String getName() {
        return name;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
