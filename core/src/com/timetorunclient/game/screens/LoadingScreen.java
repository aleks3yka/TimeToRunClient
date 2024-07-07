package com.timetorunclient.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.timetorunclient.game.TimeToRun;
import com.timetorunclient.game.ui.ProgressBar;

public class LoadingScreen extends ScreenAdapter {
    ExtendViewport viewport;
    ProgressBar progressBar;
    TimeToRun game;
    ScreenAdapter nextScreen;
    TextureAtlas atlas;
    float timer;
    private static final float minDuration = 2f;
    public LoadingScreen(TimeToRun game, ScreenAdapter nextScreen){
        this.game = game;
        this.nextScreen = nextScreen;
        this.timer = 0;
        viewport = new ExtendViewport(20, 9);
        atlas = game.assets.get("progressBar.atlas", TextureAtlas.class);
        progressBar = new ProgressBar(0, 0, 10, atlas.createSprite("progressBar"),
                atlas.createSprite("knob"),
                atlas.createSprite("afterknob"),
                atlas.createSprite("beforeknob"));
    }

    @Override
    public void render(float delta) {
        game.assets.update();
        if(timer < minDuration) {
            timer += delta;
        }
        if(timer > minDuration){
            timer = minDuration;
        }


        ScreenUtils.clear(1,1,1, 1);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();

        viewport.apply();

        progressBar.setValue(game.assets.getProgress() * (timer/minDuration));
        progressBar.draw(game.batch);

        game.batch.end();

        if(game.assets.getProgress() * (timer/minDuration) == 1){
            game.setScreen(nextScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide(){
        dispose();
    }

    @Override
    public void dispose() {
        atlas.dispose();
        progressBar.dispose();
    }
}
