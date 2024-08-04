package com.timetorunclient.game.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

public class Style {
    ArrayList<AnimationAdapter> animations;
    int index;
    Sprite output;
    public Style(ArrayList<String> animationsNames, TextureAtlas atlas, float frameDuration){
        this.output = new Sprite();
        this.animations = new ArrayList<>(animationsNames.size());
        for(int i = 0; i < animationsNames.size(); i++){
            animations.add(new AnimationAdapter(atlas.findRegions(animationsNames.get(i)), frameDuration));
            animations.get(i).setOutput(output);
        }
        this.index = 0;
        animations.get(this.index).start();
    }

    public Style(String animationsName, TextureAtlas atlas, float frameDuration){
        ArrayList<String> array = new ArrayList<>(1);
        array.add(animationsName);
        this.output = new Sprite();
        this.animations = new ArrayList<>(array.size());
        for(int i = 0; i < array.size(); i++){
            animations.add(new AnimationAdapter(atlas.findRegions(array.get(i)), frameDuration));
            animations.get(i).setOutput(output);
        }
        this.index = 0;
        animations.get(this.index).start();
    }

    public void setAnimation(int index){
        if(index >= animations.size() || index < 0 || index == this.index) return;
        animations.get(this.index).stop();
        this.index = index;
        animations.get(this.index).start();
    }
    public void update(float deltaTime){
        animations.get(this.index).update(deltaTime);
    }

    public Sprite getOutput() {
        return output;
    }
}
