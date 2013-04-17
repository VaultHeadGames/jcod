package net.fishbulb.jcod.fov;

import static net.fishbulb.jcod.util.PlotAlgorithms.Bresenham;

/**
 * A Bresenham-based line-of-sight algorithm.
 *
 * Adapted from SquidPony implementation by Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class BresenhamLOS implements LOSSolver {
    @Override
    public boolean isReachable(float[][] resistanceMap, int startx, int starty, int targetx, int targety, float force, float decay, RadiusStrategy radiusStrategy) {
        BasicLOSFunction solver = new BasicLOSFunction(resistanceMap, startx, starty, force, decay, radiusStrategy);
        Bresenham.apply(startx, starty, targetx, targety, solver);
        return solver.isReachable();
    }

    @Override
    public boolean isReachable(float[][] resistanceMap, int startx, int starty, int targetx, int targety) {
        return isReachable(resistanceMap, startx, starty, targetx, targety, Float.MAX_VALUE, 0f, BasicRadiusStrategy.CIRCLE);
    }
}
