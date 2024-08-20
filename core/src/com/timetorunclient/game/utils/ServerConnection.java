package com.timetorunclient.game.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerConnection extends Thread{
    DatagramSocket socket;
    volatile public boolean running = false;
    volatile public boolean waitingGuests = false;
    final public ConcurrentLinkedQueue<Intention> move = new ConcurrentLinkedQueue<>();
    final public ConcurrentLinkedQueue<Intention> kill = new ConcurrentLinkedQueue<>();
    final public ConcurrentLinkedQueue<Intention> gameReceived = new ConcurrentLinkedQueue<>();
    volatile public HashMap<InetAddress, Integer> players;
    volatile public HashMap<Integer, InetSocketAddress> playersReversed;
    final ArrayList<Long> lastPacket = new ArrayList<>();
    volatile int maxPlayers = -1;
    byte[] buf;
    ServerConnection(DatagramSocket socket){
        this.socket = socket;
        buf = new byte[128];
        maxPlayers = -1;
    }

    @Override
    public void run() {
        running = true;
        waitingGuests = true;
        players = new HashMap<>();
        playersReversed = new HashMap<>();

        while (running){
            DatagramPacket packet = new DatagramPacket(buf, 128);
            try {
                socket.receive(packet);
            } catch (IOException ignored) {
                continue;
            }
            PlayerPacket parsedPacket = new PlayerPacket(packet);
            int ind = -1;
            if(players.containsKey(packet.getAddress())) ind = players.get(packet.getAddress());
            else if(waitingGuests && players.size() < maxPlayers){
                ind = players.size();
                players.put(packet.getAddress(), ind);
                playersReversed.put(ind, new InetSocketAddress(packet.getAddress(), packet.getPort()));
                lastPacket.add(-1L);
            }
            if(ind == -1 || waitingGuests
                    || lastPacket.get(ind) >= parsedPacket.time){
                continue;
            }
            lastPacket.set(ind, parsedPacket.time);
            switch (parsedPacket.intent){
                case 1:
                    move.add(new Intention(ind, parsedPacket.vert));
                    break;
                case 2:
                    kill.add(new Intention(ind, parsedPacket.vert));
                    break;
                case 3:
                    gameReceived.add(new Intention(ind, parsedPacket.vert));
                    break;
            }
        }
    }
}
