package com.mediocrefireworks.realracer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mediocrefireworks.realracer.RealRacer;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 450;
        config.width = 800;
        new LwjglApplication(new RealRacer(), config);
    }
}
