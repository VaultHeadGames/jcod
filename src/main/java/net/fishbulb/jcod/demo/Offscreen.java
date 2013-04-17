package net.fishbulb.jcod.demo;

import net.fishbulb.jcod.Console;
import net.fishbulb.jcod.util.BlendMode;

public class Offscreen extends DemoApplet {

    Console snapshot;
    Console window;

    long lastUpdate = 0;
    long updateMillis = 500;   // every half second

    int x = 0;
    int y = 0;
    int xdir = 1;
    int ydir = 1;


    public Offscreen(Console parent) {
        super(parent);
        snapshot = new Console(width, height);
        parent.blit(Demo.APPLET_X, Demo.APPLET_Y, width, height, snapshot, 0, 0, 1, 1);

        window = new Console(width / 2, height / 2);
        window.printFrame(0, 0, width / 2, height / 2, false, BlendMode.Set, "Offscreen Console");
        window.printRectEx(width / 4, 2, width / 2 - 2, height / 2,
                BlendMode.None, Console.Alignment.Center,
                "You can render to an offscreen console and blit in on another one, simulating alpha transparency.");
    }

    @Override public void update() {
        long now = System.currentTimeMillis();
        if ((now - lastUpdate) < updateMillis) return;
        lastUpdate = now;

        console.print(0, 0, "" + now);
        x += xdir;
        y += ydir;

        if (x == width / 2 + 5)
            xdir = -1;
        else if (x == -5)
            xdir = 1;

        if (y == height / 2 + 5)
            ydir = -1;
        else if (y == -5)
            ydir = 1;

        // restore the initial screen
        snapshot.blit(0, 0, width, height, console, 0, 0, 1, 1);

        // blit the overlapping screen
        window.blit(0, 0, window.getWidth(), window.getHeight(), console, x, y, 1.0f, 0.75f);
    }
}
