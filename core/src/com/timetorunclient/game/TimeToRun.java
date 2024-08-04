package com.timetorunclient.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.timetorunclient.game.connection.Connection;
import com.timetorunclient.game.screens.GameScreen;
import com.timetorunclient.game.screens.HostScreen;
import com.timetorunclient.game.screens.LoadingScreen;
import com.timetorunclient.game.screens.MenuScreen;
import com.timetorunclient.game.screens.ServersScreen;
import com.timetorunclient.game.utils.ServerDiscovery;
import com.timetorunclient.game.utils.ServerRegistration;

public class TimeToRun extends Game {
	public SpriteBatch batch;
	public AssetManager assets;
	public Connection connection;
	MenuScreen menuScreen;
	GameScreen gameScreen;
	ServersScreen serversScreen;
	HostScreen hostScreen;
	public BitmapFont titleFont;
	public BitmapFont font;
	public final ServerDiscovery discovery;
	public final ServerRegistration registration;

	TimeToRun(ServerDiscovery discovery, ServerRegistration registration){
		this.discovery = discovery;
        this.registration = registration;
    }
	
	@Override
	public void create () {
		assets = new AssetManager();
		batch = new SpriteBatch();
		menuScreen = new MenuScreen(this);
		gameScreen = new GameScreen(this);
		serversScreen = new ServersScreen(this);
		hostScreen = new HostScreen(this);
		assets.load("progressBar.atlas", TextureAtlas.class);
		this.connection = new Connection();

		socialPrefs();

		//Create fonts

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("pixelFont.ttf"));
		//Title font
		FreeTypeFontGenerator.FreeTypeFontParameter params
				= new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.size = 72;
		titleFont = generator.generateFont(params);
		//Normal font

		params.size = 36;
		font = generator.generateFont(params);

		generator.dispose();

		while (!assets.update());
		//assets.load("animation.atlas", TextureAtlas.class);
//		assets.load("controls.atlas", TextureAtlas.class);
//		assets.load("game.atlas", TextureAtlas.class);
		setScreenAndLoadNeededResources(AppScreens.MENU);
	}

	public void setScreenAndLoadNeededResources(AppScreens screen){
		switch (screen){
			case GAME:
				setScreen(new LoadingScreen(this, gameScreen));
				assets.load("controls.atlas", TextureAtlas.class);
				assets.load("game.atlas", TextureAtlas.class);
				break;
			case MENU:
				setScreen(new LoadingScreen(this, menuScreen));
				assets.load("expeeui/expee-ui.json", Skin.class);
				break;
			case SERVERS:
				setScreen(new LoadingScreen(this, serversScreen));
				assets.load("expeeui/expee-ui.json", Skin.class);
				break;
			case HOST:
				setScreen(new LoadingScreen(this, hostScreen));
				assets.load("expeeui/expee-ui.json", Skin.class);
				break;
		}
	}

	public void socialPrefs(){
		Preferences socialPreferences = Gdx.app.getPreferences("social");
		if(!socialPreferences.contains("name")){
			socialPreferences.putString("name", "anon" + (int)(MathUtils.random() * 1000000));
			socialPreferences.flush();
		}
	}

	public enum AppScreens{
		MENU, GAME, SERVERS, HOST;
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
