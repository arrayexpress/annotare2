package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfColumn.Type.*;

/**
 * @author Olga Melnichuk
 */
public enum SdrfSection {
    SOURCES("Sources",
            MATERIAL_TYPE,
            PROVIDER,
            CHARCTERISTIC,
            COMMENT),
    SAMPLES("Samples",
            MATERIAL_TYPE,
            CHARCTERISTIC,
            COMMENT),
    EXTRACTS("Extracts",
            MATERIAL_TYPE,
            CHARCTERISTIC,
            COMMENT),
    LABELED_EXTRACTS("Labeled Extracts",
            LABEL,
            MATERIAL_TYPE,
            CHARCTERISTIC,
            COMMENT),
    ASSAYS("Assays",
            TECHNOLOGY_TYPE,
            FACTOR_VALUE,
            ARRAY_DESIGN,
            COMMENT),
    SCANS("Scans",
            FACTOR_VALUE,
            COMMENT),
    ARRAY_DATA_FILES("Array Data Files",
            COMMENT),
    NORMALIZATIONS("Normalizations",
            COMMENT),
    DERIVED_ARRAY_DATA_FILES("Derived Array Data Files",
            COMMENT);

    private final String title;
    private final List<SdrfColumn.Type> columnTypes = new ArrayList<SdrfColumn.Type>();

    private SdrfSection(String title, SdrfColumn.Type... columnTypes) {
        this.title = title;
        this.columnTypes.addAll(asList(columnTypes));
    }

    public String getTitle() {
        return title;
    }

    public List<SdrfColumn.Type> getColumnTypes(boolean isFirst) {
        List<SdrfColumn.Type> copy = new ArrayList<SdrfColumn.Type>();
        copy.addAll(columnTypes);
        if (!isFirst) {
            copy.add(PROTOCOL);
        }
        return copy;
    }
}
