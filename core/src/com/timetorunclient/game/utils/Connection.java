package com.timetorunclient.game.utils;



import com.badlogic.gdx.utils.TimeUtils;
import com.timetorunclient.game.entities.Entity;
import com.timetorunclient.game.space.Edge;
import com.timetorunclient.game.space.Graph;
import com.timetorunclient.game.space.Vertex;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Connection {
    Graph graph;
    boolean giveMapToGraph;
    volatile boolean playing;
    long time = -1;
    DatagramSocket socket;
    Reciever reciever;
    InetSocketAddress serverAddress;
    private static final int MAX_SEND_COOLDOWN = 100;
    public Connection (){
        this.giveMapToGraph = false;
        time = TimeUtils.millis();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }
    public void connect(){
        reciever = new Reciever(serverAddress, this, socket);
        reciever.start();
    }
    public void stop(){
        reciever.running = false;
        reciever = null;
    }
    public void setGraph(Graph graph){
        this.graph = graph;
    }
    public void update(){
        if(time >= TimeUtils.millis()){
            return;
        }
        String msg = TimeUtils.millis() + "\n0 0\n";
        DatagramPacket nothing = new DatagramPacket(msg.getBytes(), msg.length(), serverAddress);
        time = TimeUtils.millis() + MAX_SEND_COOLDOWN;
        try {
            socket.send(nothing);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setServerAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    class Reciever extends Thread{
        final InetSocketAddress waitFrom;
        final Connection connection;
        volatile public boolean running;
        volatile public boolean gotMap;
        byte[] buf;
        final DatagramSocket socket;
        private static final int BUF_LEN = 14336;
        Reciever(InetSocketAddress waitFrom, Connection connection, DatagramSocket socket) {
            this.waitFrom = waitFrom;
            this.connection = connection;
            this.socket = socket;
            this.buf = new byte[BUF_LEN];
        }

        @Override
        public void run() {
            running = true;
            gotMap = false;
            long lastSend = -1;

            while (running){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    continue;
                }
                Scanner scanner = new Scanner(new String(packet.getData(), packet.getOffset(), packet.getLength()));
                long sentTime = scanner.nextLong();
                System.out.println("here " + sentTime + " " + lastSend + " "
                        + packet.getPort() + " " + waitFrom.getPort());
                if(waitFrom.getPort() != packet.getPort()
                        || lastSend >= sentTime){
                    continue;
                }
                lastSend = sentTime;
                System.out.println("stil here");
                playing = "playing".equals(scanner.next());
                if(playing){

                }else{
                    if(gotMap) continue;
                    int n = scanner.nextInt(),
                            m = scanner.nextInt(),
                            k = scanner.nextInt(),
                            id = scanner.nextInt();
                    Vertex[] vertices = new Vertex[n];
                    Edge[] edges = new Edge[m];
                    Entity[] entities = new Entity[k];
                    for(int i = 0; i < n; i++){
                        vertices[i] = new Vertex(scanner.next());
                    }
                    for(int i = 0; i < m; i++){
                        edges[i] = new Edge(scanner.nextInt(),
                                scanner.nextInt(), scanner.nextInt(),
                                true, scanner.next());
                    }
                    for(int i = 0; i < k; i++){
                        entities[i] = new Entity(scanner.nextInt(),
                                scanner.nextInt() / 1000f,
                                scanner.next(), scanner.next(), scanner.next());
                    }

                    connection.graph.setGraph(vertices, edges, entities, entities[id]);
                    gotMap = true;
                }
            }
        }
    }

}
