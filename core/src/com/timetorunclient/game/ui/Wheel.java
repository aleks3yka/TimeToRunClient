package com.timetorunclient.game.ui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Wheel {
    WheelProcessor wheelProcessor;
    float x, y, r;
    float angel;
    Sprite wheel;
    boolean wheelTouched;

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
        this.wheelTouched = false;
    }

    public boolean isWheelTouched() {
        return wheelTouched;
    }

    public float getAngel() {
        return angel;
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
        Vector2 prevDragPoint;
        float border = 0;

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
            vector.sub(prevDragPoint);

            float delta = (vector.x / wheel.r)*MathUtils.radiansToDegrees;
            wheel.wheel.rotate(delta);
            wheel.angel += delta;
            prevDragPoint.add(vector);
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(pointer == this.pointer){
                this.pointer = -1;
                this.wheel.wheelTouched = false;
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

            if(vector.x < border){
                return false;
            }
            this.pointer = pointer;
            prevDragPoint = vector;
            this.wheel.wheelTouched = true;
            return true;
        }

    }
}
