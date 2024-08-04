package com.timetorunclient.game.entities;


import com.timetorunclient.game.space.Graph;

import java.util.ArrayList;

public class Entity {
    int now, to;
    long beginMoving;
    float speed, pos;
    Graph graph;
    public ArrayList<String> spriteName;
    int spriteIndex;

    //0 -- idling
    //1 -- moving

    public int getSpriteIndex() {
        return spriteIndex;
    }

    public Entity(int vert, float speed, String spriteIdling, String spriteGoing){
        this.now = vert;
        this.spriteName = new ArrayList<>(2);
        this.spriteName.add(spriteIdling);
        this.spriteName.add(spriteGoing);
        this.speed = speed;
        this.to = vert;
        this.beginMoving = -1;
        this.spriteIndex = 0;
    }

    public void act(float dt){
        if(beginMoving == -1) {
            spriteIndex = 0;
            return;
        }
        spriteIndex = 1;
        pos += speed * dt;
        if(pos >= 1.0){
            beginMoving = -1;
            now = to;
            spriteIndex = 0;
        }
    }
    public void move(int to, long time){
        if(beginMoving != -1){
            return;
        }
        beginMoving = time;
        this.to = to;
        pos = 0.0f;
    }

    public boolean touched(Entity other){
        if(other.pos == this.pos && other.now == this.now){
            return true;
        }else if(this.beginMoving == -1){
            return false;
        }else if(other.now == this.now && other.to == this.to &&
                (
                        (this.pos < other.pos && this.beginMoving > other.beginMoving)
                        || (this.pos > other.pos && this.beginMoving < other.beginMoving)
                )
        )
        {
            return true;
        }else if(other.now == this.to && other.to == this.now && this.pos + other.pos >= 1.0){
            return true;
        }else{
            return false;
        }
    }

    public int getVertex(){
        return now;
    }
    public long getBeginMoving(){
        return beginMoving;
    }
    public int getDestination(){
        return to;
    }
    public float getPos(){
        return pos;
    }

}
