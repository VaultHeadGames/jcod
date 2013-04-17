package net.fishbulb.jcod.util;

import static net.fishbulb.jcod.util.MathUtils.frac;
import static net.fishbulb.jcod.util.MathUtils.invfrac;
import static net.fishbulb.jcod.util.MathUtils.trunc;

public enum PlotAlgorithms implements PlotAlgorithm {

    /**
     * Bresenham's line drawing algorithm
     * <p/>
     * Based on SquidLib by Eben Howard - http://squidpony.com - howard@squidpony.com
     */
    Bresenham {
        @Override
        public void apply(int x1, int y1, int x2, int y2, PlotFunction plot) {
            int xd, yd;
            int x, y;
            int ax, ay;
            int sx, sy;
            int dx, dy;

            dx = x2 - x1;
            dy = y2 - y1;

            ax = Math.abs(dx) << 1;
            ay = Math.abs(dy) << 1;

            sx = ((dx < 0) ? -1 : (dx > 0) ? 1 : 0);
            sy = ((dy < 0) ? -1 : (dy > 0) ? 1 : 0);

            x = x1;
            y = y1;

            if (ax >= ay) {
                yd = ay - (ax >> 1);
                while (true) {
                    if (!plot.apply(x, y, 1.0f)) return;
                    if (x == x2) return;

                    if (yd >= 0) {
                        y += sy;
                        yd -= ax;
                    }

                    x += sx;
                    yd += ay;
                }
            } else {
                xd = ax - (ay >> 1);
                while (true) {
                    if (!plot.apply(x, y, 1.0f)) return;
                    if (y == y2) return;

                    if (xd >= 0) {
                        x += sx;
                        xd -= ay;
                    }

                    y += sy;
                    xd += ax;
                }
            }
        }
    },

    /**
     * Based on work by Hugo Elias at
     * http://freespace.virgin.net/hugo.elias/graphics/x_wuline.htm
     * which itself is based on the algorithm designed by Xiaolin Wu
     * <p/>
     * Based on SquidLib by Eben Howard - http://squidpony.com - howard@squidpony.com
     */
    Elias {
        @SuppressWarnings({"UnusedDeclaration", "SuspiciousNameCombination"})
        public void apply(int startx, int starty, int endx, int endy, PlotFunction plot) {
            float x1 = startx, y1 = starty, x2 = endx, y2 = endy;
            float grad, xd, yd, length, xm, ym, xgap, ygap, xend, yend, xf, yf, brightness1, brightness2;
            int x, y, ix1, ix2, iy1, iy2;
            boolean shallow = false;

            xd = x2 - x1;
            yd = y2 - y1;

            if (Math.abs(xd) > Math.abs(yd)) {
                shallow = true;
            }

            if (!shallow) {
                float temp = x1;
                x1 = y1;
                y1 = temp;
                temp = x2;
                x2 = y2;
                y2 = temp;
                xd = x2 - x1;
                yd = y2 - y1;
            }
            if (x1 > x2) {
                float temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
                xd = x2 - x1;
                yd = y2 - y1;
            }

            grad = yd / xd;

            //add the first end point
            xend = trunc(x1 + .5f);
            yend = y1 + grad * (xend - x1);

            xgap = invfrac(x1 + .5f);

            ix1 = (int) xend;
            iy1 = (int) yend;

            brightness1 = invfrac(yend) * xgap;
            brightness2 = frac(yend) * xgap;

            if (shallow) {
                if (!plot.apply(ix1, iy1, brightness1)) return;
                if (!plot.apply(ix1, iy1 + 1, brightness2)) return;
            } else {
                if (!plot.apply(iy1, ix1, brightness1)) return;
                if (!plot.apply(iy1 + 1, ix1, brightness2)) return;
            }

            yf = yend + grad;

            //add the second end point
            xend = trunc(x2 + .5f);
            yend = y2 + grad * (xend - x2);

            xgap = invfrac(x2 - .5f);

            ix2 = (int) xend;
            iy2 = (int) yend;

            brightness1 = invfrac(yend) * xgap;
            brightness2 = frac(yend) * xgap;

            if (shallow) {
                if (!plot.apply(ix2, iy2, brightness1)) return;
                if (!plot.apply(ix2, iy2 + 1, brightness2)) return;
            } else {
                if (!plot.apply(iy2, ix2, brightness1)) return;
                if (!plot.apply(iy2 + 1, ix2, brightness2)) return;
            }

            //add the in-between points
            for (x = ix1 + 1; x < ix2; x++) {
                brightness1 = invfrac(yf);
                brightness2 = frac(yf);

                if (shallow) {
                    if (!plot.apply(x, (int) yf, brightness1)) return;
                    if (!plot.apply(x, (int) yf + 1, brightness2)) return;
                } else {
                    if (!plot.apply((int) yf, x, brightness1)) return;
                    if (!plot.apply((int) yf + 1, x, brightness2)) return;
                }

                yf += grad;
            }
        }
    };


//    public static void bresenham(int x1, int y1, int x2, int y2, PlotFunction plot) {
//        Bresenham.apply(x1, y1, x2, y2, plot);
//    }
//
//    public static void elias(int x1, int y1, int x2, int y2, PlotFunction plot) {
//        Elias.apply(x1, y1, x2, y2, plot);
//    }


}
