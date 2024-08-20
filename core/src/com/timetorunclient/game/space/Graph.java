package com.timetorunclient.game.space;

import com.badlogic.gdx.utils.TimeUtils;
import com.timetorunclient.game.GraphView;
import com.timetorunclient.game.utils.Connection;
import com.timetorunclient.game.entities.Entity;
import com.timetorunclient.game.ui.Button;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Graph {
    Vertex[] vertices;
    Edge[] edges;
    public int[][] minimumEdges;
    public ArrayList<Edge>[] edgesRepresentation;
    Entity[] entities;
    GraphView graphView;
    Connection connection;
    Button goButton;
    Button eraseButton;
    Entity playableEntity;
    boolean graphSet = false;
    int lookingAt;

    private static final float UPDATES_PER_SECOND = 60;

    float prevTime;

    public Graph(){
        vertices = null;
        prevTime = 0;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setGoButton(Button goButton) {
        this.goButton = goButton;
    }

    public void setEraseButton(Button eraseButton) {
        this.eraseButton = eraseButton;
    }

    public void setGraphView(GraphView graphView){
        this.graphView = graphView;
        if(vertices != null){
            graphView.sync(vertices, edges, entities);
            graphView.lookAt(lookingAt, goCooldown);
        }
    }

    //Personal life

    public void update(float dt){
        if(!graphSet || !graphView.synced){
            return;
        }
        prevTime += dt;
        while (prevTime >= 1/UPDATES_PER_SECOND){
            prevTime -= 1/UPDATES_PER_SECOND;
            for(Entity i : entities){
                i.act(1/UPDATES_PER_SECOND);
            }
        }
        graphView.lookAt(playableEntity.getDestination(), goCooldown);
        handleInput();
    }

    long goTimer, killTimer;
    float goCooldown = 1;
    float killCooldown = 1;

    public void setGoCooldown(float goCooldown) {
        this.goCooldown = goCooldown;
    }

    public void setKillCooldown(float killCooldown) {
        this.killCooldown = killCooldown;
    }

    public void handleInput(){
        if(goButton.getTouch() && goTimer < TimeUtils.millis()){
            goTimer = TimeUtils.millis() + (int)(goCooldown*1000);
            int vertex = graphView.getSelectedVertex();
            if(vertex != -1) {
                playableEntity.move(vertex, TimeUtils.millis());
            }
        }
        if(killTimer < TimeUtils.millis() && eraseButton.getTouch()){
            killTimer = TimeUtils.millis() + (int)(killCooldown*1000);
        }
    }

    public void findMinimumEdges(int v){
        ArrayDeque<Integer> queue;
        queue = new ArrayDeque<Integer>();
        queue.addLast(v);
        boolean[] used = new boolean[vertices.length];
        for(int i = 0; i < vertices.length; i++){
            used[i] = false;
        }
        minimumEdges[v][v] = 0;
        used[v] = true;
        while (!queue.isEmpty()){
            int now = queue.pollFirst();
            for(Edge i : this.edgesRepresentation[now]){
                if(used[i.getTo(now)] || i.getWeight() == -1){
                    continue;
                }
                used[i.getTo(now)] = true;
                minimumEdges[v][i.getTo(now)] = minimumEdges[v][now] + 1;
                minimumEdges[now][i.getTo(now)] = 1;
                queue.addLast(i.getTo(now));
            }
        }
    }

    //Functions for changing parameters offline


    //For network
    public void setGraph(Vertex[] vertices, Edge[] edges, Entity[] entities, Entity playableEntity){
        this.playableEntity = playableEntity;
        this.vertices = vertices;
        this.edges = edges;
        this.entities = entities;
        this.edgesRepresentation = new ArrayList[vertices.length];
        for(int i = 0; i < vertices.length; i++) this.edgesRepresentation[i] = new ArrayList<Edge>();
        for(int i = 0; i < this.edges.length; i++){
            this.edgesRepresentation[this.edges[i].from].add(this.edges[i]);
            this.edgesRepresentation[this.edges[i].to].add(this.edges[i]);
        }
        this.minimumEdges = new int[vertices.length][];
        for(int i = 0; i < vertices.length; i++){
            this.minimumEdges[i] = new int[vertices.length];

            for(int j = 0; j < vertices.length; j++) {
                this.minimumEdges[i][j] = -1;
                if(j == i){
                    this.minimumEdges[i][j] = 0;
                }
            }
        }
        if(graphView != null){
            graphView.sync(vertices, edges, entities);
            graphView.lookAt(playableEntity.getDestination(), goCooldown);
        }
        this.graphSet = true;
    }

    //For graphics

    //For entities

    public boolean isEdge(int u, int v){
        return (minimumEdges[u][v] == 1);
    }
}
