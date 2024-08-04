package com.timetorunclient.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.timetorunclient.game.TimeToRun;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		System.out.println("Do not run");
//		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
//		config.setForegroundFPS(60);
//		config.setTitle("Time to run client");
//		config.setWindowedMode(800, 360);
//		new Lwjgl3Application(new TimeToRun(), config);
	}
}
