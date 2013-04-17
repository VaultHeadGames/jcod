package net.fishbulb.jcod.fov;

import com.google.common.annotations.Beta;
import net.fishbulb.jcod.util.Elias;

import java.awt.Point;
import java.util.List;

/**
 * A first stab at a "fuzzy" LOS using Hugo Elias's modification of Wu's algorithm.
 * <p/>
 * Portions of this code are from SquidLib
 *
 * @deprecated JCOD port of EliasLOS is seriously busted at the moment
 */
@Beta
@Deprecated
public class EliasLOS implements LOSSolver {
    private float sideview = 0.75f;

    private BresenhamLOS los = new BresenhamLOS();

    public EliasLOS() {
    }

    public EliasLOS(float sideview) {
        this.sideview = sideview;
    }

    @Override
    public boolean isReachable(float[][] resistanceMap, int startx, int starty, int targetx, int targety, float force, float decay, RadiusStrategy radiusStrategy) {
        List<Point> path = Elias.line(startx, starty, targetx, targety);

        // XXX HMM this looks seriously wrong to me
        float checkRadius = radiusStrategy.radius(startx, starty) * sideview;

        while (!path.isEmpty()) {
            Point p = path.remove(0);

            //if a non-solid midpoint on the path can see both the start and end, consider the two ends to be able to see each other
            if (resistanceMap[p.x][p.y] < 1
                    && radiusStrategy.radius(startx, starty, p.x, p.y) < checkRadius
                    && los.isReachable(resistanceMap, p.x, p.y, targetx, targety, force - (radiusStrategy.radius(startx, starty, p.x, p.y) * decay), decay, radiusStrategy)
                    && los.isReachable(resistanceMap, startx, starty, p.x, p.y, force, decay, radiusStrategy)) {

                return true;
            }
        }
        return false;//never got to the target point
    }

    @Override
    public boolean isReachable(float[][] resistanceMap, int startx, int starty, int targetx, int targety) {
        return isReachable(resistanceMap, startx, starty, targetx, targety, Float.MAX_VALUE, 0f, BasicRadiusStrategy.CIRCLE);
    }

}
