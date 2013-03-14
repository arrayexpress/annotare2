package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

/**
 * @author Olga Melnichuk
 */
public enum SdrfSection {
    SOURCES("Sources"),
    SAMPLES("Samples"),
    EXTRACTS("Extracts"),
    LABELED_EXTRACTS("Labeled Extracts"),
    ASSAYS("Assays"),
    SCANS("Scans"),
    ARRAY_DATA_FILES("Array Data Files"),
    NORMALIZATIONS("Normalizations"),
    DERIVED_ARRAY_DATA_FILES("Derived Array Data Files");

    private final String title;

    private SdrfSection(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
