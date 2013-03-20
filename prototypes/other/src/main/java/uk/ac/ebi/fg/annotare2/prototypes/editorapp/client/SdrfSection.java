package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfColumn.Type.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfSection {

    public static enum Type {

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

        private Type(String title, SdrfColumn.Type... columnTypes) {
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

    private final Type type;
    private Set<SdrfColumn> columns = new LinkedHashSet<SdrfColumn>();
    private List<SdrfRow> rows = new ArrayList<SdrfRow>();

    public SdrfSection(Type type) {
        this.type = type;
    }

    public String getTitle() {
        return type.getTitle();
    }

    public Type getType() {
        return type;
    }

    public void setColumns(List<SdrfColumn> columns) {
        this.columns = new LinkedHashSet<SdrfColumn>(columns);
    }

    public List<SdrfColumn> getColumns() {
        return new ArrayList<SdrfColumn>(columns);
    }

    public List<SdrfRow> getRows() {
        return new ArrayList<SdrfRow>(rows);
    }

    public SdrfRow addRow(String name) {
        SdrfRow row = new SdrfRow(rows.size(), name);
        rows.add(row);
        return row;
    }
}
