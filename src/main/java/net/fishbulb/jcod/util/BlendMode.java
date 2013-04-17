package net.fishbulb.jcod.util;

import com.badlogic.gdx.graphics.Color;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.fishbulb.jcod.util.ColorUtils.lerp;

/**
 * Holds predefined blend modes including all TCOD blend modes plus a few extensions.
 * All blend functions preserve destination alpha except for Set, which uses source alpha.
 * This isn't necessarily an accurate model, but it's usually what you want.
 * <p/>
 * The order of these is pretty arbitrary, and in any 0.x release branch, subject to change to suit my tastes.
 */
public enum BlendMode implements BlendFunction {


    /** Ignores the dest color and uses the source color. Dest alpha is overwritten by source alpha. */
    Set { // TCOD_BKGND_SET

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.set(source);
        }
    },

    /** Ignores the source color and uses the dest color */
    None { // TCOD_BKGND_NONE

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.set(dest);
        }
    },

    /** Multiplies the source color by the dest color (darkening it) */
    Multiply { // TCOD_BKGND_MULTIPLY

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.set(dest);
            output.mul(source);
            output.a = dest.a;
        }
    },

    /** Uses the lighest RGB components of source or dest */
    Lighten { // TCOD_BKGND_LIGHTEN

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = max(source.r, dest.r);
            output.g = max(source.g, dest.g);
            output.b = max(source.b, dest.b);
            output.a = dest.a;
            output.clamp();
        }
    },

    /** Uses the darkest RGB components of source or dest */
    Darken { // TCOD_BKGND_DARKEN

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = min(source.r, dest.r);
            output.g = min(source.g, dest.g);
            output.b = min(source.b, dest.b);
            output.a = dest.a;
            output.clamp();
        }
    },

    /** Inverts source and dest colors, multiplies them, and inverts the result (brightening it) */
    Screen { // TCOD_BKGND_SCREEN

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = (1 - ((1 - dest.r) * (1 - source.r)));
            output.g = (1 - ((1 - dest.g) * (1 - source.g)));
            output.b = (1 - ((1 - dest.b) * (1 - source.b)));
            output.a = dest.a;
            output.clamp();
        }
    },

    /** Divide source by inverted dest, decreasing contrast and "washing" toward brighter source color */
    ColorDodge { // TCOD_BKGND_COLOR_DODGE

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = (dest.r == 1) ? 1 : source.r / (1 - dest.r);
            output.g = (dest.g == 1) ? 1 : source.g / (1 - dest.g);
            output.b = (dest.b == 1) ? 1 : source.b / (1 - dest.b);
            output.a = dest.a;
            output.clamp();
        }
    },

    /** Divides inverted dest by source, emphasizing the darker colors in dest */
    ColorBurn { // TCOD_BKGND_COLOR_BURN

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = (source.r == 0) ? 0 : 1 - (1 - dest.r) / source.r;
            output.g = (source.g == 0) ? 0 : 1 - (1 - dest.g) / source.g;
            output.b = (source.b == 0) ? 0 : 1 - (1 - dest.b) / source.b;
            output.a = dest.a;
            output.clamp();
        }
    },

    /** Adds the colors together, brightening it by the source, AKA Linear Dodge.  Does not respect alpha. */
    Add { // TCOD_BKGND_ADD

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.set(dest);
            output.add(source);
            output.a = dest.a;
        }
    },

    /** TCOD additive blending using alpha, equivalent to glBlendFunc(GL_SRC_ALPHA,GL_ONE) */
    AddAlpha { // TCOD_BKGND_ADDALPHA

        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = source.r * source.a + dest.r;
            output.g = source.g * source.a + dest.g;
            output.b = source.b * source.a + dest.b;
            output.a = dest.a;
            output.clamp();
        }
    },

    /** JCOD extension: "classic" alpha blending equivalent to glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA). */
    Alpha { // JCOD Extension

        // JCOD Extension
        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = source.r * source.a + dest.r * (1 - source.a);
            output.g = source.g * source.a + dest.g * (1 - source.a);
            output.b = source.b * source.a + dest.b * (1 - source.a);
            output.a = dest.a;
            output.clamp();
        }
    },

    /** TCOD's interpolative alpha blend.  Should technically be the same as Alpha but I havent proven it */
    AlphaLerp { // TCOD_BKGND_ALPH

        // *back = TCOD_color_lerp(*back,col,(float)(alpha/255.0f));
        @Override
        public void blend(Color source, Color dest, Color output) {
            lerp(dest, source, source.a, output);
            output.a = dest.a;
            output.clamp();
        }
    },

    /** Adds the colors together and and subtracts 1, darkening it similar to Multiply.  AKA Linear Burn */
    Burn { // TCOD_BKGND_BURN

        @Override
        public void blend(Color source, Color dest, Color output) {
            // can't use set/add/sub methods, they clamp too soon
            output.r = source.r + dest.r - 1;
            output.g = source.g + dest.g - 1;
            output.b = source.b + dest.b - 1;
            output.a = dest.a;
            output.clamp();
        }
    },

    /** Multiplies dark areas (making them darker), screens light areas (making them lighter) */
    Overlay { // TCOD_BKGND_OVERLAY

        // TCOD_BKGND_OVERLAY
        @Override
        public void blend(Color source, Color dest, Color output) {
            output.r = source.r <= 0.5 ? 2 * (source.r) * dest.r : 1 - 2 * (1 - source.r) * (1 - dest.r);
            output.g = source.g <= 0.5 ? 2 * (source.g) * dest.g : 1 - 2 * (1 - source.g) * (1 - dest.g);
            output.b = source.b <= 0.5 ? 2 * (source.b) * dest.b : 1 - 2 * (1 - source.b) * (1 - dest.b);
            output.a = dest.a;
            output.clamp();
        }
    };

    @Override
    abstract public void blend(Color source, Color dest, Color output);
}

