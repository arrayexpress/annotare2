package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign;

import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;

/**
 * @author Olga Melnichuk
 */
public enum ArrayDesignTab implements EditorTab {

    Header("ArrayDesign: Header"),

    Table("ArrayDesign: Table");

    private final String title;

    private ArrayDesignTab(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Object getId() {
        return this;
    }
}
