package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.io.*;

public class TableParserRunner {
    public String[][] parse(InputStream input, String encoding, boolean stripQuoting, boolean trimWhiteSpace)
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

    public String[][] parseSection(InputStream input, String encoding, boolean stripQuoting, boolean trimWhiteSpace,
                                   String sectionStartTag, String sectionEndTag)
            throws IOException, TableParserException {

        Reader reader = new BufferedReader(new InputStreamReader(input, encoding));
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
}
