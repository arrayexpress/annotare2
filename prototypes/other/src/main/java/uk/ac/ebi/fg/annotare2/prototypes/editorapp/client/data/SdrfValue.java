package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data;

import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfColumn;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfSection;

/**
 * @author Olga Melnichuk
 */
public class SdrfValue {

    private final String name;

    private final SdrfColumn column;

    private final SdrfSection section;

    public SdrfValue(String name, SdrfColumn column, SdrfSection section) {
        if (name == null || column == null || section == null) {
            throw new IllegalArgumentException("SdrfValue doesn't allow nulls");
        }
        this.name = name;
        this.column = column;
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public SdrfColumn getColumn() {
        return column;
    }

    public SdrfSection getSection() {
        return section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SdrfValue sdrfValue = (SdrfValue) o;

        if (!column.equals(sdrfValue.column)) return false;
        if (!name.equals(sdrfValue.name)) return false;
        if (section != sdrfValue.section) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + column.hashCode();
        result = 31 * result + section.hashCode();
        return result;
    }
}
