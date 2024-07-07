package com.timetorunclient.game.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class ProgressBar implements Disposable {
    private Sprite frame, knob, afterKnob, beforeKnob;

    private float value;
    private float x, y, width, height, knobWidth;
    private int originalHeight, originalWidth;

    public ProgressBar(float x, float y, float width,
                       Sprite frame,
                       Sprite knob,
                       Sprite afterKnob,
                       Sprite beforeKnob
    ){
        this.x = x;
        this.y = y;
        this.width = width;

        this.frame = frame;
        this.knob = knob;
        this.afterKnob = afterKnob;
        this.beforeKnob = beforeKnob;


        originalHeight = frame.getRegionHeight();
        originalWidth = frame.getRegionWidth();
        height = width*originalHeight/originalWidth;

        frame.setCenter(0, 0);
        frame.setSize(width, height);
        frame.setPosition(x-width/2, y-height/2);

        knobWidth = height * knob.getRegionWidth() / knob.getRegionHeight();
        knob.setSize(knobWidth, height);
        setValue(0f);
    }
    public void draw(SpriteBatch batch){
        beforeKnob.draw(batch);
        afterKnob.draw(batch);
        knob.draw(batch);
        frame.draw(batch);
    }
    public void setValue(float value){
        this.value = value;

        knob.setPosition(x-width/2 + value * (width-knobWidth), y - height/2);
        beforeKnob.setBounds(x-width/2, y - height/2, knob.getX() - x + width/2, height);
        afterKnob.setBounds(knob.getX()+knobWidth, y - height/2, x + width/2 - knob.getX() - knobWidth, height);
    }

    @Override
    public void dispose() {
        frame.getTexture().dispose();
        knob.getTexture().dispose();
        afterKnob.getTexture().dispose();
        beforeKnob.getTexture().dispose();
    }
}
