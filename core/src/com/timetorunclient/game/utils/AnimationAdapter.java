package com.timetorunclient.game.utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

public class AnimationAdapter<T> extends Animation<T> {
    float timeNow;

    @SafeVarargs
    public AnimationAdapter(float frameDuration, T... keyFrames) {
        super(frameDuration, keyFrames);
        this.timeNow = 0;
    }

    public AnimationAdapter(float frameDuration, Array<? extends T> keyFrames, PlayMode playMode) {
        super(frameDuration, keyFrames, playMode);
        this.timeNow = 0;
    }

    public AnimationAdapter(float frameDuration, Array<? extends T> keyFrames) {
        super(frameDuration, keyFrames);
        this.timeNow = 0;
    }

    public void update(float dt){
        timeNow += dt;
        if(timeNow >= super.getAnimationDuration()) timeNow -= super.getAnimationDuration();
    }
    public T getKeyFrame(){
        return super.getKeyFrame(timeNow);
    }
    public T getKeyFrame(boolean looping){
        return super.getKeyFrame(timeNow, looping);
    }
}
