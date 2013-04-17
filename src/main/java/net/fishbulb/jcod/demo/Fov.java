package net.fishbulb.jcod.demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import net.fishbulb.jcod.Console;
import net.fishbulb.jcod.util.BlendMode;
import net.fishbulb.jcod.util.CharCodes;

public class Fov extends DemoApplet {
    String[] map = new String[]{
            "##############################################",
            "#######################      #################",
            "#####################    #     ###############",
            "######################  ###        ###########",
            "##################      #####             ####",
            "################       ########    ###### ####",
            "###############      #################### ####",
            "################    ######                  ##",
            "########   #######  ######   #     #     #  ##",
            "########   ######      ###                  ##",
            "########                                    ##",
            "####       ######      ###   #     #     #  ##",
            "#### ###   ########## ####                  ##",
            "#### ###   ##########   ###########=##########",
            "#### ##################   #####          #####",
            "#### ###             #### #####          #####",
            "####           #     ####                #####",
            "########       #     #### #####          #####",
            "########       #####      ####################",
            "##############################################",
    };

    int torchRadius = 10;
    boolean flicker = false;
    boolean recompute = true;
    boolean lightWalls = true;

    Color darkWall = new Color(0, 0, 0.35f, 1f);
    Color lightWall = new Color(0.5f, 0.4f, 0.2f, 1f);
    Color darkGround = new Color(0.2f, 0.2f, 0.6f, 1);
    Color lightGround = new Color(0.8f, 0.7f, 0.2f, 1f);

    int px = 20;
    int py = 10;

    private long lastUpdate;

    public Fov(Console parent) {
        super(parent);
        initFov();
    }

    private void initFov() {
    }

    @Override public void update() {
        long now = System.currentTimeMillis();
        long updateMillis = 30;
        if ((now - lastUpdate) < updateMillis) return;
        lastUpdate = now;

        console.clear();
        console.setDefaultForeground(Color.WHITE);
        console.print(1, 0, "WASD: Move around");
        console.print(1, 1, "L: Light Walls");
        console.print(1, 2, "T: Torch (not yet)");
        console.print(1, 3, "X: Algorithm");

        console.setDefaultForeground(Color.BLACK);

        console.putChar(px, py, '@', BlendMode.None);
//        algo.computeFov(px, py, 0, lightWalls);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = map[y].charAt(x);
//                boolean lit = light.isFov(x, y);
                boolean lit = false;
                switch (c) {
                    case '=':
                        console.putChar(x, y, CharCodes.OEM.DHLINE, BlendMode.None);
                    case ' ':
                        console.setCharBackground(x, y, lit ? lightGround : darkGround);
                        break;
                    case '#':
                        console.setCharBackground(x, y, lit ? lightWall : darkWall);
                        break;
                }
            }
        }
    }


    private void moveTo(int x, int y) {
        if (map[y].charAt(x) == ' ') {
            console.putChar(px, py, ' ', BlendMode.None);
            px = x;
            py = y;
            console.putChar(px, py, '@', BlendMode.None);
        }
    }

    @Override public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.W:
                moveTo(px, py - 1);
                return true;
            case Input.Keys.A:
                moveTo(px - 1, py);
                return true;
            case Input.Keys.S:
                moveTo(px, py + 1);
                return true;
            case Input.Keys.D:
                moveTo(px + 1, py);
                return true;
            case Input.Keys.T:
                // TODO: torch effect
            case Input.Keys.L:
                lightWalls = !lightWalls;
                return true;
        }
        return false;
    }
}
