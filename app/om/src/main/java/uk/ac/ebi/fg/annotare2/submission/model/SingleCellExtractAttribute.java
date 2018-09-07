package uk.ac.ebi.fg.annotare2.submission.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public enum  SingleCellExtractAttribute {

    SINGLE_CELL_ISOLATION("Single Cell Isolation *",
            "Help Text",
            "",
            "FACS",
            "Fluidigm C1",
            "10x",
            "Drop-seq",
            "inDrop",
            "Cell Picking",
            "Mouse Pipette",
            "Laser-capture Microdissection",
            "other"),
    LIBRARY_CONSTRUCTION("Library Construction *",
            "Help Text",
            "",
            "10x V1",
            "10x V2",
            "CEL-seq",
            "CEL-seq2",
            "Drop-seq",
            "inDrop-seq",
            "MARS-seq",
            "SCRB-seq",
            "Smart-seq",
            "Smart-seq2",
            "STRT-seq",
            "other"),
    END_BIAS("End Bias *",
            "Help Text",
            "",
            "none",
            "3 prime tag",
            "3 prime end bias",
            "5 prime tag",
            "5 prime end bias"),
    PRIMER("Primer *",
            "Help Text",
            "",
            "oligo-dT",
            "random"),
    SPIKE_IN("Spike In *",
            "Help Text",
            "",
            "ERCC",
            "ERCC mix1",
            "ERCC mix2",
            "ERCC mix1 and mix2",
            "custom"),
    INPUT_MOLECULE("Input Molecule","Help Text"),
    SPIKE_IN_DILUTION("Spike in dilution","Help Text"),
    UMI_BARCODE_READ("UMI Barcode Read",
            "Help Text",
            "",
            "Read 1",
            "Read 2",
            "Index 1",
            "Index 2"),
    UMI_BARCODE_OFFSET("UMI Barcode Offset","Help Text"),
    UMI_BARCODE_SIZE("UMI Barcode Size","Help Text"),
    cDNA_READ("cDNA Read",
            "Help Text",
            "",
            "Read 1",
            "Read 2",
            "Index 1",
            "Index 2"),
    cDNA_READ_OFFSET("cDNA Read Offset","Help Text"),
    cDNA_READ_SIZE("cDNA Read Size","Help Text"),
    CELL_BARCODE_READ("Cell Barcode Read",
            "Help Text",
            "",
            "Read 1",
            "Read 2",
            "Index 1",
            "Index 2"),
    CELL_BARCODE_OFFSET("Cell Barcode Offset","Help Text"),
    CELL_BARCODE_SIZE("Cell Barcode Size","Help Text"),
    SAMPLE_BARCODE_READ("Sample Barcode Read",
            "Help Text",
            "",
            "Read 1",
            "Read 2",
            "Index 1",
            "Index 2"),
    SAMPLE_BARCODE_OFFSET("Sample Barcode Offset","Help Text"),
    SAMPLE_BARCODE_SIZE("Sample Barcode Size","Help Text");


    private final String title;
    private final List<String> values;
    private final List<String> options;
    private final String helpText;

    SingleCellExtractAttribute(String title, String helpText, String... options) {
        this.title = title;
        this.helpText = helpText;
        this.options = asList(options);
        this.values = new ArrayList<String>();
        for (String option : options) {
            int index = option.indexOf(" (");
            String value = index < 0 ? option : option.substring(0, index);
            this.values.add(value);
        }
    }

    public String getName() {
        return toString();
    }

    public String getTitle() {
        return title;
    }

    public String getHelpText() {
        return helpText;
    }


    public boolean hasOptions() {
        return !this.options.isEmpty();
    }

    public List<String> getOptions() {
        return new ArrayList<String>(options);
    }

    public String getValue(String option) {
        return values.get(options.indexOf(option));
    }

    public boolean hasValue(String value) {
        return values.contains(value);
    }

    public String getOption(String value) {
        return options.get(values.indexOf(value));
    }
}


