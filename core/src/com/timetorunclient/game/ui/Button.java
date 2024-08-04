package com.timetorunclient.game.ui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Button{
    ButtonProcessor processor;
    Sprite buttonUp;
    Sprite buttonDown;
    boolean state;
    boolean down;

    public Button(Viewport port, Sprite buttonUp, Sprite buttonDown, float x, float y, float r){
        this.buttonDown = buttonDown;
        this.buttonUp = buttonUp;
        buttonUp.setBounds(x-r, y-r, r*2, r*2);
        buttonDown.setBounds(x-r, y-r, r*2, r*2);
        this.state = false;
        this.down = false;
        this.processor = new ButtonProcessor(this, port);
    }

    public boolean getTouch(){
        if(state){
            state = false;
            return true;
        }
        return false;
    }
    public InputProcessor getButtonProcessor(){
        return processor;
    }

    public void draw(SpriteBatch batch){
        if(down){
            buttonDown.draw(batch);
        }else{
            buttonUp.draw(batch);
        }
    }



    class ButtonProcessor extends InputAdapter {
        Button button;
        Viewport viewport;
        int pointer;
        ButtonProcessor(Button button, Viewport viewport){
            this.button = button;
            this.viewport = viewport;
            pointer = -1;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(this.pointer != -1){
                return false;
            }
            Vector2 vector = viewport.unproject(new Vector2(screenX, screenY));
            vector.sub(new Vector2(this.button.buttonUp.getX() + this.button.buttonUp.getWidth()/2,
                    this.button.buttonUp.getY() + this.button.buttonUp.getHeight()/2));
            float r = this.button.buttonUp.getHeight()/2;
            if(vector.len2() <= r*r){
                this.pointer = pointer;
                this.button.down = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(this.pointer != pointer){
                return false;
            }
            Vector2 vector = viewport.unproject(new Vector2(screenX, screenY));
            vector.sub(new Vector2(this.button.buttonUp.getX() + this.button.buttonUp.getWidth()/2,
                    this.button.buttonUp.getY() + this.button.buttonUp.getHeight()/2));
            float r = this.button.buttonUp.getHeight()/2;
            if(vector.len2() <= r*r){
                this.button.state = true;
            }
            this.button.down = false;
            this.pointer = -1;
            return true;
        }
    }
}
