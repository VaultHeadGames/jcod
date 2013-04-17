package net.fishbulb.jcod.fov;

import lombok.Getter;
import net.fishbulb.jcod.util.PlotFunction;

class BasicLOSFunction implements PlotFunction {
    private float[][] resistanceMap;
    private final int startx;
    private final int starty;
    float currentForce;
    private final float decay;
    private final RadiusStrategy radiusStrategy;

    @Getter
    boolean reachable = true;

    BasicLOSFunction(float[][] resistanceMap, int startx, int starty, float currentForce, float decay, RadiusStrategy radiusStrategy) {
        this.resistanceMap = resistanceMap;
        this.startx = startx;
        this.starty = starty;
        this.currentForce = currentForce;
        this.decay = decay;
        this.radiusStrategy = radiusStrategy;
    }

    @Override
    public boolean apply(int x, int y, float val) {
        if (x != startx || y != starty) {
            currentForce *= (1 - resistanceMap[x][y]);
        }
        double radius = radiusStrategy.radius(startx, starty, x, y);
        reachable = currentForce - (radius * decay) > 0;
        return reachable;
    }
}
