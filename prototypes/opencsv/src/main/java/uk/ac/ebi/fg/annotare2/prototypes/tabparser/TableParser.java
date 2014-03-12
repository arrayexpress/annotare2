package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.io.*;
import java.util.*;
import static uk.ac.ebi.fg.annotare2.prototypes.tabparser.TableParserException.*;

public class TableParser {

    public enum Option {
        STRIP_QUOTING,
        STRIP_ESCAPING,
        TRIM_CELL_WHITESPACE,
        TRIM_EMPTY_ROWS,
        THROW_PARSER_ERRORS
    }

    private final static char TAB       = '\t';
    private final static char CR        = '\r';
    private final static char LF        = '\n';
    private final static char ESCAPE    = '\\';
    private final static char QUOTE     = '\"';
    private final static char COMMENT   = '#';

    private final static int BUFFER_SIZE = 10000;

    private final Set<Option> options;
    private final char[] buffer;
    private final List<String[]> rows;
    private final List<String> row;
    private final StringBuilder column;

    private State state;

    public TableParser(Option... options) {
        this.options = new HashSet<Option>(Arrays.asList(options));
        this.buffer = new char[BUFFER_SIZE];
        this.rows = new ArrayList<String[]>();
        this.row = new ArrayList<String>();
        this.column = new StringBuilder();
        this.state = State.FIRST_CHAR;
    }

    private String bufferedRead(Reader input) throws IOException {
        int charsRead = input.read(buffer, 0, BUFFER_SIZE);
        if (-1 != charsRead) {
            return new String(buffer, 0, charsRead);
        } else {
            return null;
        }
    }

    public String[][] parse(Reader reader) throws IOException, TableParserException {

        if (null == reader) {
            throw new TableParserException(ERROR_NULL_READER);
        }
        while (reader.ready()) {
            processLine(bufferedRead(reader));
        }

        if (state.isOneOf(
                State.QUOTED_CHAR,
                State.ESCAPED_CHAR,
                State.ESCAPED_CHAR_INSIDE_QUOTE)) {
            throwInvalidStateError();
        }
        actionEndOfRow();
        return rows.toArray(new String[rows.size()][]);
    }

    private void processLine(String line) throws TableParserException {
        char c;
        for (int pos = 0; pos < line.length(); ++pos) {
            c = line.charAt(pos);
            switch (c) {
                case TAB:
                    if (state.isOneOf(
                            State.FIRST_CHAR,
                            State.AFTER_CARRIAGE_RETURN,
                            State.REGULAR_CHAR)) {
                        actionEndOfColumn();
                        state = State.REGULAR_CHAR;
                    } else if (state.isOneOf(State.QUOTED_CHAR, State.COMMENT_CHAR)) {
                        column.append(c);
                    } else if (State.ESCAPED_CHAR == state) {
                        column.append(c);
                        state = State.REGULAR_CHAR;
                    } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                        column.append(c);
                        state = State.QUOTED_CHAR;
                    } else {
                        throwInvalidStateError();
                    }
                    break;
                case CR:
                    if (state.isOneOf(
                            State.FIRST_CHAR,
                            State.AFTER_CARRIAGE_RETURN,
                            State.REGULAR_CHAR,
                            State.COMMENT_CHAR)) {
                        actionEndOfRow();
                        state = State.AFTER_CARRIAGE_RETURN;
                    } else if (state.isOneOf(State.QUOTED_CHAR)) {
                        column.append(c);
                    } else if (State.ESCAPED_CHAR == state) {
                        column.append(ESCAPE);
                        actionEndOfRow();
                        state = State.AFTER_CARRIAGE_RETURN;
                    } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                        column.append(ESCAPE).append(c);
                        state = State.QUOTED_CHAR;
                    } else {
                        throwInvalidStateError();
                    }
                    break;
                case LF:
                    if (state.isOneOf(
                            State.FIRST_CHAR,
                            State.REGULAR_CHAR,
                            State.COMMENT_CHAR)) {
                        actionEndOfRow();
                        state = State.FIRST_CHAR;
                    } else if (State.QUOTED_CHAR == state) {
                        column.append(c);
                    } else if (State.ESCAPED_CHAR == state) {
                        column.append(ESCAPE);
                        actionEndOfRow();
                        state = State.FIRST_CHAR;
                    } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                        column.append(ESCAPE).append(c);
                        state = State.QUOTED_CHAR;
                    } else if (State.AFTER_CARRIAGE_RETURN == state) {
                        state = State.FIRST_CHAR;
                    } else {
                        throwInvalidStateError();
                    }
                    break;
                case ESCAPE:
                    if (state.isOneOf(
                            State.FIRST_CHAR,
                            State.AFTER_CARRIAGE_RETURN,
                            State.REGULAR_CHAR)) {
                        if (!options.contains(Option.STRIP_ESCAPING)) {
                            column.append(c);
                        }
                        state = State.ESCAPED_CHAR;
                    } else if (State.QUOTED_CHAR == state) {
                        if (!options.contains(Option.STRIP_ESCAPING)) {
                            column.append(c);
                        }
                        state = State.ESCAPED_CHAR_INSIDE_QUOTE;
                    } else if (State.ESCAPED_CHAR == state) {
                        column.append(c);
                        state = State.REGULAR_CHAR;
                    } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                        column.append(c);
                        state = State.QUOTED_CHAR;
                    } else if (State.COMMENT_CHAR == state) {
                        column.append(c);
                    } else {
                        throwInvalidStateError();
                    }
                    break;
                case QUOTE:
                    if (state.isOneOf(
                            State.FIRST_CHAR,
                            State.AFTER_CARRIAGE_RETURN,
                            State.REGULAR_CHAR)) {
                        if (!options.contains(Option.STRIP_QUOTING)) {
                            column.append(c);
                        }
                        state = State.QUOTED_CHAR;
                    } else if (State.QUOTED_CHAR == state) {
                        if (!options.contains(Option.STRIP_QUOTING)) {
                            column.append(c);
                        }
                        state = State.REGULAR_CHAR;
                    } else if (State.ESCAPED_CHAR == state) {
                        column.append(c);
                        state = State.REGULAR_CHAR;
                    } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                        column.append(c);
                        state = State.QUOTED_CHAR;
                    } else if (State.COMMENT_CHAR == state) {
                        column.append(c);
                    } else {
                        throwInvalidStateError();
                    }
                    break;
                case COMMENT:
                    if (state.isOneOf(State.FIRST_CHAR, State.AFTER_CARRIAGE_RETURN)) {
                        column.append(c);
                        state = State.COMMENT_CHAR;
                        break;
                    }
                default:
                    if (state.isOneOf(
                            State.FIRST_CHAR,
                            State.AFTER_CARRIAGE_RETURN,
                            State.REGULAR_CHAR,
                            State.ESCAPED_CHAR)) {
                        column.append(c);
                        state = State.REGULAR_CHAR;
                    } else if (state.isOneOf(State.QUOTED_CHAR, State.COMMENT_CHAR)) {
                        column.append(c);
                    } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                        column.append(c);
                        state = State.QUOTED_CHAR;
                    } else {
                        throwInvalidStateError();
                    }
                    break;
            }
        }
    }

    private void actionEndOfColumn() {
        String col = column.toString();
        row.add(options.contains(Option.TRIM_CELL_WHITESPACE) ? col.trim() : col);
        column.setLength(0);
    }

    private void actionEndOfRow() {
        actionEndOfColumn();
        if (!options.contains(Option.TRIM_EMPTY_ROWS) || !isRowEmpty()) {
            rows.add(row.toArray(new String[row.size()]));
        }
        row.clear();
    }

    private boolean isRowEmpty() {
        for (String col : row) {
            if (!col.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void throwInvalidStateError() throws TableParserException {
        if (options.contains(Option.THROW_PARSER_ERRORS)) {
            throw new TableParserException("Invalid parser state: " + state.name());
        }
    }

    enum State {
        FIRST_CHAR,
        REGULAR_CHAR,
        QUOTED_CHAR,
        ESCAPED_CHAR,
        ESCAPED_CHAR_INSIDE_QUOTE,
        COMMENT_CHAR,
        AFTER_CARRIAGE_RETURN;

        public boolean isOneOf(State... states) {
            for (State s : states) {
                if (this.equals(s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
