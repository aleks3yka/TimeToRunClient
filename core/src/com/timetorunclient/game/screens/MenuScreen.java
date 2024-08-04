package com.timetorunclient.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.timetorunclient.game.TimeToRun;
import com.timetorunclient.game.utils.AnimationAdapter;

import sun.jvm.hotspot.ui.ObjectHistogramPanel;

public class MenuScreen extends ScreenAdapter {
    TimeToRun game;
    AnimationAdapter animation;
    Stage stage;
    Skin skin;
    InputProcessor prevInputProcessor;


    public MenuScreen(TimeToRun game){
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        prevInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("expeeui/expee-ui.json", Skin.class);
        skin.get("default", TextButton.TextButtonStyle.class).font = game.font;
        skin.get("default", Window.WindowStyle.class).titleFont = game.font;




        Table root = new Table(skin);
        root.setBackground("wallpaper");
        root.setFillParent(true);
        stage.addActor(root);

        Table chooseScreen = new Table(skin);
        chooseScreen.defaults().width(Value.percentWidth(0.18f, root))
                .height(40).padBottom(Value.percentHeight(0.05f, root));

        root.left();
        root.add(chooseScreen).padLeft(Value.percentWidth(0.07f, root));

        TextButton clientButton = new TextButton("Client", skin);
        clientButton.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if(clientButton.isChecked()){
                    game.setScreenAndLoadNeededResources(TimeToRun.AppScreens.SERVERS);
                }
                return false;
            }
        });
        TextButton hostButton = new TextButton("Host", skin);
        hostButton.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if(hostButton.isChecked()){
                    game.setScreenAndLoadNeededResources(TimeToRun.AppScreens.HOST);
                }
                return false;
            }
        });

        chooseScreen.add(clientButton);
        chooseScreen.row();
        chooseScreen.add(hostButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(prevInputProcessor);
        game.assets.unload("expeeui/expee-ui.json");
    }
}
