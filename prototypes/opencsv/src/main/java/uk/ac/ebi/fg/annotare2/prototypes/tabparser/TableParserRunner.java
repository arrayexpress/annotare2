package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.io.IOException;
import java.io.InputStream;

public class TableParserRunner {
    public static String[][] parse(InputStream input, String encoding, boolean stripQuoting, boolean trimWhiteSpace)
            throws IOException, TableParserException {

        TableParser parser = new TableParser(
                TableParser.Option.STRIP_ESCAPING,
                TableParser.Option.TRIM_EMPTY_TRAILING_COLUMNS,
                TableParser.Option.TRIM_EMPTY_TRAILING_ROWS,
                TableParser.Option.THROW_PARSER_ERRORS,
                stripQuoting ? TableParser.Option.STRIP_QUOTING : null,
                trimWhiteSpace ? TableParser.Option.TRIM_COLUMN_WHITESPACE : null
        );
        return parser.parse(input, encoding);
    }

    public static String[][] parseSection(InputStream input, String encoding, boolean stripQuoting, boolean trimWhiteSpace,
                                          String sectionStartTag, String sectionEndTag)
            throws IOException, TableParserException {

        TableParser parser = new TableParser(
                new TableSectionDataHandler(sectionStartTag, sectionEndTag),
                TableParser.Option.STRIP_ESCAPING,
                TableParser.Option.TRIM_EMPTY_TRAILING_COLUMNS,
                TableParser.Option.TRIM_EMPTY_TRAILING_ROWS,
                TableParser.Option.MAINTAIN_STREAM_POSITION,
                TableParser.Option.THROW_PARSER_ERRORS,
                stripQuoting ? TableParser.Option.STRIP_QUOTING : null,
                trimWhiteSpace ? TableParser.Option.TRIM_COLUMN_WHITESPACE : null
        );
        return parser.parse(input, encoding);
    }

    public static boolean hasSectionDetected(InputStream input, String encoding, String... sections)
            throws IOException, TableParserException {

        if (!input.markSupported()) {
            return false;
        }

        TableSectionDetectingDataHandler handler = new TableSectionDetectingDataHandler(sections);
        TableParser parser = new TableParser(
                handler,
                TableParser.Option.STRIP_ESCAPING,
                TableParser.Option.STRIP_QUOTING,
                TableParser.Option.TRIM_COLUMN_WHITESPACE,
                TableParser.Option.TRIM_EMPTY_TRAILING_COLUMNS,
                TableParser.Option.TRIM_EMPTY_TRAILING_ROWS,
                TableParser.Option.THROW_PARSER_ERRORS
        );

        input.mark(Integer.MAX_VALUE);
        parser.parse(input, encoding);
        input.reset();

        return handler.hasSectionDetected();
    }
}
