package com.timetorunclient.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.timetorunclient.game.TimeToRun;
import com.timetorunclient.game.ui.Slider;
import com.timetorunclient.game.ui.Wheel;


public class GameScreen extends ScreenAdapter {
    TimeToRun game;
    ExtendViewport controlsPort;
    FitViewport gamePort;
    Wheel wheel;
    Slider slider;
    TextureAtlas controlsTextures;
    InputMultiplexer inputMultiplexer;
    InputProcessor prevInputProcessor;
    float mytime;

    public GameScreen(TimeToRun game){
        this.game = game;
        controlsPort = new ExtendViewport(20, 9);
        gamePort = new FitViewport(10, 10);

    }

    @Override
    public void show() {
        //change inputProcessor
        prevInputProcessor = Gdx.input.getInputProcessor();
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        //update viewports

        controlsPort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gamePort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //get atlas for controls

        controlsTextures = game.assets.get("controls.atlas", TextureAtlas.class);

        //create controls

        //create wheel and alive it
        wheel = new Wheel(controlsPort.getWorldWidth()*2/5,
                -controlsPort.getWorldHeight() * 5 / 18,
                1.5f,
                controlsPort,
                controlsTextures.createSprite("wheel"));
        inputMultiplexer.addProcessor(wheel.getWheelProcessor());

        //create slider and alive it
        Vector2 start = new Vector2(controlsPort.getWorldWidth()*11/40, -controlsPort.getWorldHeight() * 7 / 18);
        Vector2 end = new Vector2(controlsPort.getWorldWidth()*11/40, controlsPort.getWorldHeight() * 5 / 18);


        slider = new Slider(controlsPort,
                controlsTextures.createSprite("slider"),
                controlsTextures.createSprite("sliderKnob"),
                start, end, 1);

        inputMultiplexer.addProcessor(slider.getSliderProcessor());

        mytime = 0;
    }

    @Override
    public void resize(int width, int height) {
        controlsPort.update(width, height);
        gamePort.update(width, height);
    }

    @Override
    public void render(float delta) {
        //System.out.println(Gdx.graphics.getFramesPerSecond());

        //Draw
        //ScreenUtils.clear(0.345f, 0.592f, 1 ,1);
        mytime+=delta;
        ScreenUtils.clear(MathUtils.sin(mytime*MathUtils.PI2/15),
                MathUtils.sin(mytime*MathUtils.PI2/15 - MathUtils.PI2/3),
                MathUtils.sin(mytime*MathUtils.PI2/15 - 2 * MathUtils.PI2/3), 1);
        game.batch.begin();

        //Game part
        game.batch.setProjectionMatrix(gamePort.getCamera().combined);
        gamePort.apply();

        game.batch.draw(controlsTextures.createSprite("wheel"), -5, -5, 10, 10);

        //Control part
        game.batch.setProjectionMatrix(controlsPort.getCamera().combined);

        game.batch.enableBlending();
        controlsPort.apply();

        //draw controls
        wheel.draw(game.batch);
        slider.draw(game.batch);

        game.batch.disableBlending();
        game.batch.end();

        //Do network magic and update entities
    }

    @Override
    public void hide() {
        // returns input processor
        Gdx.input.setInputProcessor(prevInputProcessor);

        // frees memory from atlases

        controlsTextures.dispose();
        game.assets.unload("controls.atlas");
    }
}
