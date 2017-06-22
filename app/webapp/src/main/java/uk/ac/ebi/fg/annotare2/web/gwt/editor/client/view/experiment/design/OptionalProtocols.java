package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

/**
 * Created by haideri on 17/05/2017.
 */
public enum OptionalProtocols {

    GROWTH_PROTOCOL("growth protocol"),
    TREATMENT_PROTOCOL("treatment protocol"),
    NORMALIZATION_DATA_TRANSFORMATION_PROTOCOL("normalization data transformation protocol"),
    CONVERSION_PROTOCOL("conversion protocol"),
    DISSECTION_PROTOCOL("dissection protocol"),
    HIGH_THROUGHPUT_SEQUENCE_ALIGNMENT_PROTOCOL("high throughput sequence alignment protocol");

    private final String name;

    OptionalProtocols(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}