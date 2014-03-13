package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

public class TableParserException extends Exception {

    public final static String ERROR_NULL_READER =
            "Invalid argument passed: reader == null";
    public final static String ERROR_MARK_UNSUPPORTED =
            "Unable to maintain stream position: stream does not support mark() operataion";

    public TableParserException(String message) {
        super(message);
    }
}
