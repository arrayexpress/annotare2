package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment;

import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;

/**
 * @author Olga Melnichuk
 */
public enum ExperimentTab implements EditorTab {

    EXP_DESCRIPTION("Experiment Description"),

    EXP_DESIGN("Sample and Data");

    private String title;

    private ExperimentTab(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isEqualTo(EditorTab other) {
        return equals(other);
    }
}
