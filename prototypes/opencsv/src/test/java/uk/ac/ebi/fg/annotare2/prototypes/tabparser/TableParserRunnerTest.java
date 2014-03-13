package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class TableParserRunnerTest {

    // default charset for testing
    private final static String DEFAULT_ENCODING                            = "UTF-8";

    // newline constant for testing
    private final static String NEWLINE                                     = "\n";

    // single line test rows
    private final static String ROW_SIMPLE                                  = "foo\tbar\tbaz";
    private final static String[] PARSED_SIMPLE                             = {"foo", "bar", "baz"};
    private final static String ROW_COMMENT                                 = "# comment";
    private final static String[] PARSED_COMMENT                            = {"# comment"};
    private final static String ROW_EMPTY                                   = "          ";
    private final static String[] PARSED_EMPTY                              = {};
    private final static String ROW_QUOTED                                  = "\"foo\"\tbar\t\"baz\"";
    private final static String[] PARSED_QUOTED_UNSTRIPPED                  = {"\"foo\"", "bar", "\"baz\""};
    private final static String[] PARSED_QUOTED_STRIPPED                    = PARSED_SIMPLE;
    private final static String ROW_QUOTE_ESCAPED                           = "\"foo\"\t\\\"bar\\\"\tbaz";
    private final static String[] PARSED_QUOTE_ESCAPED_UNSTRIPPED           = {"\"foo\"", "\"bar\"", "baz"};
    private final static String[] PARSED_QUOTE_ESCAPED_STRIPPED             = {"foo", "\"bar\"", "baz"};

    // multiline test rows
    private final static String ROW_FIRST_MULTILINE_CELL                    = "\"foo1" + NEWLINE + "foo2\"\tbar\tbaz";
    private final static String[] PARSED_FIRST_MULTILINE_CELL_STRIPPED      = {"foo1" + NEWLINE + "foo2", "bar", "baz"};
    private final static String ROW_MIDDLE_MULTILINE_CELL                   = "\"foo\"\t\"bar1" + NEWLINE + "bar2" + NEWLINE + "bar3\"\tbaz";
    private final static String[] PARSED_MIDDLE_MULTILINE_CELL_UNSTRIPPED   = {"\"foo\"", "\"bar1" + NEWLINE + "bar2" + NEWLINE + "bar3\"", "baz"};
    private final static String ROW_ALL_MULTILINE_CELLS                     = "\"foo1" + NEWLINE + "foo2" + NEWLINE + "foo3\"\t\"bar1" + NEWLINE + "bar2\"\t\"baz1" + NEWLINE + "baz2\"";
    private final static String[] PARSED_ALL_MULTILINE_CELLS_STRIPPED       = {"foo1" + NEWLINE + "foo2" + NEWLINE + "foo3", "bar1" + NEWLINE + "bar2", "baz1" + NEWLINE + "baz2"};

    // incorrect multiline test rows
    private final static String ROW_INCORRECT_FIRST_MULTILINE_CELL          = "\"foo1" + NEWLINE + "foo2\tbar\tbaz";

    // sample section tags
    private final static String SECTION_TAG_1                               = "[SECTION_1]";
    private final static String SECTION_TAG_2                               = "[SECTION_2]";

    @Test
    public void testParse_1() throws Exception {
        // try simple one-liner
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE,
                DEFAULT_ENCODING,
                false);
        assertTrue("Output should contain 1 line", 1 == out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
    }

    @Test
    public void testParse_2() throws TableParserException {
        // try standard, comment and quoted tabulation; quoting is not stripped out
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE + NEWLINE + ROW_COMMENT + NEWLINE + ROW_QUOTED,
                DEFAULT_ENCODING,
                false);
        assertTrue("Output should contain 3 lines", 3 == out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
        assertArrayEquals(PARSED_COMMENT, out[1]);
        assertArrayEquals(PARSED_QUOTED_UNSTRIPPED, out[2]);
    }

    @Test
    public void testParse_3() throws TableParserException {
        // try quoted multiline row
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_QUOTE_ESCAPED,
                DEFAULT_ENCODING,
                false);
        assertTrue("Output should contain 1 line", 1 == out.length);
        assertArrayEquals(PARSED_QUOTE_ESCAPED_UNSTRIPPED, out[0]);
    }

    @Test
    public void testParse_4() throws TableParserException {
        // try complex tabulation w/o stripping out quoting
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE + NEWLINE + ROW_EMPTY + NEWLINE + ROW_QUOTED + NEWLINE + ROW_QUOTE_ESCAPED + NEWLINE
                        + ROW_MIDDLE_MULTILINE_CELL + NEWLINE + ROW_COMMENT + NEWLINE,
                DEFAULT_ENCODING,
                false);
        assertTrue("Output should contain 6 lines", 6 == out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
        assertArrayEquals(PARSED_EMPTY, out[1]);
        assertArrayEquals(PARSED_QUOTED_UNSTRIPPED, out[2]);
        assertArrayEquals(PARSED_QUOTE_ESCAPED_UNSTRIPPED, out[3]);
        assertArrayEquals(PARSED_MIDDLE_MULTILINE_CELL_UNSTRIPPED, out[4]);
        assertArrayEquals(PARSED_COMMENT, out[5]);
    }

    @Test
    public void testParse_5() throws TableParserException {
        // try complex tabulation with stripping out quoting
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE + NEWLINE + ROW_QUOTED + NEWLINE + ROW_QUOTE_ESCAPED + NEWLINE +
                        ROW_FIRST_MULTILINE_CELL + NEWLINE + ROW_ALL_MULTILINE_CELLS,
                DEFAULT_ENCODING,
                true);
        assertTrue("Output should contain 5 lines", 5 == out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
        assertArrayEquals(PARSED_QUOTED_STRIPPED, out[1]);
        assertArrayEquals(PARSED_QUOTE_ESCAPED_STRIPPED, out[2]);
        assertArrayEquals(PARSED_FIRST_MULTILINE_CELL_STRIPPED, out[3]);
        assertArrayEquals(PARSED_ALL_MULTILINE_CELLS_STRIPPED, out[4]);
    }

    @Test(expected=TableParserException.class)
    public void testParse_6() throws TableParserException {
        // try invalid tab
        runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE + NEWLINE + ROW_INCORRECT_FIRST_MULTILINE_CELL,
                DEFAULT_ENCODING,
                true);
    }

    @Test
    public void testParseSection_1() throws TableParserException {
        String line;
        // try standard and comment tabulation

        line = SECTION_TAG_1 + NEWLINE + ROW_SIMPLE + NEWLINE + ROW_COMMENT + NEWLINE +
                SECTION_TAG_2 + NEWLINE + ROW_SIMPLE + NEWLINE + ROW_QUOTED + NEWLINE + ROW_QUOTE_ESCAPED;
        ByteArrayInputStream in = new ByteArrayInputStream(line.getBytes());
        String[][] out = readMergedTabDelimitedInputStream(
                in,
                DEFAULT_ENCODING,
                false,
                SECTION_TAG_1,
                SECTION_TAG_2);
        assertEquals("Output should contain 2 lines", 2, out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
        assertArrayEquals(PARSED_COMMENT, out[1]);

        out = readTabDelimitedInputStream(in, DEFAULT_ENCODING, true);
        assertTrue("Output should contain 3 lines", 3 == out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
        assertArrayEquals(PARSED_QUOTED_STRIPPED, out[1]);
        assertArrayEquals(PARSED_QUOTE_ESCAPED_STRIPPED, out[2]);
    }

    private String[][] runReadTabDelimitedInputStreamOnString(String input, String encoding, boolean stripQuoting)
            throws TableParserException {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        return readTabDelimitedInputStream(in, encoding, stripQuoting);
    }

    private String[][] readTabDelimitedInputStream(InputStream input, String encoding, boolean stripQuoting)
            throws TableParserException {
        try {
            return new TableParserRunner().parse(input, encoding, stripQuoting, true);
        } catch (IOException x) {
            x.printStackTrace();
        }
        return null;
    }

    private String[][] readMergedTabDelimitedInputStream(InputStream input, String encoding, boolean stripQuoting,
                                                                    String startTag, String endTag)
            throws TableParserException {
        try {
            return new TableParserRunner().parseSection(input, encoding, stripQuoting, true, startTag, endTag);
        } catch (IOException x) {
            x.printStackTrace();
        }
        return null;
    }
}
