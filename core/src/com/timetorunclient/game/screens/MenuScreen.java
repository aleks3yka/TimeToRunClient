package com.timetorunclient.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.timetorunclient.game.TimeToRun;
import com.timetorunclient.game.utils.AnimationAdapter;

public class MenuScreen extends ScreenAdapter {
    TimeToRun game;
    Texture img;
    AnimationAdapter<Sprite> animation;
    TextureAtlas animationAtlas;
    ExtendViewport viewport;

    public MenuScreen(TimeToRun game){
        viewport = new ExtendViewport(20, 9);
        this.game = game;
    }

    @Override
    public void show() {
        animationAtlas = game.assets.get("animation.atlas", TextureAtlas.class);
        animation = new AnimationAdapter<Sprite>(0.2f, animationAtlas.createSprites("anime"), Animation.PlayMode.LOOP);
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.4f, 0.4f, 0.4f, 1);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        viewport.apply();

        animation.update(delta);
        game.batch.draw(animation.getKeyFrame(), 0, 0, 4, 4);


        game.batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
