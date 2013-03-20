package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

/**
 * @author Olga Melnichuk
 */
public class SdrfColumn {

    public static enum Type {
        CHARCTERISTIC("Characteristic", true),
        FACTOR_VALUE("Factor Value", true),
        ARRAY_DESIGN("Array Design"),
        LABEL("Label"),
        MATERIAL_TYPE("Material Type"),
        PROVIDER("Provider"),
        TECHNOLOGY_TYPE("Technology Type"),
        COMMENT("Comment", true),
        PROTOCOL("Protocol");

        private final String title;
        private final boolean requiresKey;

        private Type(String title) {
            this(title, false);
        }

        private Type(String title, boolean requiresKey) {
            this.title = title;
            this.requiresKey = requiresKey;
        }

        public String getTitle() {
            return getTitle(".");
        }

        public String getTitle(String key) {
            return title + (requiresKey ? " [" + key + "]" : "");
        }

        public boolean requiresKey() {
            return requiresKey;
        }
    }

    private final Type type;

    private String key;

    public SdrfColumn(Type type, String key) {
        if (type == null) {
            throw new IllegalArgumentException("column type == null");
        }
        this.type = type;
        setKey(key);
    }

    public Type getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (type.requiresKey()) {
            this.key = key;
        }
    }

    public String getTitle() {
        return type.getTitle(key);
    }

    public SdrfCellValueEditor createEditor(String name) {
        switch (type) {
            case  MATERIAL_TYPE: return new MaterialTypeValueEditor(name);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SdrfColumn that = (SdrfColumn) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}