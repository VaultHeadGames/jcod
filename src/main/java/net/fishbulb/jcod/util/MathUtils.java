package net.fishbulb.jcod.util;

public class MathUtils {
    public static float trunc(float x) {
        if (x < 0) {
            return (float) Math.ceil(x);
        } else {
            return (float) Math.floor(x);
        }
    }

    public static float frac(float x) {
        return x - trunc(x);
    }

    public static float invfrac(float x) {
        return 1 - frac(x);
    }
}
