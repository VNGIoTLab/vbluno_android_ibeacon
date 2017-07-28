package org.altbeacon.beaconreference;

import java.util.Collection;
import java.util.ArrayList;

import android.app.Activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

/**
 * @author dyoung
 * @author namdt
 */
public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    static private ArrayList<Beacon>arrListBeacon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        arrListBeacon = new ArrayList<Beacon>();

        beaconManager.bind(this);
    }

    @Override 
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override 
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override 
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
           @Override
           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
              if (beacons.size() > 0) {
                  arrListBeacon.clear();
                  arrListBeacon.addAll(beacons);
                  showListBeacon();
              }
           }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    private void showListBeacon() {
        runOnUiThread(new Runnable() {
            public void run() {
                Beacon beacon;
                EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                int size = arrListBeacon.size();
                editText.setText("Number of iBeacon devices: " + String.valueOf(size));
                for(int i = 0; i < size; ++i) {
                    beacon = arrListBeacon.get(i);
                    editText.append("\n---------------------" +
                                    "\n-UUID: " + beacon.getId1().toUuid().toString() +
                                    "\n-Address: " + beacon.getBluetoothAddress() +
                                    "\n-Major: " + beacon.getId2().toHexString() +
                                    "\n-Minor: " + beacon.getId3().toHexString() +
                                    "\n-TxPower: " + beacon.getTxPower() + " dBm" +
                                    "\n-RSSI: " + beacon.getRssi() + " dBm" +
                                    "\n-Distance: " + String.format("%.3f", beacon.getDistance()) + " m");
                }
            }
        });
    }

}
