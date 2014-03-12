package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.io.*;

public class TableParserRunner {
    public String[][] parse(InputStream input, String encoding, boolean stripQuoting, boolean trimWhiteSpace)
            throws IOException, TableParserException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
        TableParser parser = new TableParser(
                TableParser.Option.STRIP_ESCAPING,
                TableParser.Option.TRIM_EMPTY_ROWS,
                TableParser.Option.THROWS_PARSER_ERRORS,
                stripQuoting ? TableParser.Option.STRIP_QUOTING : null,
                trimWhiteSpace ? TableParser.Option.TRIM_CELL_WHITESPACE : null
        );
        return parser.parse(reader);
    }
}
