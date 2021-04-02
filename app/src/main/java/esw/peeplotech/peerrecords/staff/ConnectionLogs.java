package esw.peeplotech.peerrecords.staff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.adapters.LogAdapter;
import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.databinding.ActivityConnectionLogsBinding;
import esw.peeplotech.peerrecords.models.Connection;

public class ConnectionLogs extends AppCompatActivity {

    //binding
    private ActivityConnectionLogsBinding activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_connection_logs);

        //int
        initialize();
    }

    private void initialize() {

        //back
        activity.backBtn.setOnClickListener(v -> onBackPressed());

        //load
        loadLogs();

    }

    private void loadLogs() {

        activity.logRecycler.setHasFixedSize(true);
        activity.logRecycler.setLayoutManager(new LinearLayoutManager(this));

        List<Connection> connectionList = new Database(this).getConnectionLogs();

        activity.logRecycler.setAdapter(new LogAdapter(this, this, connectionList));

    }
}