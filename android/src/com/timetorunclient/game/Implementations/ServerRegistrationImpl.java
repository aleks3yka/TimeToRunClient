package com.timetorunclient.game.Implementations;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.util.Log;

import com.timetorunclient.game.utils.ServerRegistration;

import java.net.InetSocketAddress;
import java.util.Objects;

public class ServerRegistrationImpl implements ServerRegistration {
    final private String TAG = "TTR Service registration";

    final NsdManager manager;
    InetSocketAddress address = null;
    String name = null;
    boolean started = false;
    NsdManager.RegistrationListener registrationListener;
    String actualName;
//    WifiManager.MulticastLock lock;
    Context context;

    public ServerRegistrationImpl(Context context){
        manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        this.context = context;
    }

    @Override
    public void register() {
        if(address == null || name == null){
            return;
        }
        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(TAG, "Service failed registration");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(TAG, "Service failed registration");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                actualName = nsdServiceInfo.getServiceName();
                Log.d(TAG, "Service registered with name: " + actualName);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Log.d(TAG, "Service unregistered");
            }
        };

        NsdServiceInfo info = new NsdServiceInfo();
        info.setServiceName(name);
        info.setServiceType("_myApp._udp");
        info.setPort(address.getPort());

//        lock = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).createMulticastLock(TAG);
//        lock.setReferenceCounted(true);
//        lock.acquire();

        manager.registerService(info, NsdManager.PROTOCOL_DNS_SD, registrationListener);

        started = true;
    }

    @Override
    public void unregister() {
        if(!started){
            return;
        }
        started = false;
        manager.unregisterService(registrationListener);
//        lock.release();
    }

    @Override
    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public void setName(String name) {
        if(Objects.equals(name, "")){
            return;
        }
        this.name = name;
    }
}
