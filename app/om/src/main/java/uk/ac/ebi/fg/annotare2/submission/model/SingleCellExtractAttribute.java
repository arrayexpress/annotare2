package uk.ac.ebi.fg.annotare2.submission.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public enum  SingleCellExtractAttribute {

    SINGLE_CELL_ISOLATION("Single Cell Isolation",
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
    LIBRARY_CONSTRUCTION("Library Construction",
            "Help Text",
            "",
            "10xV1",
            "10xV2",
            "CEL-seq",
            "CEL-seq2",
            "Drop-seq",
            "inDrop-seq",
            "MARS-seq",
            "SCRB-seq",
            "Smart-like (using SMARTer chemistry)",
            "Smart-seq",
            "Smart-seq2",
            "STRT-seq",
            "other"),
    END_BIAS("End Bias",
            "Help Text",
            "",
            "none (full length)",
            "3 prime tag",
            "3 prime end bias",
            "5 prime tag",
            "5 prime end bias"),
    PRIMER("Primer",
            "Help Text",
            "",
            "oligo-dT",
            "random",
            "other"),
    SPIKE_IN("Spike In",
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
            "The file that contains the UMI barcode read",
            "",
            "read 1",
            "read 2",
            "index 1",
            "index 2"),
    UMI_BARCODE_OFFSET("UMI Barcode Offset","Offset in sequence for UMI barcode read (in bp)"),
    UMI_BARCODE_SIZE("UMI Barcode Size","Length of UMI barcode read (in bp)"),
    cDNA_READ("cDNA Read",
            "The file that contains the cDNA read",
            "",
            "read 1",
            "read 2",
            "index 1",
            "index 2"),
    cDNA_READ_OFFSET("cDNA Read Offset","Offset in sequence for cDNA read (in bp)"),
    cDNA_READ_SIZE("cDNA Read Size","Length of cDNA read (in bp)"),
    CELL_BARCODE_READ("Cell Barcode Read",
            "The file that contains the cell barcode read",
            "",
            "read 1",
            "read 2",
            "index 1",
            "index 2"),
    CELL_BARCODE_OFFSET("Cell Barcode Offset","Offset in sequence for cell barcode read (in bp)"),
    CELL_BARCODE_SIZE("Cell Barcode Size","Length of cell barcode read (in bp)"),
    SAMPLE_BARCODE_READ("Sample Barcode Read",
            "The file that contains the sample barcode read",
            "",
            "read 1",
            "read 2",
            "index 1",
            "index 2"),
    SAMPLE_BARCODE_OFFSET("Sample Barcode Offset","Offset in sequence for sample barcode read (in bp)"),
    SAMPLE_BARCODE_SIZE("Sample Barcode Size","Length of sample barcode read (in bp)");


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


