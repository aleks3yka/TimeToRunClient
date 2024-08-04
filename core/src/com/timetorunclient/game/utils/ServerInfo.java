package com.timetorunclient.game.utils;

import java.net.InetSocketAddress;

public class ServerInfo implements Comparable<ServerInfo>{
    final public InetSocketAddress address;
    final public String name;
    public ServerInfo(InetSocketAddress address, String name) {
        this.address = address;
        this.name = name;
    }

    @Override
    public int compareTo(ServerInfo o) {
        return (int) Math.signum(name.hashCode() - o.name.hashCode());
    }
}
