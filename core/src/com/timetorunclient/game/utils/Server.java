package com.timetorunclient.game.utils;

import com.badlogic.gdx.utils.TimeUtils;
import com.sun.org.apache.xpath.internal.operations.String;
import com.timetorunclient.game.entities.Entity;
import com.timetorunclient.game.space.Edge;
import com.timetorunclient.game.space.GraphServer;
import com.timetorunclient.game.space.Vertex;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int SEND_COOLDOWN = 100;
    private static final int REMOVE_COOLDOWN = 1000, MOVE_COOLDOWN = 1000;
    public DatagramSocket socket;
    Executer executer;
    volatile ServerState state;
    ServerConnection connection;
    GraphServer graph;
    volatile AtomicInteger playersGotMap;
    volatile ArrayList<Boolean> playersGotMapArray;
    volatile int minPlayers = 1;
    long lastSend = -1;
    public Server(){
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        state = ServerState.SLEEP;
        executer = new Executer(this);
        connection = new ServerConnection(socket);
    }

    public ServerState getState() {
        return state;
    }

    public int getMaxPlayers(){
        return graph.players.size();
    }
    public int getPlayers(){
        return playersGotMap.get();
    }

    public InetSocketAddress getSocketAddress(){
        return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
    }

    public void createGraph(InputStream stream){
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt(), m = scanner.nextInt();
        Vertex[] vertices = new Vertex[n];
        for(int i = 0; i < n; i++){
            vertices[i] = new Vertex(scanner.next());
        }
        Edge[] edges = new Edge[m];
        for(int i = 0; i < m; i++){
            edges[i] = new Edge(scanner.nextInt(), scanner.nextInt(),
                    scanner.nextInt(), true, scanner.next());
        }
        int k1 = scanner.nextInt();
        Entity[] entities = new Entity[k1];
        for(int i = 0; i < k1; i++){
            entities[i] = new Entity(scanner.nextInt(), scanner.nextInt()/1000f,
                    scanner.next(), scanner.next(), scanner.next());
        }
        System.out.println(n + " " + m + " " + k1);
        connection.maxPlayers = k1;
        playersGotMapArray = new ArrayList<>(k1);
        for(int i = 0; i < k1; i++){
            playersGotMapArray.add(false);
        }
        graph = new GraphServer();
        graph.setGraph(vertices, edges, entities);
    }

    public void startWaiting(){
        if(state != ServerState.SLEEP){
            return;
        }
        state = ServerState.WAIT_FOR_PLAYERS;
        connection.start();
        executer.start();
    }
    void startGame(){
        if(state == ServerState.WAIT_FOR_PLAYERS && connection.players.size() >= minPlayers){
            state = ServerState.PLAY;
            connection.waitingGuests = false;
        }
    }
    void stop(){
        state = ServerState.SLEEP;
        connection.running = false;
    }

    private class Executer extends Thread{
        Server server;
        long time;
        Executer(Server myServer){
            server = myServer;
            time = TimeUtils.millis();
        }
        @Override
        public void run() {
            while(server.getState() != ServerState.SLEEP){
                switch (server.getState()){
                    case PLAY:
                        break;
                    case WAIT_FOR_PLAYERS:
                        //System.out.println(server.connection.maxPlayers);
                        if(!server.connection.gameReceived.isEmpty()){
                            Intention intention = server.connection.gameReceived.poll();
                            if(!playersGotMapArray.get(intention.who)){
                                playersGotMapArray.set(intention.who, true);
                                playersGotMap.getAndIncrement();
                            }
                        }
                        if(time + SEND_COOLDOWN <= TimeUtils.millis()){
                            for(int i = 0; i < server.connection.maxPlayers; i++){
//                                System.out.println(i + "\n"
//                                        + !playersGotMapArray.get(i) + "\n"
//                                        + server.connection.playersReversed.containsKey(i));
                                if(!playersGotMapArray.get(i) && server.connection.playersReversed.containsKey(i)){
                                    //System.out.println("i'm here");
                                    try {
                                        socket.send(graph.getDataForConnection(i,
                                                server.connection.playersReversed.get(i).getAddress(),
                                                server.connection.playersReversed.get(i).getPort()));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            time = TimeUtils.millis();
                        }
                        break;
                }
            }
        }

    }

    enum ServerState {
        SLEEP, WAIT_FOR_PLAYERS, PLAY;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }
}