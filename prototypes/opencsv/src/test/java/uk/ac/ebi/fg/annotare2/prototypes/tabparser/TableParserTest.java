package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TableParserTest {

    // default charset for testing
    private final static String DEFAULT_ENCODING                            = "UTF-8";

    // newline constant for testing
    private final static String NL                                          = System.getProperty("line.separator");

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
    private final static String ROW_QUOTE_ESCAPED                           = "foo\t\\\"bar\\\"\tbaz";
    private final static String[] PARSED_QUOTE_ESCAPED_UNSTRIPPED           = {"foo", "\\\"bar\\\"", "baz"};
    private final static String[] PARSED_QUOTE_ESCAPED_STRIPPED             = {"foo", "\"bar\"", "baz"};

    // multiline test rows
    private final static String ROW_FIRST_MULTILINE_CELL                    = "\"foo1\" + NL + \"foo2\"\tbar\tbaz";
    private final static String[] PARSED_FIRST_MULTILINE_CELL_STRIPPED      = {"foo1" + NL + "foo2", "bar", "baz"};
    private final static String ROW_MIDDLE_MULTILINE_CELL                   = "\"foo\"\t\"bar1" + NL + "bar2" + NL + "bar3\"\tbaz";
    private final static String[] PARSED_MIDDLE_MULTILINE_CELL_UNSTRIPPED   = {"\"foo\"", "\"bar1" + NL + "bar2" + NL + "bar3\"", "baz"};
    private final static String ROW_ALL_MULTILINE_CELLS                     = "\"foo1" + NL + "foo2" + NL + "foo3\"\t\"bar1" + NL + "bar2\"\t\"baz1" + NL + "baz2\"";
    private final static String[] PARSED_ALL_MULTILINE_CELLS_STRIPPED       = {"foo1" + NL + "foo2" + NL + "foo3", "bar1" + NL + "bar2", "baz1" + NL + "baz2"};

    // incorrect multiline test rows
    private final static String ROW_INCORRECT_FIRST_MULTILINE_CELL          = "\"foo1\" + NL + \"foo2\tbar\tbaz";


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
    public void testParse_2() {
        // try standard, comment and quoted tabulation; quoting is not stripped out
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE + NL + ROW_COMMENT + NL + ROW_QUOTED,
                DEFAULT_ENCODING,
                false);
        assertTrue("Output should contain 3 lines", 3 == out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
        assertArrayEquals(PARSED_COMMENT, out[1]);
        assertArrayEquals(PARSED_QUOTED_UNSTRIPPED, out[2]);
    }

    @Test
    public void testParse_3() {
        // try quoted multiline row
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_QUOTE_ESCAPED,
                DEFAULT_ENCODING,
                false);
        assertTrue("Output should contain 1 line", 1 == out.length);
        assertArrayEquals(PARSED_QUOTE_ESCAPED_STRIPPED, out[0]);
    }

    @Test
    public void testParse_4() {
        // try complex tabulation w/o stripping out quoting
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE + NL + ROW_EMPTY + NL + ROW_QUOTED + NL + ROW_QUOTE_ESCAPED + NL
                        + ROW_MIDDLE_MULTILINE_CELL + NL + ROW_COMMENT + NL,
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
    public void testParse_5() {
        // try complex tabulation with stripping out quoting
        String[][] out = runReadTabDelimitedInputStreamOnString(
                ROW_SIMPLE + NL + ROW_QUOTED + NL + ROW_QUOTE_ESCAPED + NL +
                        ROW_FIRST_MULTILINE_CELL + NL + ROW_ALL_MULTILINE_CELLS,
                DEFAULT_ENCODING,
                true);
        assertTrue("Output should contain 5 lines", 5 == out.length);
        assertArrayEquals(PARSED_SIMPLE, out[0]);
        assertArrayEquals(PARSED_QUOTED_STRIPPED, out[1]);
        assertArrayEquals(PARSED_QUOTE_ESCAPED_STRIPPED, out[2]);
        assertArrayEquals(PARSED_FIRST_MULTILINE_CELL_STRIPPED, out[3]);
        assertArrayEquals(PARSED_ALL_MULTILINE_CELLS_STRIPPED, out[4]);
    }

    private String[][] runReadTabDelimitedInputStreamOnString(String input, String encoding, boolean stripQuoting) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
            return new TableParser().parse(in, encoding, stripQuoting, false);
        } catch (IOException x) {
            x.printStackTrace();
        }
        return null;
    }
}
