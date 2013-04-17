package net.fishbulb.jcod.util;

import com.badlogic.gdx.graphics.Color;

// A note on alpha:
//
// Every one of these blend modes ignores alpha completely, except for the ones that have "Alpha" in the name, which
// use only source alpha .  Destination alpha is never used and always preserved, except for the default Set mode,
// which replaces the destination alpha with the source.  The reasoning behind this is partly to keep compatibility with
// TCOD, and also because there isn't really a consistent treatment of destination alpha that would also play nice with
// blending for the second time in OpenGL.  So we do color manipulation here and "real" blending with OpenGL later.

public interface BlendFunction {

    /**
     * Inputs that are not clamped with their rgba components between 0 and 1 will produce undefined output.
     * <p/>
     * When implementing a blend mode, the blend method MUST NOT be destructive of either source or dest, unless either
     * color is also used as the output (i.e. "aliased"), in which case the blend function MUST still produce the same
     * color as if output was not aliased to either input.<p/>
     *
     * @param source - The new "top" color
     * @param dest   - The existing "bottom" color
     * @param output - Output parameter holding result of the blend
     */
    public void blend(Color source, Color dest, Color output);
}

