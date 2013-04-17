package net.fishbulb.jcod.util;

import com.badlogic.gdx.graphics.Color;

public final class ColorUtils {

    private ColorUtils() {}

    public static Color lerp(Color from, Color to, float coef, Color output) {
        output.r = from.r + (to.r - from.r) * coef;
        output.g = from.g + (to.g - from.g) * coef;
        output.b = from.b + (to.b - from.b) * coef;
        output.a = from.a;
        return output;
    }

    public static Color lerp(Color from, Color to, float coef) {
        return lerp(from, to, coef, new Color());
    }

    /**
     * Returns a float between [0,1] giving the Euclidian distance between two colors in RGB space (alpha is ignored).
     * Note that this is really only useful for colors that have similar hues already.  For something closer to what
     * human vision considers "color similarity", you'll want to look into the (very complex) CIEDE formulas.
     *
     * @param c1 first color
     * @param c2 second color
     * @return distance between c1 and c2
     */
    public static float rgbdist(Color c1, Color c2) {
        float dr = c1.r - c2.r;
        float dg = c1.g - c2.g;
        float db = c1.b - c2.b;
        return dr * dr + dg * dg + db * db;
    }

}
