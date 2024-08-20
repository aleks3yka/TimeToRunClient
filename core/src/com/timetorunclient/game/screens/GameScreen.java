package com.timetorunclient.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.timetorunclient.game.GraphView;
import com.timetorunclient.game.TimeToRun;
import com.timetorunclient.game.utils.Connection;
import com.timetorunclient.game.space.Graph;
import com.timetorunclient.game.ui.Button;
import com.timetorunclient.game.ui.Slider;
import com.timetorunclient.game.ui.Wheel;

import java.net.InetSocketAddress;


public class GameScreen extends ScreenAdapter {
    TimeToRun game;
    ExtendViewport controlsPort;
    FitViewport gamePort;
    Wheel wheel;
    Slider slider;
    Button buttonA, buttonB;
    TextureAtlas controlsTextures;
    InputMultiplexer inputMultiplexer;
    InputProcessor prevInputProcessor;

    GraphView graphView;
    Graph graph;

    //For Testing

    float mytime;

    public GameScreen(TimeToRun game){
        this.game = game;
        controlsPort = new ExtendViewport(20, 9);
        gamePort = new FitViewport(10, 10);
    }

    @Override
    public void show() {
        //For testing
        this.game.server.setMinPlayers(1);
        this.game.server.createGraph(Gdx.files.internal("map.txt").read());
        this.game.server.startWaiting();

        //create connection
        this.game.connection.setServerAddress(this.game.server.getSocketAddress());
        this.game.connection.connect();

        //change inputProcessor
        prevInputProcessor = Gdx.input.getInputProcessor();
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        //update viewports

        controlsPort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gamePort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //get atlas for controls

        controlsTextures = game.assets.get("controls.atlas", TextureAtlas.class);

        //create controls

        //create slider and alive it
        Vector2 start = new Vector2(controlsPort.getWorldWidth()*1/4, -controlsPort.getWorldHeight() * 7 / 18);
        Vector2 end = new Vector2(controlsPort.getWorldWidth()*1/4, controlsPort.getWorldHeight() * 5 / 18);


        slider = new Slider(controlsPort,
                controlsTextures.createSprite("slider"),
                controlsTextures.createSprite("sliderKnob"),
                start, end, 1);

        inputMultiplexer.addProcessor(slider.getSliderProcessor());

        //create wheel and alive it
        wheel = new Wheel(controlsPort.getWorldWidth()*15 / 40,
                -controlsPort.getWorldHeight() * 5 / 18,
                1.5f,
                controlsPort,
                controlsTextures.createSprite("wheel"));
        inputMultiplexer.addProcessor(wheel.getWheelProcessor());

        //create buttons

        buttonA = new Button(controlsPort, controlsTextures.createSprite("buttonUp"),
                controlsTextures.createSprite("buttonDown"),
                -controlsPort.getWorldWidth()*7/20, -controlsPort.getWorldHeight()/9, 1);
        buttonB = new Button(controlsPort, controlsTextures.createSprite("buttonUp"),
                controlsTextures.createSprite("buttonDown"),
                -controlsPort.getWorldWidth()*6/20, -controlsPort.getWorldHeight()*3/9, 1);

        inputMultiplexer.addProcessor(buttonA.getButtonProcessor());
        inputMultiplexer.addProcessor(buttonB.getButtonProcessor());

        //create Game things

        //create game atlas
        TextureAtlas gameAtlas = this.game.assets.get("game.atlas", TextureAtlas.class);

        //create Graph
        this.graph = new Graph();

        //create GraphView
        this.graphView = new GraphView(4, 0, 0, 0.8f, 0.4f, this.graph,
                "vertex", gameAtlas,
                wheel, slider);

        //connect classes
        this.graph.setGraphView(graphView);
        this.game.connection.setGraph(graph);
        this.graph.setConnection(this.game.connection);
        this.graph.setGoButton(buttonA);
        this.graph.setEraseButton(buttonB);
        this.graph.setGoCooldown(1);


        this.game.connection.update();

        mytime = 0;
    }

    @Override
    public void resize(int width, int height) {
        controlsPort.update(width, height);
        gamePort.update(width, height);
    }

    @Override
    public void render(float delta) {
        //System.out.println(Gdx.graphics.getFramesPerSecond());

        //Draw
        //ScreenUtils.clear(0.345f, 0.592f, 1 ,1);
        mytime+=delta;
        ScreenUtils.clear((MathUtils.sin(mytime*MathUtils.PI2/15)+1)/2,
                (MathUtils.sin(mytime*MathUtils.PI2/15 - MathUtils.PI2/3)+1)/2,
                (MathUtils.sin(mytime*MathUtils.PI2/15 - 2 * MathUtils.PI2/3)+1)/2, 1);
        game.batch.begin();

        //Game part
        game.batch.setProjectionMatrix(gamePort.getCamera().combined);
        gamePort.apply();
        game.batch.enableBlending();

        graphView.update(delta);
        this.graphView.draw(this.game.batch);


        game.batch.disableBlending();

        //Control part
        game.batch.setProjectionMatrix(controlsPort.getCamera().combined);

        game.batch.enableBlending();
        controlsPort.apply();

        //draw controls
        //wheel.draw(game.batch);
        slider.draw(game.batch);
        buttonA.draw(game.batch);
        buttonB.draw(game.batch);



        game.batch.disableBlending();
        game.batch.end();

        //Do network magic and update entities
        this.game.connection.update();
        graph.update(delta);
    }

    @Override
    public void hide() {
        // returns input processor
        Gdx.input.setInputProcessor(prevInputProcessor);

        // frees memory from atlases

        controlsTextures.dispose();
        game.assets.unload("controls.atlas");
        this.game.assets.unload("game.atlas");
        this.game.connection.stop();
    }
}
