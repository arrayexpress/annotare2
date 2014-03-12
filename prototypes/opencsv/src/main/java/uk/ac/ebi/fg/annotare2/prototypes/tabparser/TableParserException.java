package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

public class TableParserException extends Exception {

    public final static String ERROR_NULL_READER = "Invalid argument passed: reader == null";

    public TableParserException(String message) {
        super(message);
    }
}
