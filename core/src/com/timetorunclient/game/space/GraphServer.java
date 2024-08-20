package com.timetorunclient.game.space;

import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import com.timetorunclient.game.GraphView;
import com.timetorunclient.game.utils.Connection;
import com.timetorunclient.game.entities.Entity;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class GraphServer {
    Vertex[] vertices;
    Edge[] edges;
    public ArrayList<Edge>[] edgesRepresentation;
    Entity[] entities;
    GraphView graphView;
    Connection connection;
    public volatile ArrayList<Entity> players;
    long lastUpdate;

    private static final float UPDATES_PER_SECOND = 60;

    float prevTime;

    public GraphServer(){
        vertices = null;
        prevTime = 0;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    //Personal life

    public void update(long delta){
        float dt = (delta) / 1000f;
        lastUpdate = TimeUtils.millis();
        prevTime += dt;
        while (prevTime >= 1/UPDATES_PER_SECOND){
            prevTime -= 1/UPDATES_PER_SECOND;
            for(Entity i : entities){
                i.act(1/UPDATES_PER_SECOND);
            }
        }
    }

//    public void trunkPlayers(int num){
//        for()
//    }


    //For network
    public void setGraph(Vertex[] vertices, Edge[] edges, Entity[] entities){
        this.vertices = vertices;
        this.edges = edges;
        this.entities = entities;
        this.edgesRepresentation = new ArrayList[vertices.length];
        for(int i = 0; i < vertices.length; i++) this.edgesRepresentation[i] = new ArrayList<Edge>();
        for(int i = 0; i < this.edges.length; i++){
            this.edgesRepresentation[this.edges[i].from].add(this.edges[i]);
            this.edgesRepresentation[this.edges[i].to].add(this.edges[i]);
        }
    }

//    public DatagramPacket getData(int playerId){
//
//    }
    public DatagramPacket getDataForConnection(int playerId, InetAddress address, int port){
        StringBuilder msg = new StringBuilder();
        msg.append(String.valueOf(TimeUtils.millis())).append("\nwaiting\n");
        msg.append(String.valueOf(vertices.length)).append(" ")
                .append(String.valueOf(edges.length)).append(" ")
                .append(String.valueOf(entities.length)).append(" ")
                .append(String.valueOf(playerId)).append("\n");
        for(int i = 0; i < vertices.length; i++){
            msg.append(vertices[i].spriteName).append("\n");
        }
        for(int i = 0; i < edges.length; i++){
            Edge edge = edges[i];
            msg.append(String.valueOf(edge.from)).append(" ")
                    .append(String.valueOf(edge.to)).append(" ")
                    .append(String.valueOf(edge.weight)).append(" ")
                    .append(edge.spriteName).append("\n");
        }
        for (int i = 0; i < entities.length; i++){
            Entity entity = entities[i];
            msg.append(String.valueOf(entity.getVertex())).append(" ")
                    .append(String.valueOf(Math.round(entity.getSpeed()*1000))).append(" ")
                    .append(entity.spriteName.get(0)).append(" ")
                    .append(entity.spriteName.get(1)).append(" ")
                    .append(entity.spriteName.get(2)).append("\n");
        }
        return new DatagramPacket(msg.toString().getBytes(), msg.length(), address, port);
    }
}
