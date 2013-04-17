package net.fishbulb.jcod.demo;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public final class DemoDesktop {

    public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
        config.width = 800;
        config.height = 600;
        config.vSyncEnabled = false;
        config.title = "JCOD Demo";
        new LwjglApplication(new Demo(), config);
	}
}
