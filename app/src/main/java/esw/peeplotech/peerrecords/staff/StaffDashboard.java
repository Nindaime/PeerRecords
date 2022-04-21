package esw.peeplotech.peerrecords.staff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.SignIn;
import esw.peeplotech.peerrecords.adapters.DeviceAdapter;
import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.databinding.ActivityStaffDashboardBinding;
import esw.peeplotech.peerrecords.fragments.AllRecords;
import esw.peeplotech.peerrecords.fragments.MyRecords;
import esw.peeplotech.peerrecords.interfaces.DeviceListener;
import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.receivers.WifiDirectReceiver;
import esw.peeplotech.peerrecords.util.Common;
import esw.peeplotech.peerrecords.util.Methods;
import io.paperdb.Paper;

public class StaffDashboard extends AppCompatActivity implements DeviceListener {

    //binding
    private ActivityStaffDashboardBinding activity;

    //fragment
    private MyRecords myRecords = new MyRecords();
    private AllRecords allRecords = new AllRecords();

    //dialogs
    private AlertDialog connectionDialog;

    //permission
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private static final int VERIFY_LOCATION_REQUEST = 7598;

    //P2P networking
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2PManager;
    private WifiP2pManager.Channel wifiP2PChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private boolean isWifiEnabled = false;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] deviceNameArray;
    private WifiP2pDevice[] deviceArray;
    public WifiP2pManager.PeerListListener peerListListener;
    private DeviceAdapter adapter;
    public WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private static final int MESSAGE_READ = 1;
    private Handler handler;
    private ServerClass serverClass;
    private ClientClass clientClass;
    private SendReceive sendReceive;
    private RelativeLayout joinConnectionBtn, disconnectBtn;

    //values
    private String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_staff_dashboard);

        //value
        myUsername = Paper.book().read(Common.USER_ID);

        //init
        initialize();
    }

    private void initialize() {

        //request permission
        requestPermission();

        //network init
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2PManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiP2PChannel = wifiP2PManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectReceiver(wifiP2PManager, wifiP2PChannel, this);
        isWifiEnabled = wifiManager.isWifiEnabled();

        //intent filter init
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //connection info listener
        connectionInfoListener = info -> {
            Paper.book().write(Common.CONNECTION_STATUS, Common.CONNECTION_CONNECTED);

            //the info
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            //check if i am host
            if (info.isGroupOwner && info.groupFormed){

                Paper.book().write(Common.CONNECTION_HOST, Common.HOST_ME);
                serverClass = new ServerClass();
                serverClass.start();

            } else {

                Paper.book().write(Common.CONNECTION_HOST, Common.HOST_NOT_ME);
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();

            }
        };

        //handler
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {

                switch (msg.what){
                    case MESSAGE_READ:
                        byte[] readBuff = (byte[]) msg.obj;
                        String tempMsg = new String(readBuff, 0, msg.arg1);
                        break;
                }

                return true;
            }

        });

        //get current user
        User currentUser = Paper.book().read(Common.CURRENT_USER);
        activity.setUserData(currentUser);

        //set base fragment
        setBaseFragment(myRecords);

        //my records
        activity.myRecord.setOnClickListener(v -> {

            activity.setIsMyRecord(true);

            setFragment(myRecords);

        });

        //all records
        activity.allRecord.setOnClickListener(v -> {

            activity.setIsMyRecord(false);

            setFragment(allRecords);

        });

        //connection
        activity.connectionFab.setOnClickListener(v -> {

            openConnectionDialog();

        });

        //menu
        activity.menuBtn.setOnClickListener(v -> {

            PopupMenu popupInvalidate = new PopupMenu(this, activity.menuBtn);
            popupInvalidate.inflate(R.menu.menu);
            popupInvalidate.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.action_logs) {

                    //go to connection logs
                    Intent logsIntent = new Intent(this, ConnectionLogs.class);
                    startActivity(logsIntent);
                    return true;

                } else if (item.getItemId() == R.id.action_logout) {

                    //end session
                    new Database(this).logoutSession(Paper.book().read(Common.CURRENT_SESSION), Methods.getTimestamp());

                    //destroy local db
                    Paper.book().delete(Common.USER_ID);
                    Paper.book().delete(Common.CURRENT_USER);
                    Paper.book().delete(Common.CURRENT_SESSION);

                    //go to sign in
                    Intent logoutIntent = new Intent(this, SignIn.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    finish();
                    return true;

                }
                return false;
            });

            popupInvalidate.show();

        });
    }

    private void initCsvFileCarrier() {

        //check if file exist in directory and create if not
        File dir = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER);
        if (dir.exists()) {
            dir.mkdir();
        }

        //check if config file exists and create file if not
        File newFile = new File(dir, Common.CSV_FILE);
        try (FileWriter writer = new FileWriter(newFile, true)) {

            StringBuilder sb = new StringBuilder();
            sb.append("record_id");
            sb.append(',');
            sb.append("staff_username");
            sb.append(',');
            sb.append("student_username");
            sb.append(',');
            sb.append("score");
            sb.append(',');
            sb.append("timestamp");
            sb.append(',');
            sb.append("record_status");
            sb.append(',');
            sb.append("record_reason");
            sb.append('\n');

            writer.write(sb.toString());

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File Not Available", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            initCsvFileCarrier();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERIFY_PERMISSIONS_REQUEST);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == VERIFY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                initCsvFileCarrier();

            } else {

                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();

            }
        } else

        if (requestCode == VERIFY_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted, please try again", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void setBaseFragment(MyRecords home) {

        //set state
        activity.setIsMyRecord(true);

        //load fragment
        setFragment(home);

    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragFrame, fragment);
        fragmentTransaction.commitAllowingStateLoss();

    }

    private void openConnectionDialog() {

        connectionDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.connection_layout, null);

        //widgets
        RecyclerView deviceRecycler = viewOptions.findViewById(R.id.deviceRecycler);
        joinConnectionBtn = viewOptions.findViewById(R.id.joinConnectionBtn);
        TextView joinConnectionText = viewOptions.findViewById(R.id.joinConnectionText);
        AVLoadingIndicatorView joinConnectionProgress = viewOptions.findViewById(R.id.joinConnectionProgress);
        disconnectBtn = viewOptions.findViewById(R.id.disconnectBtn);
        TextView disconnectText = viewOptions.findViewById(R.id.disconnectText);
        AVLoadingIndicatorView disconnectProgress = viewOptions.findViewById(R.id.disconnectProgress);

        //add view properties
        connectionDialog.setView(viewOptions);
        connectionDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        connectionDialog.getWindow().setGravity(Gravity.BOTTOM);
        connectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = connectionDialog.getWindow().getAttributes();
        connectionDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        connectionDialog.show();

        //disply btns
        joinConnectionBtn.setVisibility(View.VISIBLE);

        //join
        joinConnectionBtn.setOnClickListener(v -> {

            //start loading
            joinConnectionBtn.setEnabled(false);
            joinConnectionText.setVisibility(View.INVISIBLE);
            joinConnectionProgress.setVisibility(View.VISIBLE);

            //enable wifi if not enabled
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            //start peer listener
            //check permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                wifiP2PManager.discoverPeers(wifiP2PChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(StaffDashboard.this, "Peer discovery started", Toast.LENGTH_SHORT).show();

                        //manager populate
                        peerListListener = peersList -> {
                            if (!peersList.getDeviceList().equals(peers)) {

                                //clear list
                                peers.clear();

                                //populate with new list
                                peers.addAll(peersList.getDeviceList());

                                //init recycler
                                deviceRecycler.setHasFixedSize(true);
                                deviceRecycler.setLayoutManager(new LinearLayoutManager(StaffDashboard.this));

                                //adapter
                                adapter = new DeviceAdapter(StaffDashboard.this, StaffDashboard.this, peers, StaffDashboard.this);
                                deviceRecycler.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }

                            if (peersList.getDeviceList().size() == 0) {
                                Toast.makeText(StaffDashboard.this, "No device found", Toast.LENGTH_SHORT).show();
                            }

                            //stop loading
                            joinConnectionBtn.setEnabled(true);
                            joinConnectionProgress.setVisibility(View.INVISIBLE);
                            joinConnectionText.setVisibility(View.VISIBLE);
                        };
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(StaffDashboard.this, "Couldnt start peer discovery because: " + reason, Toast.LENGTH_SHORT).show();
                    }
                });

            } else {

                //stop loading
                joinConnectionBtn.setEnabled(true);
                joinConnectionProgress.setVisibility(View.INVISIBLE);
                joinConnectionText.setVisibility(View.VISIBLE);

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, VERIFY_PERMISSIONS_REQUEST);

            }


        });

        //disconnect
        disconnectBtn.setOnClickListener(v -> {
            wifiP2PManager.cancelConnect(wifiP2PChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    joinConnectionBtn.setVisibility(View.VISIBLE);
                    disconnectBtn.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int reason) {

                }
            });

            wifiManager.setWifiEnabled(false);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //register receiver
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister receiver
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        if (!activity.getIsMyRecord()) {

            setBaseFragment(myRecords);

        } else {

            finish();

        }
    }

    @Override
    public void onDeviceClicked(WifiP2pDevice device) {
        //setup connection config
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        //setup manager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        wifiP2PManager.connect(wifiP2PChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(StaffDashboard.this, "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();

                joinConnectionBtn.setVisibility(View.GONE);
                disconnectBtn.setVisibility(View.VISIBLE);

                //write to csv
                File dir = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER);
                if (dir.exists()) {
                    dir.mkdir();
                }

                //check if config file exists and create file if not
                File newFile = new File(dir, Common.CSV_FILE);

                //write to file
                List<Record> recordList = new Database(StaffDashboard.this).getAllRecords();
                for (Record theRecord : recordList){


                    try (FileWriter writer = new FileWriter(newFile, true)) {

                        StringBuilder sb = new StringBuilder();
                        sb.append(theRecord.getRecord_id());
                        sb.append(',');
                        sb.append(theRecord.getStaff_username());
                        sb.append(',');
                        sb.append(theRecord.getStudent_username());
                        sb.append(',');
                        sb.append(theRecord.getScore());
                        sb.append(',');
                        sb.append(theRecord.getTimestamp());
                        sb.append(',');
                        sb.append(theRecord.getRecord_status());
                        sb.append(',');
                        sb.append(theRecord.getRecord_reason());
                        sb.append('\n');

                        writer.write(sb.toString());

                        //send file
                        sendReceive.writeData(newFile);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                }

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(StaffDashboard.this, "Connection to " + device.deviceName + " failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ServerClass extends Thread {

        //create socket
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread {

        //create socket
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress){
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendReceive extends Thread {

        //create socket
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt){
            socket = skt;

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (socket != null){

                try {
                    //write to csv
                    File dir = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER);
                    if (dir.exists()) {
                        dir.mkdir();
                    }

                    //check if config file exists and create file if not
                    File newFile = new File(dir, Common.CSV_FILE);

                    File dirs = new File(newFile.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    newFile.createNewFile();
                    InputStream inputstream = socket.getInputStream();
                    copyFile(inputstream, new FileOutputStream(newFile));
                    socket.close();


                    //open file and do check algorithm
                    compareDataAlgo(newFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

        public void writeData(File csvFile){
            int port;
            int len;
            byte buf[]  = new byte[1024];

            try {


                OutputStream outputStream = socket.getOutputStream();
                ContentResolver cr = StaffDashboard.this.getContentResolver();
                InputStream inputStream = null;
                inputStream = cr.openInputStream(Uri.parse(csvFile.getAbsolutePath()));
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void compareDataAlgo(File newFile) {

        try {
            CSVReader reader = new CSVReader(new FileReader(newFile));
            String[] nextLine;
            int count = 0;
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                count++;

                if (nextLine.length == 7) {

                    if (!new Database(this).isRecordIdInUse(nextLine[0]) ) {

                        new Database(this).createNewRecord(nextLine[0], nextLine[1], nextLine[2], Integer.parseInt(nextLine[3]), nextLine[4], nextLine[5], nextLine[6]);

                    } else {

                        if (!myUsername.equals(nextLine[1])){

                            //my record
                            Record theRecord = new Database(StaffDashboard.this).getRecordDetails(nextLine[0]);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            Date parsedDate = dateFormat.parse(theRecord.getTimestamp());
                            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

                            //foreign
                            Date parsedForeignDate = dateFormat.parse(nextLine[4]);
                            Timestamp foreignTimestamp = new java.sql.Timestamp(parsedForeignDate.getTime());

                            //check time
                            if (foreignTimestamp.after(timestamp)){

                                new Database(StaffDashboard.this).updateRecord(nextLine[0], nextLine[4], nextLine[5]);

                            }

                        }

                    }
                }

            }

        } catch (IOException | ParseException e) {
        }


        //write to file
        List<Record> recordList = new Database(StaffDashboard.this).getAllRecords();
        for (Record theRecord : recordList){


            try (FileWriter writer = new FileWriter(newFile, true)) {

                StringBuilder sb = new StringBuilder();
                sb.append(theRecord.getRecord_id());
                sb.append(',');
                sb.append(theRecord.getStaff_username());
                sb.append(',');
                sb.append(theRecord.getStudent_username());
                sb.append(',');
                sb.append(theRecord.getScore());
                sb.append(',');
                sb.append(theRecord.getTimestamp());
                sb.append(',');
                sb.append(theRecord.getRecord_status());
                sb.append(',');
                sb.append(theRecord.getRecord_reason());
                sb.append('\n');

                writer.write(sb.toString());

                //send file
                sendReceive.writeData(newFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

        }


    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }


}