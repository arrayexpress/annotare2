package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.io.*;
import java.util.*;

public class TableParser {

    enum Option {
        STRIP_QUOTING,
        STRIP_ESCAPING,
        TRIM_WHITESPACE
    }

    private final static int BUFFER_SIZE = 10000;
    private final char[] buffer = new char[BUFFER_SIZE];

    public String[][] parse(InputStream input, String encoding, boolean stripQuoting, boolean trimWhiteSpace)
            throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
        RowAssembler rowAssembler = new RowAssembler(
                Option.STRIP_ESCAPING,
                stripQuoting ? Option.STRIP_QUOTING : null,
                trimWhiteSpace ? Option.TRIM_WHITESPACE : null
        );
        List<String[]> parsed = new ArrayList<String[]>();

        while (reader.ready()) {
            String line = bufferedRead(reader);
            boolean isEOF = null == line;
            // we have a line, feed it to the assembler
            if (!isEOF) {
                rowAssembler.processLine(line);
            }
            // get all we parsed so far
            // (including possibly incomplete last row)
            if (rowAssembler.isRowComplete() || isEOF) {
                parsed.add(rowAssembler.getRow(isEOF));
            }
            // no more data from the input, exit the loop
            if (isEOF) {
                break;
            }
        }

        return parsed.toArray(new String[parsed.size()][]);
    }

    private String bufferedRead(Reader input) throws IOException {
        int charsRead = input.read(buffer, 0, BUFFER_SIZE);
        if (-1 != charsRead) {
            return new String(buffer, 0, charsRead);
        } else {
            return null;
        }
    }

    static class RowAssembler {

        private final static char TAB       = '\t';
        private final static char CR        = '\r';
        private final static char NL        = '\n';
        private final static char ESCAPE    = '\\';
        private final static char QUOTE     = '\"';

        private final Set<Option> options;

        private boolean isRowComplete;
        private List<String> assembled;
        private StringBuilder column;
        private State state;

        public RowAssembler(Option... options) {
            this.options =  new HashSet<Option>(Arrays.asList(options));
            this.isRowComplete = false;
            this.assembled = new ArrayList<String>();
            this.column = new StringBuilder();
            this.state = State.NORMAL_COLUMN;
        }

        public boolean isRowComplete() {
            return this.isRowComplete;
        }

        public String[] getRow(boolean shouldIncludeIncomplete) {
            if (shouldIncludeIncomplete) {
                assembled.add(column.toString());
            }
            return assembled.toArray(new String[assembled.size()]);
        }

        public void processLine(String line) {
            char c;
            for (int pos = 0; pos < line.length(); ++pos) {
                c = line.charAt(pos);
                switch (c) {
                    default:
                        column.append(c);
                        break;
                }
            }
        }

        enum State {
            NORMAL_COLUMN
        };
    }
}
