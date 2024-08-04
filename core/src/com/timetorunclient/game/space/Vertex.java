package com.timetorunclient.game.space;

public class Vertex {
    public String spriteName;
    private static final int spriteIndex = 0;

    public int getSpriteIndex() {
        return spriteIndex;
    }

    public Vertex(String spriteName){
        this.spriteName = spriteName;
    }
}
