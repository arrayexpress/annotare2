package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data;

import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfColumn;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfSection;

/**
 * @author Olga Melnichuk
 */
public class MaterialTypeValue extends SdrfValue {

    private final String value;
    private final ValueSource source;

    public MaterialTypeValue(String name, SdrfColumn column, SdrfSection section, String value, ValueSource source) {
        super(name, column, section);
        if (value == null || source == null) {
            throw new IllegalArgumentException("MaterialTypeValue doesn't allow nulls");
        }
        this.value = value;
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public ValueSource getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MaterialTypeValue that = (MaterialTypeValue) o;

        if (!source.equals(that.source)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + source.hashCode();
        return result;
    }
}
