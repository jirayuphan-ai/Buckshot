package com.gamedev.buckshot.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gamedev.buckshot.UIscreen.Main;

// แล้วส่งเกม (Main) ให้ LibGDX รัน 
public class Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Buckshot");
        config.setWindowedMode(1280, 720);
        config.setResizable(true);
        config.setForegroundFPS(100);
        new Lwjgl3Application(new Main(), config); // วนลูปเกมเชื่อมกับ Main
    }
}
