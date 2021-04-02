package esw.peeplotech.peerrecords.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.models.Connection;
import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.util.Common;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private Activity activity;
    private Context ctx;
    private List<Connection> logList;

    public LogAdapter(Activity activity, Context context, List<Connection> logList) {
        this.activity = activity;
        this.ctx = context;
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.log_item, parent, false);

        return new LogViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {

        //get current student
        Connection currentLog = logList.get(position);

        //bind
        holder.logUser.setText("User: " + currentLog.getStaff_username());
        holder.logDevice.setText("Device: " + currentLog.getNode_id());
        holder.logAdded.setText("Added " + currentLog.getPoints_added() + " points");
        holder.logRemoved.setText("Deducted " + currentLog.getPoints_removed() + " points");
        holder.logLogin.setText("Logged in: " + currentLog.getLogin_timestamp());
        holder.logLogout.setText("Logged out: " + currentLog.getLogout_timestamp());

    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {

        //widgets
        public TextView logUser, logDevice, logAdded, logRemoved, logLogin, logLogout;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            logUser = itemView.findViewById(R.id.logUser);
            logDevice = itemView.findViewById(R.id.logDevice);
            logAdded = itemView.findViewById(R.id.logAdded);
            logRemoved = itemView.findViewById(R.id.logRemoved);
            logLogin = itemView.findViewById(R.id.logLogin);
            logLogout = itemView.findViewById(R.id.logLogout);

        }

    }
}
