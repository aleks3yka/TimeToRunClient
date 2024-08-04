package com.timetorunclient.game;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.timetorunclient.game.Implementations.ServerDiscoveryImpl;
import com.timetorunclient.game.Implementations.ServerRegistrationImpl;
import com.timetorunclient.game.TimeToRun;
import com.timetorunclient.game.utils.ServerDiscovery;
import com.timetorunclient.game.utils.ServerRegistration;

public class AndroidLauncher extends AndroidApplication {
	private static final String TAG = "launcher";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		WifiP2pManager manager = (WifiP2pManager) this.getSystemService(Context.WIFI_P2P_SERVICE);
//
//		BroadcastReceiver receiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				String action = intent.getAction();
//				if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
//					// Determine if Wi-Fi Direct mode is enabled or not, alert
//					// the Activity.
//					int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
//					if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//						Log.d(TAG, "wifiP2p enabled");
//					} else {
//						Log.d(TAG, "wifiP2p disabled");
//					}
//				} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
//
//					// The peer list has changed! We should probably do something about
//					// that.
//
//				} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
//
//					// Connection state changed! We should probably do something about
//					// that.
//
//				} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//					/*DeviceListFragment fragment = (DeviceListFragment) this.getFragmentManager()
//							.findFragmentById(R.id.frag_list);
//					fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//							WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/
//
//				}
//			}
//		};
//
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//
//		// Indicates a change in the list of available peers.
//		filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//
//		// Indicates the state of Wi-Fi Direct connectivity has changed.
//		filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//
//		// Indicates this device's details have changed.
//		//filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//
//		WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
//		registerReceiver(receiver, filter);
//
//		if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
//			// TODO: Consider calling
//			//    Activity#requestPermissions
//			// here to request the missing permissions, and then overriding
//			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//			//                                          int[] grantResults)
//			// to handle the case where the user grants the permission. See the documentation
//			// for Activity#requestPermissions for more details.
//			requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//		}
//		if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
//			// TODO: Consider calling
//			//    Activity#requestPermissions
//			// here to request the missing permissions, and then overriding
//			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//			//                                          int[] grantResults)
//			// to handle the case where the user grants the permission. See the documentation
//			// for Activity#requestPermissions for more details.
//			return;
//		}
//		manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
//			@Override
//			public void onSuccess() {
//				Log.d(TAG, "Woohoo");
//			}
//
//			@Override
//			public void onFailure(int i) {
//
//			}
//		});

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		ServerDiscovery discovery = new ServerDiscoveryImpl(getApplicationContext());
		ServerRegistration registration = new ServerRegistrationImpl(getApplicationContext());

		initialize(new TimeToRun(discovery, registration), config);
	}
}
