package com.timetorunclient.game.space;


import com.badlogic.gdx.utils.TimeUtils;

public class Edge {
    int weight;
    long expireTime;
    int from, to;
    public String spriteName;
    private static final int spriteIndex = 0;

    public int getSpriteIndex() {
        return spriteIndex;
    }

    public Edge(int a, int b, int weight, Boolean exist, String skin){
        this.from = a;
        this.to = b;
        this.weight = weight;
        expireTime = -1;
        this.spriteName = skin;
    }

    public void setWeight(int weight){
        setWeight(weight, -1);
    }

    public void setWeight(int weight, long expireTime){
        if(this.expireTime <= TimeUtils.millis()){
            this.weight = weight;
            this.expireTime = expireTime;
        }
    }

    public int getWeight(){
        return this.weight;
    }

    public int getTo(int v){
       return (v==to ? from : to);
    }

    public int getA(){
        return from;
    }
    public int getB(){
        return to;
    }
}
