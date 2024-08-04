package com.timetorunclient.game.ui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Slider {
    SliderProcessor sliderProcessor;
    float value;
    Vector2 start, end, segment;
    Sprite slider, sliderKnob;
    public Slider(Viewport controlViewport, Sprite slider, Sprite sliderKnob, Vector2 start, Vector2 end, float sliderKnobWidth){
        sliderProcessor = new SliderProcessor(this, controlViewport);
        this.slider = slider;
        this.sliderKnob = sliderKnob;
        this.start = start;
        this.end = end;
        this.segment = new Vector2(end).sub(start);

        this.sliderKnob.setSize(sliderKnobWidth, sliderKnobWidth * sliderKnob.getRegionHeight() / sliderKnob.getRegionWidth());
        this.slider.setSize(1, Math.abs(start.y - end.y) + sliderKnob.getHeight());
        this.slider.setSize(slider.getHeight() * slider.getRegionWidth() / slider.getRegionHeight(), slider.getHeight());
        this.slider.setOriginCenter();
        Vector2 middle = new Vector2(end.x/2, end.y/2);
        middle.mulAdd(start, 0.5f);
        this.slider.setOriginBasedPosition(middle.x, middle.y);
        this.sliderKnob.setOriginCenter();
        this.sliderKnob.setOriginBasedPosition(start.x, start.y);

        this.value = 0;
    }
    public InputProcessor getSliderProcessor(){
        return this.sliderProcessor;
    }
    public float getValue(){
        return Math.max(Math.min(this.value, 1), 0);
    }
    public void draw(SpriteBatch batch){
        slider.draw(batch);
        sliderKnob.draw(batch);
    }

    private class SliderProcessor extends InputAdapter{
        Viewport viewport;
        int pointer;
        Slider slider;
        float prevPosition;
        private SliderProcessor(Slider slider, Viewport viewport){
            this.viewport = viewport;
            this.pointer = -1;
            this.slider = slider;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(pointer != this.pointer){
                return false;
            }
            this.pointer = -1;
            this.slider.value = Math.max(Math.min(this.slider.value, 1), 0);
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if(this.pointer != pointer){
                return false;
            }

            Vector2 vector = viewport.unproject(new Vector2(screenX, screenY));
            vector.sub(this.slider.start);
            float newPosition = vector.dot(this.slider.segment)
                    / this.slider.segment.len2();
            this.slider.value += newPosition - this.prevPosition;


            vector.set(slider.start);
            vector.mulAdd(segment, Math.max(Math.min(this.slider.value, 1), 0));

            sliderKnob.setOriginBasedPosition(vector.x, vector.y);

            this.prevPosition = newPosition;
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(this.pointer != -1){
                return false;
            }
            Vector2 vector = viewport.unproject(new Vector2(screenX, screenY));
            if(vector.x - slider.sliderKnob.getX() >= 0
                    &&  vector.y - slider.sliderKnob.getY() >= 0
                    && vector.x - slider.sliderKnob.getX() <= slider.sliderKnob.getWidth()
                    && vector.y - slider.sliderKnob.getY() <= slider.sliderKnob.getHeight()){
                this.pointer = pointer;
                vector.sub(this.slider.start);
                this.prevPosition = vector.dot(this.slider.segment)
                        / this.slider.segment.len2();

                return true;
            }
            return false;
        }
    }
}
