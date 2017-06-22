package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

/**
 * Created by haideri on 17/05/2017.
 */
public enum MandatoryProtocols {

    SAMPLE_COLLECTION_PROTOCOL("sample collection protocol"),
    NUCLEIC_ACID_EXTRACTION_PROTOCOL("nucleic acid extraction protocol"),
    NUCLEIC_ACID_LABELING_PROTOCOL("nucleic acid labeling protocol"),
    NUCLEIC_ACID_HYBRIDIZATION_TO_ARRAY_PROTOCOL("nucleic acid hybridization to array protocol"),
    NUCLEIC_ACID_LIBRARY_CONSTRUCTION_PROTOCOL("nucleic acid library construction protocol"),
    ARRAY_SCANNING_AND_FEATURE_EXTRACTION_PROTOCOL("array scanning and feature extraction protocol"),
    NUCLEIC_ACID_SEQUENCING_PROTOCOL("nucleic acid sequencing protocol");

    private final String name;

    MandatoryProtocols(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
