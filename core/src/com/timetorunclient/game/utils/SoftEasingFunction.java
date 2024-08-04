package com.timetorunclient.game.utils;

import com.badlogic.gdx.math.MathUtils;

public class SoftEasingFunction implements EasingFunction{
    float a1, b1, d, a2, b2, c2, x1, t, v1, v2, e1, e2;
    //a1*x+b1*cos(x*d)+x1
    //a2*x+b2*cos(x*d)+c2+x1

    //e1*(1 - cos(x * d)) + v1
    //e2*(1 - cos(x * d)) + v2
    public SoftEasingFunction(float x1, float x2, float v1, float v2, float t){
        this.x1 = x1;
        float s = x2 - x1;
        float vm = 2*(s)/t - (v1+v2)/2;
        this.a1 = (vm+v1)/2;
        this.a2 = (vm+v2)/2;
        this.e1 = (vm - v1) / 2;
        this.e2 = (vm - v2) / 2;
        this.b1 = -t * e1 / MathUtils.PI2;
        this.b2 = -t * e2 / MathUtils.PI2;
        this.d = MathUtils.PI2/t;
        this.c2 = - t * (v2 - v1) / 4;
        this.t = t;
        this.v1 = v1;
        this.v2 = v2;
    }

    public SoftEasingFunction(float x1, float x2, float t){
        this(x1, x2, 0, 0, t);
    }

    public SoftEasingFunction(float timePassed, SoftEasingFunction prev, float x2, float v2, float t){
        this(prev.getValue(timePassed), x2, prev.getSpeed(timePassed), v2, t);
    }
    public SoftEasingFunction(float timePassed, SoftEasingFunction prev, float x2, float t){
        this(timePassed, prev, x2, 0, t);
    }

    public float getValue(float now){
        if(now > t/2){
            return a2*now+b2*MathUtils.sin(now*d)+c2+x1;
        }
        return a1*now+b1*MathUtils.sin(now*d)+x1;
    }
    public float getSpeed(float now){
        if(now > t/2){
            return e2*(1 - MathUtils.cos(now * d)) + v2;
        }
        return e1*(1 - MathUtils.cos(now * d)) + v1;
    }
}
