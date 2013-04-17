package net.fishbulb.jcod.demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.google.common.collect.Iterators;
import net.fishbulb.jcod.Console;
import net.fishbulb.jcod.util.BlendMode;
import net.fishbulb.jcod.util.PlotAlgorithm;
import net.fishbulb.jcod.util.PlotAlgorithms;
import net.fishbulb.jcod.util.PlotFunction;

import java.text.MessageFormat;
import java.util.Iterator;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static net.fishbulb.jcod.util.BlendMode.None;
import static net.fishbulb.jcod.util.BlendMode.Set;

public class Lines extends DemoApplet {

    Color lineColor = new Color(0.25f, 0.25f, 1.0f, 1.0f); // light blue
    Color tmp = new Color();

    float currentAlpha = 1.0f;

    Iterator<BlendMode> modes = Iterators.cycle(BlendMode.values());
    BlendMode currentMode;

    Iterator<? extends PlotAlgorithm> algos = Iterators.cycle(PlotAlgorithms.values());
    PlotAlgorithm currentAlgo;

    PlotFunction listener = new PlotFunction() {
        @Override public boolean apply(int x, int y, float val) {
            tmp.set(lineColor);
            tmp.mul(val);
            getConsole().setCharBackground(x, y, tmp, currentMode);

            return true;
        }
    };

    Console backdrop;
    private long lastUpdate;

    private void cycleMode() {
        currentMode = modes.next();
    }

    private void cycleAlgo() {
        currentAlgo = algos.next();
    }

    public Lines(Console parent) {
        super(parent);
        console.setDefaultForeground(Color.WHITE);
        console.setBlendFunction(None);
        backdrop = new Console(width, height);
        Color col = new Color(1, 1, 1, currentAlpha);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                col.r = ((float) x / (width - 1));
                col.g = ((float) (x + y) / (width - 1 + height - 1));
                col.b = ((float) y / (height - 1));
                backdrop.setCharBackground(x, y, col, Set);
            }
        }
        cycleMode();
        cycleAlgo();
    }

    @Override public void update() {
        long now = System.currentTimeMillis();
        long updateMillis = 30;

        if ((now - lastUpdate) < updateMillis) return;
        lastUpdate = now;

        switch (currentMode) {
            case Alpha: case AddAlpha: case AlphaLerp:
                currentAlpha = (float) ((1.0 + cos((double) now / 1000 * 2)) / 2.0);
                break;
            default:
                currentAlpha = 1.0f;
        }

        // blit the background
        backdrop.blit(0, 0, width, height, console, 0, 0, 1, 1);

        // render the gradient
        int recty = (int) ((height - 2) * (1.0 + cos((double) now / 1000)) / 2.0);
        Color col = new Color(1, 1, 1, currentAlpha);
        for (int x = 0; x < width; x++) {
            col.r = (float) x / width;
            col.g = (float) x / width;
            col.b = (float) x / width;
            console.setCharBackground(x, recty, col, currentMode);
            console.setCharBackground(x, recty + 1, col, currentMode);
            console.setCharBackground(x, recty + 2, col, currentMode);
        }

        // calculate the segment ends
        double angle = (double) now / 1000 * 2.0f;
        double cosAngle = cos(angle);
        double sinAngle = sin(angle);
        int xo = (int) (width / 2 * (1 + cosAngle));
        int yo = (int) (height / 2 + sinAngle * width / 2);
        int xd = (int) (width / 2 * (1 - cosAngle));
        int yd = (int) (height / 2 - sinAngle * width / 2);

        // render the line
        lineColor.a = currentAlpha;
        currentAlgo.apply(xo, yo, xd, yd, listener);

        // print settings
        console.print(1, 1, MessageFormat.format("Blend: {0} (ENTER to change)", currentMode));
        console.print(1, 2, MessageFormat.format("Line : {0} (L to change)", currentAlgo));
    }


    @Override public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.ENTER:
                cycleMode();
                return true;
            case Input.Keys.L:
                cycleAlgo();
                return true;
        }
        return false;
    }
}
