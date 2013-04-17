package net.fishbulb.jcod.util;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import net.fishbulb.jcod.Console;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static net.fishbulb.jcod.util.ColorUtils.lerp;
import static net.fishbulb.jcod.util.ColorUtils.rgbdist;

public final class ImageUtils {

    private ImageUtils() {}

    public static Pixmap makeColorTransparent(Pixmap pixmap, Color color) {
        Pixmap p = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        p.drawPixmap(pixmap, 0, 0);

        int trans = Color.rgba8888(color);

        ByteBuffer bytes = p.getPixels();
        IntBuffer pixels = bytes.asIntBuffer();
        pixels.rewind();

        while (pixels.hasRemaining()) {

            pixels.mark();
            int pixel = pixels.get();
            if (pixel == trans) {
                pixels.reset();
                pixels.put(0);
            }
        }
        return p;
    }

    public static Pixmap grayScaleToAlpha(Pixmap pixmap, boolean premultiplied) {
        Pixmap p = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        p.drawPixmap(pixmap, 0, 0);

        ByteBuffer pixels = p.getPixels();
        pixels.rewind();

        while (pixels.hasRemaining()) {
            pixels.mark();
            byte r = pixels.get();
            byte g = pixels.get();
            byte b = pixels.get();
            //noinspection UnusedAssignment
            byte a = pixels.get();

            if ((r == g) && (g == b)) {
                a = r;
                if (!premultiplied) {
                    r = g = b = (byte) 255;
                }
                pixels.reset();
                pixels.put(r);
                pixels.put(g);
                pixels.put(b);
                pixels.put(a);
            }
        }
        pixels.rewind();
        return p;
    }

    public static Pixmap grayScaleToAlpha(Pixmap pixmap) {
        return grayScaleToAlpha(pixmap, false);     // default blend mode in SpriteBatch assumes non-premultiplied
    }


    public static void imageBlit(Pixmap image, Console console, float x, float y,
                                 BlendFunction bkgnd_flag, float scalex, float scaley, float angle) {

        Color tmp = new Color();

        if (scalex == 0.0f || scaley == 0.0f || bkgnd_flag == BlendMode.None) return;
        int width = image.getWidth();
        int height = image.getHeight();
        if (scalex == 1.0f && scaley == 1.0f && angle == 0.0f && x - ((int) x) == 0.0f && y - ((int) y) == 0.0f) {
            // clip the image
            int ix = (int) (x - width * 0.5f);
            int iy = (int) (y - height * 0.5f);
            int minx = max(ix, 0);
            int miny = max(iy, 0);
            int maxx = min(ix + width, console.getWidth());
            int maxy = min(iy + height, console.getHeight());
            int offx = 0, offy = 0;
            int cx, cy;
            if (ix < 0) offx = -ix;
            if (iy < 0) offy = -iy;
            for (cx = minx; cx < maxx; cx++) {
                for (cy = miny; cy < maxy; cy++) {
                    int pixel = image.getPixel(cx - minx + offx, cy - miny + offy);
                    Color.rgba8888ToColor(tmp, pixel);
                    console.setCharBackground(cx, cy, tmp, bkgnd_flag);
                }
            }
        } else {
            float iw = width / 2 * scalex;
            float ih = height / 2 * scaley;
            // get the coordinates of the image corners in the console
            float newx_x = (float) cos(angle);
            float newx_y = -(float) sin(angle);
            float newy_x = newx_y;
            float newy_y = -newx_x;
            float x0, y0, x1, y1, x2, y2, x3, y3; /* image corners coordinates */
            int rx, ry, rw, rh; /* rectangular area in the console */
            int cx, cy;
            int minx, miny, maxx, maxy;
            float invscalex, invscaley;
            // 0 = P - w/2 x' +h/2 y'
            x0 = x - iw * newx_x + ih * newy_x;
            y0 = y - iw * newx_y + ih * newy_y;
            // 1 = P + w/2 x' + h/2 y'
            x1 = x + iw * newx_x + ih * newy_x;
            y1 = y + iw * newx_y + ih * newy_y;
            // 2 = P + w/2 x' - h/2 y'
            x2 = x + iw * newx_x - ih * newy_x;
            y2 = y + iw * newx_y - ih * newy_y;
            // 3 = P - w/2 x' - h/2 y'
            x3 = x - iw * newx_x - ih * newy_x;
            y3 = y - iw * newx_y - ih * newy_y;
            // get the affected rectangular area in the console
            rx = (int) (min(min(x0, x1), min(x2, x3)));
            ry = (int) (min(min(y0, y1), min(y2, y3)));
            rw = (int) (max(max(x0, x1), max(x2, x3))) - rx;
            rh = (int) (max(max(y0, y1), max(y2, y3))) - ry;
            // clip it
            minx = max(rx, 0);
            miny = max(ry, 0);
            maxx = min(rx + rw, console.getWidth());
            maxy = min(ry + rh, console.getHeight());
            invscalex = 1.0f / scalex;
            invscaley = 1.0f / scaley;
            for (cx = minx; cx < maxx; cx++) {
                for (cy = miny; cy < maxy; cy++) {
                    float ix, iy;
                    // map the console pixel to the image world
                    ix = (iw + (cx - x) * newx_x + (cy - y) * (-newy_x)) * invscalex;
                    iy = (ih + (cx - x) * (newx_y) - (cy - y) * newy_y) * invscaley;
                    int pixel = image.getPixel((int) (ix), (int) (iy));
                    Color.rgba8888ToColor(tmp, pixel);
                    console.setCharBackground(cx, cy, tmp, bkgnd_flag);
                }
            }
        }
    }


    private static int getPattern(Color[] desired, Color[] palette) {
        // adapted from Jeff Lait's code posted on r.g.r.d
        int flag = 0;

        // pixels have following flag values :
        // X 1
        // 2 4
        // flag indicates which pixels uses foreground color (palette[1])

        int[] flagToAscii = {
                0,
                CharCodes.OEM.SUBP_NE, CharCodes.OEM.SUBP_SW, -CharCodes.OEM.SUBP_DIAG, CharCodes.OEM.SUBP_SE,
                CharCodes.OEM.SUBP_E, -CharCodes.OEM.SUBP_N, -CharCodes.OEM.SUBP_NW
        };

        int[] weight = {0, 0};
        int i;

        // First colour trivial.
        palette[0] = desired[0];

        // Ignore all duplicates...
        for (i = 1; i < 4; i++) {
            if (desired[i].r != palette[0].r || desired[i].g != palette[0].g || desired[i].b != palette[0].b)
                break;
        }

        if (i == 4) {
            return ' ';
        }

        weight[0] = i;

        // Found a second colour...
        palette[1] = desired[i];
        weight[1] = 1;
        flag |= 1 << (i - 1);

        // remaining colours
        i++;
        while (i < 4) {
            if (desired[i].r == palette[0].r && desired[i].g == palette[0].g && desired[i].b == palette[0].b) {
                weight[0]++;
            } else if (desired[i].r == palette[1].r && desired[i].g == palette[1].g && desired[i].b == palette[1].b) {
                flag |= 1 << (i - 1);
                weight[1]++;
            } else {
                // Bah, too many colours,
                // merge the two nearest
                float dist0i = rgbdist(desired[i], palette[0]);
                float dist1i = rgbdist(desired[i], palette[1]);
                float dist01 = rgbdist(palette[0], palette[1]);
                if (dist0i < dist1i) {
                    if (dist0i <= dist01) {
                        // merge 0 and i
                        palette[0] = lerp(desired[i], palette[0], weight[0] / (1.0f + weight[0]));
                        weight[0]++;
                    } else {
                        // merge 0 and 1
                        palette[0] = lerp(palette[0], palette[1], (float) (weight[1]) / (weight[0] + weight[1]));
                        weight[0]++;
                        palette[1] = desired[i];
                        flag = 1 << (i - 1);
                    }
                } else {
                    if (dist1i <= dist01) {
                        // merge 1 and i
                        palette[1] = lerp(desired[i], palette[1], weight[1] / (1.0f + weight[1]));
                        weight[1]++;
                        flag |= 1 << (i - 1);
                    } else {
                        // merge 0 and 1
                        palette[0] = lerp(palette[0], palette[1], (float) (weight[1]) / (weight[0] + weight[1]));
                        weight[0]++;
                        palette[1] = desired[i];
                        flag = 1 << (i - 1);
                    }
                }
            }
            i++;
        }
        return flagToAscii[flag];
    }


    public static void imageBlit2x(Pixmap image, Console con, int dx, int dy, int sx, int sy, int w, int h) {
        // clamp source coordinates to image dimensions
        sx = max(0, sx);
        sy = max(0, sy);
        w = min(w, image.getWidth() - sx);
        h = min(h, image.getHeight() - sy);

        int maxx = ((dx + (w / 2)) <= con.getWidth()) ? w : ((con.getWidth() - dx) * 2);
        int maxy = ((dy + (h / 2)) <= con.getHeight()) ? h : ((con.getHeight() - dy) * 2);
        // check that the image is not blitted outside the console
        if (!(dx + maxx / 2 >= 0 && dy + maxy / 2 >= 0 && dx < con.getWidth() && dy < con.getHeight()))
            return;
        maxx += sx;
        maxy += sy;

        int pixel;

        Color[] grid = {new Color(), new Color(), new Color(), new Color()};
        Color[] cols = {new Color(), new Color()};

        Color oldBg = con.getDefaultBackground();
        Color oldFg = con.getDefaultForeground();

        for (int cx = sx; cx < maxx; cx += 2) {
            for (int cy = sy; cy < maxy; cy += 2) {
            /* get the 2x2 super pixel colors from the image */
                int conx = dx + (cx - sx) / 2;
                int cony = dy + (cy - sy) / 2;
                Color consoleBack = con.getCharBackground(conx, cony);
                // grid[0]=TCOD_image_get_pixel(image,cx,cy);
                grid[0].set(consoleBack);
                grid[1].set(consoleBack);
                grid[2].set(consoleBack);
                grid[3].set(consoleBack);
                if (((pixel = image.getPixel(cx, cy)) & 0xff) != 0)
                    Color.rgba8888ToColor(grid[0], pixel);

                if (cx < maxx - 1 && (((pixel = image.getPixel(cx + 1, cy)) & 0xff) != 0))
                    Color.rgba8888ToColor(grid[1], pixel);

                if (cy < maxy - 1 && (((pixel = image.getPixel(cx, cy + 1)) & 0xff) != 0))
                    Color.rgba8888ToColor(grid[2], pixel);

                if (cy < maxy - 1 && cy < maxy - 1 && (((pixel = image.getPixel(cx + 1, cy + 1)) & 0xff) != 0))
                    Color.rgba8888ToColor(grid[3], pixel);

                int ascii = getPattern(grid, cols);
                if (ascii == ' ') {
                    // single color
                    con.setCharBackground(conx, cony, cols[0], BlendMode.Set);
                    con.setChar(conx, cony, ' ');
                } else {
                    if (ascii >= 0) {
                        con.setDefaultBackground(cols[0]);
                        con.setDefaultForeground(cols[1]);
                        con.putChar(conx, cony, (char) ascii, BlendMode.Set);

                    } else {
                        // negative ascii code means we need to invert back/fore colors
                        con.setDefaultBackground(cols[1]);
                        con.setDefaultForeground(cols[0]);
                        con.putChar(conx, cony, (char) -ascii, BlendMode.Set);
                    }
                }
            }
        }
        con.setDefaultBackground(oldBg);
        con.setDefaultForeground(oldFg);
    }
}
