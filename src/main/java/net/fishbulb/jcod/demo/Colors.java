package net.fishbulb.jcod.demo;

import com.badlogic.gdx.graphics.Color;
import net.fishbulb.jcod.Console;
import net.fishbulb.jcod.util.BlendMode;

import java.util.Random;

import static net.fishbulb.jcod.util.ColorUtils.lerp;

public class Colors extends DemoApplet {

    int[] dirr = {1, -1, 1, 1};
    int[] dirg = {1, -1, -1, 1};
    int[] dirb = {1, 1, 1, -1};

    Color[] cols = new Color[]{
            new Color(0.19f, 0.15f, 0.58f, 1),
            new Color(0.94f, 0.33f, 0.01f, 1),
            new Color(0.19f, 0.13f, 0.94f, 1),
            new Color(0.03f, 0.78f, 0.50f, 1)
    }; // random corner colors

    Random rng = new Random();

    Color top = new Color();
    Color bottom = new Color();
    Color cur = new Color();

    public Colors(Console parent) {
        super(parent);
    }

    @Override
    public void update() {
        float delta = 0.002f;   // Much slower than the TCOD demo, so it's easier to look at
        // ==== slighty modify the corner colors ====
        for (int c = 0; c < 4; c++) {
            // move each corner color
            int component = rng.nextInt(3);
            switch (component) {
                case 0:
                    cols[c].r += delta * dirr[c];
                    if (cols[c].r >= 1) dirr[c] = -1;
                    else if (cols[c].r <= 0) dirr[c] = 1;
                    break;
                case 1:
                    cols[c].g += delta * dirg[c];
                    if (cols[c].g >= 1) dirg[c] = -1;
                    else if (cols[c].g <= 0) dirg[c] = 1;
                    break;
                case 2:
                    cols[c].b += delta * dirb[c];
                    if (cols[c].b >= 1) dirb[c] = -1;
                    else if (cols[c].b <= 0) dirb[c] = 1;
                    break;
            }
        }


        // ==== scan the whole screen, interpolating corner colors ====
        for (int x = 0; x < width; x++) {
            float xcoef = (float) (x) / (width - 1);
            // get the current column top and bottom colors
            lerp(cols[0], cols[1], xcoef, top);
            lerp(cols[2], cols[3], xcoef, bottom);
            for (int y = 0; y < height; y++) {
                float ycoef = (float) (y) / (height - 1);
                // get the current cell color
                lerp(top, bottom, ycoef, cur);
                console.setCharBackground(x, y, cur);
            }
        }

        // ==== print the text with a random color ====
        // get the background color at the text position
        Color textColor = console.getCharBackground(width / 2, 5).cpy();
        // and invert it (note the .cpy() above to avoid aliasing it to the cell's actual color)
        textColor.r = 1.0f - textColor.r;
        textColor.g = 1.0f - textColor.g;
        textColor.b = 1.0f - textColor.b;
        textColor.a = 1.0f;
        // put random text (for performance tests)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color col = console.getCharBackground(x, y);
                col = lerp(col, Color.BLACK, 0.5f);
                char c = (char) (rng.nextInt(('z' - 'a') + 1) + 'a');
                console.setDefaultForeground(col);
                console.putChar(x, y, c, BlendMode.None);
            }
        }

        console.setDefaultForeground(textColor);
        // the background behind the text is slightly darkened using the BKGND_MULTIPLY flag
        console.setDefaultBackground(Color.GRAY);
        console.printRectEx(width / 2, 5, width - 2, height - 1,
                BlendMode.Multiply, Console.Alignment.Center,
                "The JCOD port of the Doryen library uses 32-bit colors with true alpha blending");

    }


}
