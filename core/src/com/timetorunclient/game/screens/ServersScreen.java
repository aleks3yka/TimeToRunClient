package com.timetorunclient.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.ScreenUtils;
import com.timetorunclient.game.TimeToRun;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ServersScreen extends ScreenAdapter {
    TimeToRun game;
    InputProcessor prevInputProcessor;
    Stage stage;

    public ServersScreen(TimeToRun game) {
        this.game = game;

    }

    @Override
    public void show() {
        game.discovery.startDiscovery();


        stage = new Stage();
        prevInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(stage);
        Skin skin = game.assets.get("expeeui/expee-ui.json", Skin.class);
        Table root = new Table(skin);
        stage.addActor(root);
        root.setFillParent(true);

        TextButton exit = new TextButton("back", skin);
        exit.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if(exit.isChecked()){
                    game.setScreenAndLoadNeededResources(TimeToRun.AppScreens.MENU);
                }
                return false;
            }
        });
        root.left().top();
        root.add(exit).width(Value.percentWidth(0.1f, root)).height(Value.percentHeight(0.05f, root))
                .padLeft(Value.percentWidth(0.01f, root)).padTop(Value.percentHeight(0.02f, root));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1,1,1,1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        game.discovery.endDiscovery();
        Gdx.input.setInputProcessor(prevInputProcessor);
    }
}
