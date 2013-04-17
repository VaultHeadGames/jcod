package net.fishbulb.jcod.demo;

import lombok.Getter;
import net.fishbulb.jcod.Console;

public abstract class DemoApplet {

    public static final int DEFAULT_WIDTH = 46;
    public static final int DEFAULT_HEIGHT = 20;

    @Getter
    int width;

    @Getter
    int height;

    @Getter
    Console console;

    @Getter
    Console parent;

    public DemoApplet(Console parent) {
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        this.parent = parent;
        console = new Console(width, height);
    }


    public abstract void update();

    public boolean keyDown(int keyCode) { return false; }

}
