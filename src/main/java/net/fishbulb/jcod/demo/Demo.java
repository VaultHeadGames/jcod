package net.fishbulb.jcod.demo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.common.collect.Iterators;
import net.fishbulb.jcod.Console;
import net.fishbulb.jcod.display.ImageTileset;
import net.fishbulb.jcod.display.TileDisplay;
import net.fishbulb.jcod.display.Tileset;
import net.fishbulb.jcod.util.ImageUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Demo extends InputAdapter implements ApplicationListener {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;

    public static final int APPLET_X = 20;
    public static final int APPLET_Y = 10;

    public static final int TILE_WIDTH = 10;
    public static final int TILE_HEIGHT = 10;

    Stage stage;

    Console root;

    DemoApplet active;

    List<Class<? extends DemoApplet>> allApplets = new ArrayList<Class<? extends DemoApplet>>() {{
        add(Lines.class);
        add(Fov.class);
        add(Colors.class);
        add(Offscreen.class);
        add(Noise.class);
        add(Image.class);
    }};

    Iterator<Class<? extends DemoApplet>> applets = Iterators.cycle(allApplets);

    @SuppressWarnings("rawtypes")
    private DemoApplet createApplet(Class<? extends DemoApplet> klass) {
        try {
            Constructor ctor = klass.getDeclaredConstructor(Console.class);
            ctor.setAccessible(true);
            return (DemoApplet) ctor.newInstance(root);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void cycleApplet() {
        active = createApplet(applets.next());
    }

    @Override
    public void create() {
        Gdx.input.setInputProcessor(this);

        stage = new Stage();
        stage.getRoot().setTransform(false);    // minor optimization

        // Jump through some hoops to load grayscale fonts.  I might add a util for this later
        Pixmap pm = new Pixmap(Gdx.files.internal("tiles/consolas10x10_gs_tc.png"));
        Pixmap pm2 = ImageUtils.grayScaleToAlpha(pm);
        pm.dispose();       // dispose of old pixmaps or they will leak
        Texture tiles = new Texture(pm2);
        Tileset tileset = new ImageTileset(tiles, TILE_WIDTH, TILE_HEIGHT, ImageTileset.Layout.TCOD);
        TileDisplay display = new TileDisplay(WIDTH, HEIGHT, TILE_WIDTH, TILE_HEIGHT);  // doesn't have to match!
        root = new Console(WIDTH, HEIGHT, tileset, display);

        stage.addActor(root.getDisplay());

        cycleApplet();

        float xpos = (Gdx.app.getGraphics().getWidth() - (display.getPrefWidth() + APPLET_X + TILE_WIDTH)) / 2;
        float ypos = (Gdx.app.getGraphics().getHeight() - (display.getPrefHeight() + APPLET_Y + TILE_HEIGHT)) / 2;

        // drop down from top
//        display.addAction(Actions.moveTo(xpos, Gdx.app.getGraphics().getHeight() + root.getHeight()));
//        display.addAction(Actions.moveTo(xpos, ypos, 1));
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        active.update();

        // blitting the applet console is a really slow operation compared to directly rendering.
        // I don't feel like changing it at the moment
        Console ac = active.getConsole();
        ac.blit(0, 0, ac.getWidth(), ac.getHeight(), root, APPLET_X, APPLET_Y, 1, 1);
        root.flush();

        root.print(APPLET_X, APPLET_Y + active.getHeight() + 2, "Press space to switch between demos.");

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}

    @Override
    public boolean keyUp(int keyCode) {
        return true;
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Input.Keys.SPACE:
                cycleApplet();
                break;

        }

        return active.keyDown(keyCode);
    }

}
