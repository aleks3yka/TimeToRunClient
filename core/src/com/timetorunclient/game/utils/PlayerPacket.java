package com.timetorunclient.game.utils;

import java.net.DatagramPacket;
import java.util.Scanner;

public class PlayerPacket {
    public final long time;
    public final int intent, vert;
    PlayerPacket(DatagramPacket packet){
        Scanner scanner = new Scanner(new String(packet.getData()));
        time = scanner.nextLong();
        intent = scanner.nextInt();
        vert = scanner.nextInt();
    }
}
