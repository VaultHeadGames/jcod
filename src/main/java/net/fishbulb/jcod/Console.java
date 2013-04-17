package net.fishbulb.jcod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.fishbulb.jcod.display.Tile;
import net.fishbulb.jcod.display.TileDisplay;
import net.fishbulb.jcod.display.Tileset;
import net.fishbulb.jcod.util.BlendFunction;
import net.fishbulb.jcod.util.CharCodes;

import static java.lang.Math.min;
import static net.fishbulb.jcod.util.ColorUtils.lerp;

/**
 * ==Incompatibilities==
 * <ul>
 * <li>There is no concept of a "root" console.  Simply create a console and use that instance.</li>
 * <li>The various print functions do not take printf-formatted strings, but require you format them yourself
 * beforehand.  This is similar to the C# and python APIs.</li>
 * <li>Text wrapping is implemented differently and may break lines in different places.  If you need perfect
 * character placement, you'll have to do it by hand.</li>
 * <li>All colors take an alpha value (including embedded color codes in strings) and a few blend modes do use it.  For
 * the most part, the existing (destination) alpha is preserved in blending and lerp ops.</li>
 * </ul>
 * <p/>
 * ==Things not supported==
 * <ul>
 * <li>keyboard and mouse functions (TileDisplay will eventually have some handlers)</li>
 * <li>initRoot (there is no static root, just create a console)</li>
 * <li>setCustomFont (use setTileSet instead)</li>
 * <li>is/setFullScreen,setWindowTitle,isWindowClosed (window control not possible)</li>
 * <li>get/setFade,getFadingColor (control the TileDisplay actor instead)</li>
 * <li>setDirty (not applicable)</li>
 * <li>load/saveApf and load/saveAsc (haven't bothered, might eventually support them)</li>
 * <li>credits (I'll do something different)</li>
 * <li></li>
 * </ul>
 */

public class Console {

    @Getter @Setter @NonNull
    private Tileset tileSet;

    @Getter
    private final int width;

    @Getter
    private final int height;

    @Getter @Setter
    private Color defaultBackground = Color.BLACK;

    @Getter @Setter
    private Color defaultForeground = Color.WHITE;

    @Getter @Setter
    private Color keyColor = null;

    @Getter @Setter
    private char defaultChar = ' '; // JCOD extension (TCOD hardwires it to space)

    @Getter @Setter
    private BlendFunction blendFunction = null;

    private final Cell[] cells;

    @Getter @Setter
    private TileDisplay display;

    // color controls are directly interpolated into strings which is why they aren't enums
    public static final char COLCTRL_1 = 1;
    public static final char COLCTRL_2 = 2;
    public static final char COLCTRL_3 = 3;
    public static final char COLCTRL_4 = 4;
    public static final char COLCTRL_5 = 5;
    public static final char COLCTRL_NUMBER = 5;
    public static final char COLCTRL_FORE_RGB = 6;
    public static final char COLCTRL_BACK_RGB = 7;
    public static final char COLCTRL_STOP = 8;

    private Color[] colorControlFore = new Color[COLCTRL_NUMBER];
    private Color[] colorControlBack = new Color[COLCTRL_NUMBER];

    public static enum Alignment {Left, Right, Center}

    @Getter @Setter
    private Alignment alignment = Alignment.Left;


    public Console(int width, int height, Tileset tileSet, TileDisplay display) {
        this.width = width;
        this.height = height;
        this.tileSet = tileSet;
        this.display = display;

        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Invalid console dimensions specified (must be at least 1x1)");
        }

        cells = new Cell[width * height];
        Tile template = (tileSet == null) ? new Tile((Drawable)null) : tileSet.get(defaultChar);
        for (int i = 0; i < (width * height); i++) {
            Tile t = template.copy().fg(defaultForeground).bg(defaultBackground);
            cells[i] = new Cell(defaultChar, t);
        }
    }

    public Console(int width, int height, Tileset tileSet) {
        this(width, height, tileSet, null);
    }

    public Console(int width, int height) {
        this(width, height, null, null);
    }

    private Cell cellAt(int x, int y) {
        if ((x >= 0) && (x < width) && (y >= 0) && (y < height))
            return cells[((y * width) + x)];
        else
            return null;
    }


    public void mapAsciiCodeToFont(char asciiCode, int fontCharX, int fontCharY) {
        tileSet.mapChar(asciiCode, tileSet.get(fontCharX, fontCharY));
    }

    public void mapAsciiCodesToFont(char firstAsciiCode, int nbCodes, int fontCharX, int fontCharY) {
        for (char c = firstAsciiCode; c < (firstAsciiCode + nbCodes); c++) {
            mapAsciiCodeToFont(c, fontCharX, fontCharY);
            fontCharX++;
            if (fontCharX == tileSet.columns()) {
                fontCharX = 0;
                fontCharY++;
            }
        }
    }

    public void mapStringToFont(CharSequence s, int fontCharX, int fontCharY) {
        for (int i = 0; i < s.length(); i++) {
            mapAsciiCodeToFont(s.charAt(i), fontCharX, fontCharY);
            fontCharX++;
            if (fontCharX == tileSet.columns()) {
                fontCharX = 0;
                fontCharY++;
            }
        }
    }

    public void clear() {
        for (Cell cell : cells) {
            cell.c(defaultChar).fg(defaultForeground).bg(defaultBackground);
        }
    }

    public void setCharBackground(int x, int y, Color col, BlendFunction blendFunc) {
        Cell c = cellAt(x, y);
        if (c == null) return;
        if (blendFunc == null) {
            c.bg(col);
        } else {
            Color bg = c.bg();
            blendFunc.blend(col, bg, bg);
            c.bg(bg);
        }
    }

    public Color getCharBackground(int x, int y) {
        Cell c = cellAt(x, y);
        if (c == null)
            return Color.BLACK.cpy();
        else
            return c.bg();
    }

    public void setCharBackground(int x, int y, Color col) {
        setCharBackground(x, y, col, blendFunction);
    }

    public Color getCharForeground(int x, int y) {
        Cell c = cellAt(x, y);
        if (c == null)
            return Color.WHITE.cpy();
        else
            return c.fg();
    }

    public void setCharForeground(int x, int y, Color col) {
        Cell c = cellAt(x, y);
        if (c == null) return;
        c.fg(col);
    }

    public char getChar(int x, int y) {
        Cell c = cellAt(x, y);
        if (c == null) return 0;
        return c.c();
    }

    public void setChar(int x, int y, char ch) {
        Cell c = cellAt(x, y);
        if (c == null) return;
        c.c(ch);
    }

    public void putChar(int x, int y, char ch, BlendFunction func) {
        Cell c = cellAt(x, y);
        if (c == null) return;
        cellAt(x, y).c(ch).fg(defaultForeground);
        setCharBackground(x, y, defaultBackground, func);
    }

    public void putChar(int x, int y, char ch) {
        putChar(x, y, ch, blendFunction);
    }

    /** Sets all character properties at once.  Background is set unconditionally (no blend function) */
    public void putCharEx(int x, int y, char ch, Color fore, Color back) {
        Cell c = cellAt(x, y);
        if (c == null) return;
        c.c(ch).fg(fore).bg(back);
    }

    // for compatibility
    public BlendFunction getBackgroundFlag() {
        return getBlendFunction();
    }

    // for compatibility
    public void setBackgroundFlag(BlendFunction func) {
        setBlendFunction(func);
    }

    public void flush() {
        // blit tiles from cells onto display
        if (display == null) return;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                display.setTile(x, y, cellAt(x, y).tile);
            }
        }
    }

    //static void setColorControl(TCOD_colctrl_t con, const TCODColor &fore, const TCODColor &back);
    public void setColorControl(char ctrl, Color fg, Color bg) {
        if ((ctrl < 1) || (ctrl > COLCTRL_NUMBER)) {
            throw new IllegalArgumentException(
                    String.format("Color control (%s) out of range -- must be between 1 and %s", ctrl, COLCTRL_NUMBER)
            );
        }
        colorControlFore[ctrl] = fg.cpy();
        colorControlBack[ctrl] = bg.cpy();
    }


    /**
     * Returns the length of a string up to the next \n, minus color codes
     *
     * @param str The string to be examined
     * @return The length of the printable portion of the line
     */
    public static int lineLength(String str, int start) {
        int len = str.length();
        int count = 0;
        for (int i = start; i < len; i++) {
            char c = str.charAt(i);
            if (((c >= 1) && (c <= COLCTRL_NUMBER)) || (c == COLCTRL_STOP)) {
                continue;
            } else if ((c == COLCTRL_BACK_RGB) || (c == COLCTRL_FORE_RGB)) {
                i += 4;
                continue;
            } else if (c == '\n') {
                return count;
            }
            count++;
        }
        return count;
    }

    /**
     * A simple utility function to return the given string, minus all color codes
     *
     * @param str The input string
     * @return str with all color codes stripped out
     */
    public static String stripColorCodes(String str) {
        return str.replaceAll("(?:[\u0001\u0002\u0003\u0004\u0005\u0008]|[\u0006\u0007]....)", "");
    }

    public static String getRGBAColorControlString(int code, float r, float g, float b, float a) {
        if (!((code == COLCTRL_FORE_RGB) || (code == COLCTRL_BACK_RGB))) {
            throw new IllegalArgumentException(String.format("Color control (%s) out of range", code));
        }

        return String.format(
                "%c%c%c%c%c",
                (char) code,
                (char) min(r * 255, 255),
                (char) min(g * 255, 255),
                (char) min(b * 255, 255),
                (char) min(a * 255, 255)
        );
    }

    public static String getRGBAColorControlString(int code, int r, int g, int b, int a) {
        if (!((code == COLCTRL_FORE_RGB) || (code == COLCTRL_BACK_RGB))) {
            throw new IllegalArgumentException(String.format("Color control (%s) out of range", code));
        }
        return String.format("%c%c%c%c%c", (char) code, (char) r, (char) g, (char) b, (char) a);
    }

    public static String getColorControlString(int code) {
        if (!((code == COLCTRL_STOP) || ((code > 0) && (code <= COLCTRL_NUMBER)))) {
            throw new IllegalArgumentException(String.format("Color code (%s) out of range", code));
        }
        return "" + (char) code;
    }


    /**
     * Implementation for all print methods.  Can be used publicly, but it's not the most convenient API
     *
     * @param x     Column of Left/Center/Rightmost char (for left/center/right align respectively)
     * @param y     Row to begin printing at
     * @param xmax  Column to stop printing at, EXCLUSIVE (will not print in this column)
     * @param ymax  Row to stop printing at, EXCLUSIVE (will not print in this row)
     * @param str   The string to print
     * @param pos   Offset within the string to begin printing at
     * @param align One of Alignment.Left, Alignment.Center, Alignment.Right
     * @param fg    Default foreground color (can be changed by color codes)
     * @param bg    Default background color (can be changed by color codes)
     * @param func  Blending function to use for background
     */
    public void putString(int x, int y, int xmax, int ymax, String str, int pos, Alignment align, Color fg, Color bg, BlendFunction func) {
        int strlen = str.length();
        int len = lineLength(str, pos);
        if (len == 0) return;
        int leftMargin = x;

        Color origFg = fg;
        Color origBg = bg;

        switch (align) {
            case Left: /* no change */ break;
            case Right: x -= len - 1; break;
            case Center: x -= len / 2; break;
        }
        for (int i = pos; i < strlen; i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\n':
                    if (++y >= ymax) return;
                    len = lineLength(str, i + 1);
                    if (len == 0) return;
                    x = leftMargin;
                    switch (align) {
                        case Left: /* no change */ break;
                        case Right: x -= len - 1; break;
                        case Center: x -= len / 2; break;
                    }
                    continue;

                case COLCTRL_1: case COLCTRL_2: case COLCTRL_3: case COLCTRL_4: case COLCTRL_5:
                    if (colorControlFore[c] != null) fg = colorControlFore[c];
                    if (colorControlBack[c] != null) bg = colorControlBack[c];
                    continue;

                case COLCTRL_BACK_RGB:
                    bg = new Color((float) str.charAt(++i) / 255f, (float) str.charAt(++i) / 255f, (float) str.charAt(++i) / 255f, (float) str.charAt(++i) / 255f);
                    continue;

                case COLCTRL_FORE_RGB:
                    fg = new Color((float) str.charAt(++i) / 255f, (float) str.charAt(++i) / 255f, (float) str.charAt(++i) / 255f, (float) str.charAt(++i) / 255f);
                    continue;

                case COLCTRL_STOP:
                    fg = origFg;
                    bg = origBg;
                    continue;

                default:
                    if (x >= xmax) continue;        // went past margin
                    Cell cell = cellAt(x++, y);
                    if (cell == null) continue;     // went off the edge

                    cell.c(c).fg(fg);

                    if (func == null) {
                        cell.bg(bg);
                    } else {
                        Color dest = cell.bg();
                        func.blend(bg, dest, dest);
                        cell.bg(dest);
                    }
            }

        }
    }

    public void print(int x, int y, String message) {
        putString(x, y, width, height, message, 0, alignment, defaultForeground, defaultBackground, blendFunction);
    }

    public void printEx(int x, int y, BlendFunction func, Alignment align, String message) {
        putString(x, y, width, height, message, 0, align, defaultForeground, defaultBackground, func);
    }

    // Classic word wrap algorithms don't work very well when you have zero-width sequences like we do.
    // This one simply replaces spaces with a \n where appropriate.  It probably doesn't handle runs of
    // multiple spaces as well as it could, but I can live with that corner case.
    static String wrap(String str, int margin) {
        int len = str.length();
        char[] chars = new char[len];
        str.getChars(0, len, chars, 0);
        int lastSpace = -1;
        int remain = margin;
        for (int i = 0; i < len; i++) {
            char c = chars[i];
            switch (c) {
                case '\n':
                    remain = margin;
                    lastSpace = i;
                    continue;

                case ' ':
                    remain--;
                    lastSpace = i;
                    continue;

                case COLCTRL_1: case COLCTRL_2: case COLCTRL_3: case COLCTRL_4: case COLCTRL_5: case COLCTRL_STOP:
                    continue;

                case COLCTRL_BACK_RGB: case COLCTRL_FORE_RGB:
                    i += 4;
                    continue;

                default:
                    if ((--remain <= 0) && (lastSpace != -1) && (chars[lastSpace] != '\n')) {
                        chars[lastSpace] = '\n';
                        i = lastSpace + 1;      // rewind.  adjusting remain is too tricky, because of color codes
                        remain = margin;
                    }
            }
        }
        return new String(chars);
    }

    // basic brute force char counter, nothing special
    static int countMatches(String s, char c) {
        int i = 0;
        int len = s.length();
        int count = 0;
        while (i < len) {
            if (s.charAt(i++) == c) count++;
        }
        return count;
    }


    /**
     * Prints a string within the given rectangle, using default colors, alignment, and blending.  Returns the number
     * of lines that were printed.
     *
     * @param x   Column to begin printing at
     * @param y   Row to begin printing at
     * @param w   Width of rectangle
     * @param h   Height of rectangle
     * @param msg Message to print
     * @return Number of lines actually printed.
     */
    public int printRect(int x, int y, int w, int h, String msg) {
        return printRectEx(x, y, w, h, defaultForeground, defaultBackground, blendFunction, alignment, msg);
    }

    /**
     * Prints a string within the given rectangle, using default colors and the given blending and alignment.
     * Returns the number of lines that were printed
     *
     * @param x     Column to begin printing at
     * @param y     Row to begin printing at
     * @param w     Width of rectangle
     * @param h     Height of rectangle
     * @param blend Blend mode to use
     * @param align Alignment to use (one of Alighment.{Left,Right,Center}
     * @param msg   Message to print
     * @return Number of lines actually printed
     */
    public int printRectEx(int x, int y, int w, int h, BlendFunction blend, Alignment align, String msg) {
        return printRectEx(x, y, w, h, defaultForeground, defaultBackground, blend, align, msg);
    }

    /**
     * JCOD extension: Prints a string within the given rectangle, using given colors, blending and alignment.
     * Returns the number of lines that were printed
     *
     * @param x     Column to begin printing at
     * @param y     Row to begin printing at
     * @param w     Width of rectangle
     * @param h     Height of rectangle
     * @param fg    Foreground color
     * @param bg    Background color
     * @param blend Blend mode to use
     * @param align Alignment to use (one of Alighment.{Left,Right,Center}
     * @param msg   Message to print
     * @return Number of lines actually printed
     */
    public int printRectEx(int x, int y, int w, int h, Color fg, Color bg, BlendFunction blend,
                           Alignment align, String msg) {
        String wrapped = wrap(msg, w);
        if (h == 0) {
            // documented behavior of tcod, if h = 0, then use console height as maximum.
            // we could just set it to height, but we need h to be accurate for the count
            h = height - y;
        }
        putString(x, y, x + w, y + h, wrapped, 0, align, fg, bg, blend);
        return min(countMatches(wrapped, '\n'), h);
    }

    /**
     * Calculates the number of lines that would be printed in the given rectangle according to printRect[Ex], but
     * without actually printing them.
     *
     * @param x   Leftmost column of rectangle (not used)
     * @param y   Topmost row of rectangle
     * @param w   Width of rectangle
     * @param h   Height of rectangle
     * @param msg Message to test
     * @return Number of lines that would be printed
     */
    public int getHeightRect(@SuppressWarnings("UnusedParameters") int x, int y, int w, int h, String msg) {
        String wrapped = wrap(msg, w);
        if (h == 0) {
            // Not documented in getHeightRect, but consistent with printRect and TCOD's actual behavior
            h = height - y;
        }
        return min(countMatches(wrapped, '\n'), h);
    }


    public void rect(int x, int y, int w, int h, boolean clear, BlendFunction blend) {
        for (int iy = y; iy < (y + h); iy++) {
            for (int ix = x; ix < (x + w); ix++) {
                setCharBackground(ix, iy, defaultBackground, blend);
                if (clear) {
                    setChar(ix, iy, defaultChar);
                }
            }
        }
    }

    public void rect(int x, int y, int w, int h, boolean clear) {
        rect(x, y, w, h, clear, blendFunction);
    }

    public void hline(int x, int y, int l, BlendFunction blend) {
        for (int ix = x; ix < (x + l); ix++) {
            putChar(ix, y, CharCodes.OEM.HLINE, blend);
        }
    }

    public void hline(int x, int y, int l) {
        hline(x, y, l, blendFunction);
    }

    public void vline(int x, int y, int l, BlendFunction blend) {
        for (int iy = y; iy < (y + l); iy++) {
            putChar(x, iy, CharCodes.OEM.VLINE, blend);
        }
    }

    public void vline(int x, int y, int l) {
        vline(x, y, l, blendFunction);
    }

    public void printFrame(int x, int y, int w, int h, boolean clear, BlendFunction blend, String title) {
        // draw the corners
        putChar(x, y, CharCodes.OEM.NW, blend);
        putChar((x + w) - 1, y, CharCodes.OEM.NE, blend);
        putChar((x + w) - 1, (y + h) - 1, CharCodes.OEM.SE, blend);
        putChar(x, (y + h) - 1, CharCodes.OEM.SW, blend);

        // draw edges
        if (w > 2) {
            hline(x + 1, y, w - 2, blend);
            hline(x + 1, (y + h) - 1, w - 2, blend);
        }
        if (h > 2) {
            vline(x, y + 1, h - 2, blend);
            vline((x + w) - 1, y + 1, h - 2, blend);
        }
        // clear out if asked to
        if (clear) {
            rect(x + 1, y + 1, w - 2, h - 2, true, blend);
        }

        // render title if given.  Note that the color is reversed and is not customizeable, nor is the blend function
        if (title != null) {
            printRectEx((x + (w / 2)) - 1, y, w - 2, 1, defaultBackground, defaultForeground, null, Alignment.Center, title);
        }
    }

    public void printFrame(int x, int y, int w, int h, boolean clear, BlendFunction blend) {
        printFrame(x, y, w, h, clear, blend, null);
    }

    public void printFrame(int x, int y, int w, int h, boolean clear) {
        printFrame(x, y, w, h, clear, blendFunction, null);
    }

    public void printFrame(int x, int y, int w, int h) {
        printFrame(x, y, w, h, true, blendFunction, null);
    }

    @SuppressWarnings({"ConstantConditions", "UnusedDeclaration"})
    public void blit(int xSrc, int ySrc, int wSrc, int hSrc, Console dest, int xDest, int yDest,
                     float fgAlpha, float bgAlpha) {
        // if wSrc or hSrc are 0, set them to width or height
        if (wSrc == 0) wSrc = width;
        if (hSrc == 0) hSrc = height;
        for (int iy = 0; iy < hSrc; iy++) {
            for (int ix = 0; ix < wSrc; ix++) {
                Cell srcCell = cellAt(xSrc + ix, ySrc + iy);
                if (srcCell == null) continue;

                Cell dstCell = dest.cellAt(xDest + ix, yDest + iy);
                if (dstCell == null) continue;

                if ((keyColor != null) && keyColor.equals(srcCell.bg())) {
                    // transparent key color, skip it
                    continue;
                }

                if ((Math.abs(1.0 - fgAlpha) < 0.001) && (Math.abs(1.0 - bgAlpha) < 0.001)) {
                    dstCell.c(srcCell.c()).fg(srcCell.fg()).bg(srcCell.bg());
                } else {
                    // copied wholesale from TCOD_console_blit
                    dstCell.bg(lerp(dstCell.bg(), srcCell.bg(), bgAlpha));
                    if (srcCell.c() == ' ') {
                        dstCell.fg(lerp(dstCell.fg(), srcCell.bg(), bgAlpha));
                    } else if (dstCell.c() == ' ') {
                        dstCell.c(srcCell.c());
                        dstCell.fg(lerp(dstCell.bg(), srcCell.fg(), fgAlpha));
                    } else if (dstCell.c() == srcCell.c()) {
                        dstCell.fg(lerp(dstCell.fg(), srcCell.fg(), fgAlpha));
                    } else {
                        if (fgAlpha < 0.5f) {
                            dstCell.fg(lerp(dstCell.fg(), dstCell.bg(), fgAlpha * 2));
                        } else {
                            dstCell.c = srcCell.c;
                            dstCell.fg(lerp(dstCell.bg(), srcCell.fg(), (fgAlpha - 0.5f) * 2));
                        }
                    }
                }
            }
        }
    }

    protected class Cell {
        private char c;
        private Tile tile;

        public Cell(char c, Tile tile) {
            this.c = c;
            this.tile = tile;
        }

        public char c() {
            return c;
        }

        public Cell c(char c) {
            this.c = c;
            if (tileSet != null)
                tile.glyph(tileSet.get(c).glyph());
            return this;
        }

        public Color fg() {
            return tile.fg();
        }

        public Cell fg(Color fg) {
            tile.fg(fg);
            return this;
        }

        public Color bg() {
            return tile.bg();
        }

        public Cell bg(Color bg) {
            tile.bg(bg);
            return this;
        }
    }
}
