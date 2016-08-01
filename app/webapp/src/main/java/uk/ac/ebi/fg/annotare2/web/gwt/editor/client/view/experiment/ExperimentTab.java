package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment;

import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;

/**
 * @author Olga Melnichuk
 */
public enum ExperimentTab implements EditorTab {

    EXP_INFO("Experiment Description"),

    EXP_DESIGN("Samples and Data"),

    IDF_PREVIEW("Experiment Description Preview"),

    SDRF_PREVIEW("Sample and Data Preview");

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
