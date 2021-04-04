package esw.peeplotech.peerrecords.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.interfaces.DeviceListener;
import esw.peeplotech.peerrecords.interfaces.ItemClickListener;
import esw.peeplotech.peerrecords.models.Connection;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private Activity activity;
    private Context ctx;
    private List<WifiP2pDevice> deviceList;
    private DeviceListener listener;

    public DeviceAdapter(Activity activity, Context context, List<WifiP2pDevice> deviceList, DeviceListener listener) {
        this.activity = activity;
        this.ctx = context;
        this.deviceList = deviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.device_item, parent, false);

        return new DeviceViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {

        //get current student
        WifiP2pDevice currentDevice = deviceList.get(position);

        //bind
        holder.deviceName.setText(currentDevice.deviceName);
        holder.deviceAddress.setText(currentDevice.deviceAddress);

        //click
        holder.setItemClickListener((view, position1, isLongClick) -> {

            listener.onDeviceClicked(currentDevice);

        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //widgets
        private ItemClickListener itemClickListener;
        public TextView deviceName, deviceAddress;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceAddress = itemView.findViewById(R.id.deviceAddress);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
