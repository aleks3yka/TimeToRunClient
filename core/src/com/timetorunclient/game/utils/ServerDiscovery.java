package com.timetorunclient.game.utils;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

public interface ServerDiscovery {
    public ConcurrentSkipListSet<ServerInfo> servers = new ConcurrentSkipListSet<>();
    public void startDiscovery();
    public void endDiscovery();
}
