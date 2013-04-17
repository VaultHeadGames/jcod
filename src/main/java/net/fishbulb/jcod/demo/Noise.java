package net.fishbulb.jcod.demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import net.fishbulb.jcod.Console;
import net.fishbulb.jcod.util.ImageUtils;
import toxi.math.noise.PerlinNoise;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.fishbulb.jcod.util.BlendMode.None;
import static net.fishbulb.jcod.util.BlendMode.Set;

public class Noise extends DemoApplet {

    Color noiseColor = new Color(0.5f, 0.5f, 1.0f, 1.0f); // light blue
    Color currentColor = new Color(1, 1, 1, 1);
    private long lastUpdate;

    float dx = 0;
    float dy = 0;

    float zoom = 1;

    private PerlinNoise noise;

    private Pixmap img;

    public Noise(Console parent) {
        super(parent);
        console.setDefaultForeground(Color.WHITE);
        console.setBlendFunction(Set);
        noise = new PerlinNoise();
        img = new Pixmap(console.getWidth() * 2, console.getHeight() * 2, Pixmap.Format.RGBA8888);
    }

    @Override public void update() {
        long now = System.currentTimeMillis();
        long updateMillis = 30;
        dx += 0.0003f;
        dy += 0.0003f;

        if ((now - lastUpdate) < updateMillis) return;
        lastUpdate = now;
        console.setBlendFunction(Set);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                float xx = (float) x * zoom / width + dx;
                float yy = (float) y * zoom / height + dy;
                float val = noise.noise(xx, yy);
                currentColor.set(noiseColor);
                currentColor.mul(val);
                img.drawPixel(x, y, Color.rgba8888(currentColor));
            }
        }
        ImageUtils.imageBlit2x(img, console, 0, 0, 0, 0, img.getWidth(), img.getHeight());
        console.setBlendFunction(None);
        console.setDefaultForeground(Color.WHITE);
        console.print(0, 0, String.format("zoom: %.1f    ", zoom));
    }


    @Override public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.PLUS:
                zoom += 0.1f;
                zoom = min(10, zoom);
                return true;
            case Input.Keys.MINUS:
                zoom -= 0.1f;
                zoom = max(0.1f, zoom);
                return true;
            default:
                return false;
        }
    }
}
