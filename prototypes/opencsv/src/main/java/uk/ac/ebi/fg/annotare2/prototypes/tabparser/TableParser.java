package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Finite state machine based parser for Tab-delimited files
 *
 * Main features:
 *   - accepts all common line separators (CR, LF, CR+LF)
 *   - stripping of quote and escape characters, trimming of column whitespace
 *     and removal of empty trailing columns and rows are configurable options
 *   - warning: NOT THREAD SAFE!
 *
 * @author Nikolay Kolesnikov
 * @date 12-Mar-2014
 */

public class TableParser {

    public enum Option {
        STRIP_QUOTING,
        STRIP_ESCAPING,
        TRIM_COLUMN_WHITESPACE,
        TRIM_EMPTY_TRAILING_COLUMNS,
        TRIM_EMPTY_TRAILING_ROWS,
        MAINTAIN_STREAM_POSITION,
        THROW_PARSER_ERRORS
    }

    private final static char TAB       = '\t';
    private final static char CR        = '\r';
    private final static char LF        = '\n';
    private final static char ESCAPE    = '\\';
    private final static char QUOTE     = '\"';
    private final static char COMMENT   = '#';

    private final Set<Option> options;
    private final StringBuilder column;
    private final ITableDataHandler dataHandler;

    private State state;

    public TableParser(ITableDataHandler dataHandler, Option... options) {
        this.options = new HashSet<Option>(Arrays.asList(options));
        this.column = new StringBuilder();
        this.dataHandler = dataHandler;

        this.dataHandler.setOptions(this.options);
    }

    public TableParser(Option... options) {
        this(new TableDataHandler(), options);
    }

    public String[][] parse(InputStream input, String encoding) throws IOException, TableParserException {
        if (null == input) {
            throw new TableParserException(TableParserException.ERROR_NULL_READER);
        } if (options.contains(Option.MAINTAIN_STREAM_POSITION) && !input.markSupported()) {
            throw new TableParserException(TableParserException.ERROR_MARK_UNSUPPORTED);
        }
        actionStartOfTable();
        if (options.contains(Option.MAINTAIN_STREAM_POSITION)) {
            input.mark(Integer.MAX_VALUE);
        }
        Reader reader = new BufferedReader(new InputStreamReader(input, encoding));
        int intChar;
        int position = 0;
        while (dataHandler.needsData() && (intChar = reader.read()) != -1) {
            processChar((char)intChar);
            position++;
        }
        if (options.contains(Option.MAINTAIN_STREAM_POSITION)) {
            input.reset();
            input.skip(position);
        }
        actionEndOfTable();

        return dataHandler.getTable();
    }

    private void resetState() {
        column.setLength(0);
        state = State.FIRST_CHAR_IN_THE_ROW;
    }

    private void processChar(char c) throws TableParserException {
        switch (c) {
            case TAB:
                if (state.isOneOf(
                        State.FIRST_CHAR_IN_THE_ROW,
                        State.FIRST_CHAR_IN_THE_COLUMN,
                        State.AFTER_CARRIAGE_RETURN,
                        State.REGULAR_CHAR)) {
                    actionEndOfColumn();
                    state = State.FIRST_CHAR_IN_THE_COLUMN;
                } else if (state.isOneOf(State.QUOTED_CHAR, State.COMMENT_CHAR)) {
                    column.append(c);
                } else if (State.AFTER_CLOSING_QUOTE == state) {
                    if (!options.contains(Option.STRIP_QUOTING)) {
                        column.append(QUOTE);
                    }
                    actionEndOfColumn();
                    state = State.FIRST_CHAR_IN_THE_COLUMN;
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
                        State.FIRST_CHAR_IN_THE_ROW,
                        State.FIRST_CHAR_IN_THE_COLUMN,
                        State.AFTER_CARRIAGE_RETURN,
                        State.REGULAR_CHAR,
                        State.COMMENT_CHAR)) {
                    actionEndOfRow();
                    state = State.AFTER_CARRIAGE_RETURN;
                } else if (state.isOneOf(State.QUOTED_CHAR)) {
                    column.append(c);
                } else if (State.AFTER_CLOSING_QUOTE == state) {
                    if (!options.contains(Option.STRIP_QUOTING)) {
                        column.append(QUOTE);
                    }
                    actionEndOfRow();
                    state = State.AFTER_CARRIAGE_RETURN;
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
                        State.FIRST_CHAR_IN_THE_ROW,
                        State.FIRST_CHAR_IN_THE_COLUMN,
                        State.REGULAR_CHAR,
                        State.COMMENT_CHAR)) {
                    actionEndOfRow();
                    state = State.FIRST_CHAR_IN_THE_ROW;
                } else if (State.QUOTED_CHAR == state) {
                    column.append(c);
                } else if (State.AFTER_CLOSING_QUOTE == state) {
                    if (!options.contains(Option.STRIP_QUOTING)) {
                        column.append(QUOTE);
                    }
                    actionEndOfRow();
                    state = State.FIRST_CHAR_IN_THE_ROW;
                } else if (State.ESCAPED_CHAR == state) {
                    column.append(ESCAPE);
                    actionEndOfRow();
                    state = State.FIRST_CHAR_IN_THE_ROW;
                } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                    column.append(ESCAPE).append(c);
                    state = State.QUOTED_CHAR;
                } else if (State.AFTER_CARRIAGE_RETURN == state) {
                    state = State.FIRST_CHAR_IN_THE_ROW;
                } else {
                    throwInvalidStateError();
                }
                break;
            case ESCAPE:
                if (state.isOneOf(
                        State.FIRST_CHAR_IN_THE_ROW,
                        State.FIRST_CHAR_IN_THE_COLUMN,
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
                } else if (State.AFTER_CLOSING_QUOTE == state) {
                    column.append(QUOTE);
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
            case QUOTE:
                if (state.isOneOf(
                        State.FIRST_CHAR_IN_THE_ROW,
                        State.AFTER_CARRIAGE_RETURN,
                        State.FIRST_CHAR_IN_THE_COLUMN)) {
                    if (!options.contains(Option.STRIP_QUOTING)) {
                        column.append(c);
                    }
                    state = State.QUOTED_CHAR;
                } else if (State.REGULAR_CHAR == state) {
                    column.append(c);
                } else if (State.QUOTED_CHAR == state) {
                    state = State.AFTER_CLOSING_QUOTE;
                } else if (State.AFTER_CLOSING_QUOTE == state) {
                    column.append(QUOTE);
                    state = State.AFTER_CLOSING_QUOTE;
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
                if (state.isOneOf(State.FIRST_CHAR_IN_THE_ROW, State.AFTER_CARRIAGE_RETURN)) {
                    column.append(c);
                    state = State.COMMENT_CHAR;
                    break;
                }
            default:
                if (state.isOneOf(
                        State.FIRST_CHAR_IN_THE_ROW,
                        State.FIRST_CHAR_IN_THE_COLUMN,
                        State.AFTER_CARRIAGE_RETURN,
                        State.REGULAR_CHAR,
                        State.ESCAPED_CHAR)) {
                    column.append(c);
                    state = State.REGULAR_CHAR;
                } else if (state.isOneOf(State.QUOTED_CHAR, State.COMMENT_CHAR)) {
                    column.append(c);
                } else if (State.AFTER_CLOSING_QUOTE == state) {
                    column.append(QUOTE);
                    state = State.REGULAR_CHAR;
                } else if (State.ESCAPED_CHAR_INSIDE_QUOTE == state) {
                    column.append(c);
                    state = State.QUOTED_CHAR;
                } else {
                    throwInvalidStateError();
                }
                break;
        }
    }

    private void actionStartOfTable() {
        resetState();
        dataHandler.startTable();
    }

    private void actionEndOfColumn() {
        dataHandler.addColumn(column.toString());
        column.setLength(0);
    }

    private void actionEndOfRow() {
        actionEndOfColumn();
        dataHandler.addRow();
    }

    private void actionEndOfTable() throws TableParserException {
        if (state.isOneOf(
                State.QUOTED_CHAR,
                State.ESCAPED_CHAR,
                State.ESCAPED_CHAR_INSIDE_QUOTE)) {
            throwInvalidStateError();
        } else if (State.AFTER_CLOSING_QUOTE == state) {
            if (!options.contains(Option.STRIP_QUOTING)) {
                column.append(QUOTE);
            }
        }
        actionEndOfRow();
        dataHandler.endTable();
    }

    private void throwInvalidStateError() throws TableParserException {
        if (options.contains(Option.THROW_PARSER_ERRORS)) {
            throw new TableParserException("Invalid parser state: " + state.name());
        }
    }

    enum State {
        FIRST_CHAR_IN_THE_ROW,
        FIRST_CHAR_IN_THE_COLUMN,
        REGULAR_CHAR,
        QUOTED_CHAR,
        AFTER_CLOSING_QUOTE,
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
