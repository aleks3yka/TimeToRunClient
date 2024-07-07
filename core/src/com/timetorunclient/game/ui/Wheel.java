package com.timetorunclient.game.ui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Wheel {
    WheelProcessor wheelProcessor;
    float x, y, r;
    float angel;
    Sprite wheel;
    public Wheel(float x, float y, float r, Viewport controlViewport, Sprite wheel){
        wheelProcessor = new WheelProcessor(this, controlViewport);
        angel = 0;
        this.wheel = wheel;
        this.x = x;
        this.y = y;
        this.r = r;
        wheel.setSize(r*2, r*2);
        wheel.setOriginCenter();
        wheel.setOriginBasedPosition(x, y);
    }
    public void draw(SpriteBatch batch){
        wheel.draw(batch);
    }
    public InputProcessor getWheelProcessor(){
        return wheelProcessor;
    }

    private class WheelProcessor extends InputAdapter {
        Wheel wheel;
        int pointer;
        float prevAngel;

        Viewport controlViewport;
        private WheelProcessor(Wheel wheel, Viewport controlViewport){
            this.wheel = wheel;
            this.pointer = -1;
            this.controlViewport = controlViewport;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if(this.pointer == -1 || this.pointer != pointer){
                return false;
            }
            Vector2 vector = controlViewport.unproject(new Vector2(screenX, screenY));
            vector.add(-wheel.x, -wheel.y);

            float nowAngel = vector.angleDeg();
            float delta = nowAngel - prevAngel;
            if(delta >= 180){
                delta -= 360;
            }
            if(delta <= -180){
                delta += 360;
            }
            wheel.wheel.rotate(delta);
            wheel.angel += delta;
            prevAngel = nowAngel;

            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(pointer == this.pointer){
                this.pointer = -1;
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(this.pointer != -1){
                return false;
            }
            Vector2 vector = controlViewport.unproject(new Vector2(screenX, screenY));
            vector.add(-wheel.x, -wheel.y);

            if(vector.len2() > wheel.r * wheel.r){
                return false;
            }
            this.pointer = pointer;
            prevAngel = vector.angleDeg();
            return true;
        }

    }
}
