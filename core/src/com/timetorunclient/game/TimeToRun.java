package com.timetorunclient.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.timetorunclient.game.screens.GameScreen;
import com.timetorunclient.game.screens.LoadingScreen;
import com.timetorunclient.game.screens.MenuScreen;

public class TimeToRun extends Game {
	public SpriteBatch batch;
	public AssetManager assets;
	MenuScreen menuScreen;
	GameScreen gameScreen;

	
	@Override
	public void create () {
		assets = new AssetManager();
		batch = new SpriteBatch();
		menuScreen = new MenuScreen(this);
		gameScreen = new GameScreen(this);
		assets.load("progressBar.atlas", TextureAtlas.class);


		while (!assets.update());
		//assets.load("animation.atlas", TextureAtlas.class);
		assets.load("controls.atlas", TextureAtlas.class);
		setScreen(new LoadingScreen(this, gameScreen));
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
