package com.timetorunclient.game.utils;

import com.badlogic.gdx.math.MathUtils;

public class CosEasingFunction implements EasingFunction{
    float a, b, c;
    public CosEasingFunction(float x1, float x2, float time){
        this.a = (x1 + x2)/2;
        this.b = -(x2 - x1)/2;
        this.c = MathUtils.PI/time;
    }
    public float getValue(float time){
        return this.a + this.b * MathUtils.cos(time * this.c);
    }
}
