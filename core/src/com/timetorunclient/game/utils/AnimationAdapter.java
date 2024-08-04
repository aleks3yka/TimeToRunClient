package com.timetorunclient.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationAdapter{
    float timeNow;
    Sprite output;
    boolean running;
    Animation<TextureAtlas.AtlasRegion> frames;

    public AnimationAdapter(Array<TextureAtlas.AtlasRegion> frames, float frameDuration, Animation.PlayMode mode){
        this.running = false;
        this.output = new Sprite();
        this.frames = new Animation<>(frameDuration, frames,
                mode);
    }

    public AnimationAdapter(Array<TextureAtlas.AtlasRegion> frames, float frameDuration){
        this(frames, frameDuration, Animation.PlayMode.LOOP);
    }

    public void start(boolean continuing){
        if(running){
            return;
        }
        if(!continuing) timeNow = 0;
        running = true;
    }
    public void start(){
        start(false);
    }

    public void stop(){
        if(!running){
            return;
        }
        running = false;
    }

    public Sprite getOutput() {
        return output;
    }

    public void setOutput(Sprite output) {
        this.output = output;
    }

    public void update(float dt){
        if(!running){
            return;
        }
        timeNow += dt;
        if(timeNow >= frames.getAnimationDuration()
                && frames.getPlayMode() != Animation.PlayMode.NORMAL
                && frames.getPlayMode() != Animation.PlayMode.REVERSED)
            timeNow -= frames.getAnimationDuration();
        output.setRegion(frames.getKeyFrame(timeNow));
    }
}
