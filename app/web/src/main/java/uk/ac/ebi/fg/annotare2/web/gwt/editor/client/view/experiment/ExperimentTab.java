package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment;

import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;

/**
 * @author Olga Melnichuk
 */
public enum ExperimentTab implements EditorTab {

    EXP_INFO("Experiment Description"),

    EXP_DESIGN("Sample and Data"),

    IDF_PREVIEW("IDF Preview"),

    SDRF_PREVIEW("SDRF Preview");

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
