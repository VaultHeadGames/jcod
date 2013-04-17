package net.fishbulb.jcod;

import mockit.Mocked;
import net.fishbulb.jcod.display.TileDisplay;
import net.fishbulb.jcod.display.Tileset;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;

public class ConsoleTest {
    private static final Logger LOG = Logger.getLogger(ConsoleTest.class);

    // not actually useful yet...
    @Mocked Tileset mockTileset;
    @Mocked TileDisplay mockDisplay;

    // helps in diagnosing failed test results
    private static byte[] strBytes(String s) {
        return s.getBytes(Charset.forName("ISO-8859-1"));
    }

    @Test
    public void testGetColorString() throws Exception {
        assertEquals(Console.getColorControlString(Console.COLCTRL_1), "\u0001");
        assertEquals(
                strBytes(Console.getRGBAColorControlString(Console.COLCTRL_FORE_RGB, 0, 0, 0, 0)),
                strBytes("\u0006\0\0\0\0")
        );

        assertEquals(strBytes(
                Console.getRGBAColorControlString(Console.COLCTRL_BACK_RGB, 0.5f, 0.25f, 1f, 0f)),
                strBytes("\u0007\u007f\u003f\u00ff\u0000")
        );
    }

    @Test
    public void testLineLength() throws Exception {
        // one line
        assertEquals(Console.lineLength("foobarbaz", 0), 9);
        assertEquals(Console.lineLength("foo\u0001bar\u0002baz", 0), 9);
        assertEquals(Console.lineLength("foo\u0007abcdbar\u0002baz", 0), 9);
        assertEquals(Console.lineLength("", 0), 0);
        assertEquals(Console.lineLength("abcdef", 2), 4);
        assertEquals(Console.lineLength("abcdef", 10), 0);

        // multiple lines
        assertEquals(Console.lineLength("foo\u0007abcdbar\u0002baz\nmumble frotz", 0), 9);
        assertEquals(Console.lineLength("foo\u0007abcdbar\u0002\nbaz mumble frotz", 0), 6);
        assertEquals(Console.lineLength("foo\n\u0007abcdbar\u0002\nbaz mumble frotz", 0), 3);
        assertEquals(Console.lineLength("foo\n\u0007abcdbar\u0002\nbaz mumble frotz", 2), 1);
        assertEquals(Console.lineLength("foo\n\u0007abcdbar\u0002\nbaz mumble frotz", 3), 0);
        assertEquals(Console.lineLength("foo\n\u0007abcdbar\u0002\nbaz mumble frotz", 4), 3);

        // \n inside a color escape sequence
        assertEquals(Console.lineLength("foo\u0007ab\ndbar\u0002\nbazmumble frotz", 0), 6);
    }

    @Test
    public void testWrap() throws Exception {
        assertEquals(Console.wrap("foobarbazmumblefrotz", 20), "foobarbazmumblefrotz");
        assertEquals(Console.wrap("foobarbazmumblefrotz", 5), "foobarbazmumblefrotz");

        assertEquals(Console.wrap("apple kumquat apple", 10), "apple\nkumquat\napple");

        assertEquals(Console.wrap("Lorem ipsum dolor sit amet", 10), "Lorem\nipsum\ndolor sit\namet");

        String wrapped;
        wrapped = Console.wrap("foo bar baz mumble frotz", 8);
        assertEquals(wrapped, "foo bar\nbaz\nmumble\nfrotz");

        wrapped = Console.wrap("foo bar baz mumble frotz", 12);
        assertEquals(wrapped, "foo bar baz\nmumble frotz");

        wrapped = Console.wrap("foo bar baz reallylongstringthatwontwrapnosir mumble frotz", 12);
        assertEquals(wrapped, "foo bar baz\nreallylongstringthatwontwrapnosir\nmumble frotz");

        wrapped = Console.wrap("reallylongstringthatwontwrapnosir foo bar baz mumble frotz", 12);
        assertEquals(wrapped, "reallylongstringthatwontwrapnosir\nfoo bar baz\nmumble frotz");

        wrapped = Console.wrap("foo b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001ar baz mumble frotz", 12);
        assertEquals(wrapped, "foo b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001ar baz\nmumble frotz");

        wrapped = Console.wrap("foo b\u0006abcd\u0006abcd\u0007abcd\u0007abcd\u0007abcdar baz mumble frotz", 12);
        assertEquals(wrapped, "foo b\u0006abcd\u0006abcd\u0007abcd\u0007abcd\u0007abcdar baz\nmumble frotz");

        wrapped = Console.wrap("foo  bar             baz    mumble  frotz", 12);
//        LOG.warn("\n" + wrapped.replace(" ", "."));
        assertEquals(wrapped, "foo  bar            \nbaz   \nmumble \nfrotz");

        wrapped = Console.wrap("foo\nbar baz xyz mumble frotz", 12);
        assertEquals(wrapped, "foo\nbar baz xyz\nmumble frotz");
    }



}
