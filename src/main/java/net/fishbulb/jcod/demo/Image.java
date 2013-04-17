package net.fishbulb.jcod.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import net.fishbulb.jcod.Console;
import net.fishbulb.jcod.util.BlendMode;
import net.fishbulb.jcod.util.ImageUtils;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Image extends DemoApplet {
    private long lastUpdate;

    Pixmap circles;
    Pixmap skull;

    double rot = 0.0f;
    double xrot = 0.0f;
    double yrot = 0.0f;
    double sxrot = 0.0f;
    double syrot = 0.0f;

    public Image(Console parent) {
        super(parent);

        Pixmap tmp = new Pixmap(Gdx.files.internal("skull.png"));
        skull = tmp;
        skull = ImageUtils.grayScaleToAlpha(tmp);
        tmp.dispose();

        circles = new Pixmap(Gdx.files.internal("circle.png"));
    }

    @Override public void update() {
        long now = System.currentTimeMillis();
        long updateMillis = 30;
        if ((now - lastUpdate) < updateMillis) return;
        lastUpdate = now;

        rot = (rot + 0.1f) % (2 * Math.PI);
        xrot = (xrot + 0.07f) % (2 * Math.PI);
        yrot = (yrot + 0.04f) % (2 * Math.PI);
        sxrot = (yrot + 0.11f) % (2 * Math.PI);
        syrot = (yrot + 0.9f) % (2 * Math.PI);

        int x = (int) (console.getWidth() / 2 + cos(xrot) * 12);
        int y = (int) (console.getHeight() / 2 + sin(yrot) * 8);

        console.clear();

        long nowsecs = now / 1000;


        double scalex = (1 + cos(sxrot)) - 0.5;
        double scaley = (1 + sin(syrot)) - 0.5;

        ImageUtils.imageBlit(circles, console, circles.getWidth() / 2 + 10, circles.getHeight() / 2, BlendMode.Set, 1, 1f, 0);

        console.setBlendFunction(BlendMode.Alpha);
        ImageUtils.imageBlit(skull, console, x, y, BlendMode.Alpha, (float) scalex, (float)scaley, (float) rot);

    }
}
