package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment;

import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;

/**
 * @author Olga Melnichuk
 */
public enum ExperimentTab implements EditorTab {

    IDF("Investigation Design"),

    SDRF("Sample and Data Relationship");

    private String title;

    private ExperimentTab(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Object getId() {
        return this;
    }
}
