package com.timetorunclient.game.Implementations;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

import com.timetorunclient.game.utils.ServerDiscovery;
import com.timetorunclient.game.utils.ServerInfo;

import android.net.nsd.*;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServerDiscoveryImpl implements ServerDiscovery {
    private static final String TAG = "discoveryService";
    private final NsdManager nsdManager;

    public ServerDiscoveryImpl(Context context){
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        this.context = context;
    }

    NsdManager.DiscoveryListener discoveryListener;
    Context context;
//    WifiManager.MulticastLock lock;
    boolean started;

    @Override
    public void startDiscovery() {

        discoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                Log.e(TAG, "DiscoveryStartFailed");
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                Log.e(TAG, "StopDiscoveryFailed");
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                Log.d(TAG, "DiscoveryStarted");
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Log.d(TAG, "DiscoveryStopped");
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                System.out.println("omg, i found it");
                NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                        Log.e(TAG, "resolve error");
                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                        if(nsdServiceInfo.getHost() != null) {
                            System.out.println(nsdServiceInfo.getServiceName()
                                    + " " + nsdServiceInfo.getServiceType()
                                    + " " + nsdServiceInfo.getHost().getHostAddress()
                                    + " " + nsdServiceInfo.getPort());
                        }else{
                            System.out.println(nsdServiceInfo.getServiceName()
                                    + " " + nsdServiceInfo.getServiceType()
                                    + nsdServiceInfo.getPort());
                        }
                        ServerInfo info = new ServerInfo(
                                new InetSocketAddress(nsdServiceInfo.getHost(), nsdServiceInfo.getPort()),
                                nsdServiceInfo.getServiceName()
                        );
                        servers.add(info);
                    }
                };
                nsdManager.resolveService(nsdServiceInfo, resolveListener);
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                servers.remove(new ServerInfo(
                        new InetSocketAddress(nsdServiceInfo.getHost(), nsdServiceInfo.getPort()),
                        nsdServiceInfo.getServiceName()
                ));
            }
        };
//        lock = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).createMulticastLock(TAG);
//        lock.setReferenceCounted(true);
//        lock.acquire();
        nsdManager.discoverServices("_myApp._udp", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        started = true;
    }

    @Override
    public void endDiscovery() {
        if(!started){
            return;
        }
        started = false;
        Log.d(TAG, "endDiscovery");
        nsdManager.stopServiceDiscovery(discoveryListener);
//        lock.release();
    }
}
