package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

/**
 * @author Olga Melnichuk
 */
public class SdrfColumn {

    public static enum Type {
        CHARCTERISTIC("Characteristic [.]"),
        FACTOR_VALUE("Factor Value [.]"),
        ARRAY_DESIGN("Array Design"),
        LABEL("Label"),
        MATERIAL_TYPE("Material Type"),
        PROVIDER("Provider"),
        TECHNOLOGY_TYPE("Technology Type"),
        COMMENT("Comment [.]"),
        PROTOCOL("Protocol");

        private final String title;

        private Type(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }


}