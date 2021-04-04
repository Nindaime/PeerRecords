package esw.peeplotech.peerrecords.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import esw.peeplotech.peerrecords.staff.StaffDashboard;
import esw.peeplotech.peerrecords.util.Common;
import io.paperdb.Paper;

public class WifiDirectReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private StaffDashboard staffDashboard;

    public WifiDirectReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, StaffDashboard staffDashboard) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.staffDashboard = staffDashboard;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //get intent action
        String action = intent.getAction();

        //check actions
        if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {

            //get wifi p2p state
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            //check values
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "Wifi is ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Wifi is OFF", Toast.LENGTH_SHORT).show();
            }

        } else if (action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {

            if (mManager != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mManager.requestPeers(mChannel, staffDashboard.peerListListener);
            }

        } else

        if (action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){

            if (mManager == null){

                return;

            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()){
                mManager.requestConnectionInfo(mChannel, staffDashboard.connectionInfoListener);
            } else {
                Paper.book().write(Common.CONNECTION_STATUS, Common.CONNECTION_DISCONNECTED);
            }

        } else

        if (action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){



        }

    }
}