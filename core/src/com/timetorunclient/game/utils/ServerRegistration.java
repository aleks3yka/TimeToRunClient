package com.timetorunclient.game.utils;

import java.net.InetSocketAddress;

public interface ServerRegistration {
    void register();
    void unregister();
    void setAddress(InetSocketAddress address);
    void setName(String name);
}
