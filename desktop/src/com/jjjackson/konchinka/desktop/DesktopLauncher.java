package com.jjjackson.konchinka.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jjjackson.konchinka.KonchinkaGame;
import com.jjjackson.konchinka.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 480;
        config.height = 800;
		new LwjglApplication(new KonchinkaGame(), config);
	}
}
