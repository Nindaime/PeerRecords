package esw.peeplotech.peerrecords.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.interfaces.ItemClickListener;
import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.students.StudentDetails;
import esw.peeplotech.peerrecords.util.Common;
import esw.peeplotech.peerrecords.util.Methods;
import io.paperdb.Paper;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private Activity activity;
    private Context ctx;
    private List<Record> recordList;

    public RecordAdapter(Activity activity, Context context, List<Record> recordList) {
        this.activity = activity;
        this.ctx = context;
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.record_item, parent, false);

        return new RecordViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {

        //get current student
        Record currentRecord = recordList.get(position);

        //bind
        if (currentRecord.getRecord_status().equals(Common.RECORD_AWARDED)) {
            holder.pointIndicator.setImageResource(R.drawable.ic_added_point);

            //show deduction
            holder.deductPoint.setVisibility(View.VISIBLE);
            holder.deductPoint.setOnClickListener(v -> {

                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to do this?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        (dialog, which) -> {

                            new Database(ctx).deductRecord(currentRecord.getRecord_id(), Common.RECORD_DEDUCTED, Methods.getTimestamp());

                            new Database(ctx).updateRemovedRecord(Paper.book().read(Common.CURRENT_SESSION), currentRecord.getScore());

                            if (ctx instanceof StudentDetails) {
                                ((StudentDetails) ctx).updateDeduction(position);
                            }

                            alertDialog.dismiss();

                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        (dialog, which) -> {

                            alertDialog.dismiss();

                        });

                alertDialog.show();

            });

            //text
            holder.scoreText.setText("@" + currentRecord.getStaff_username() + " awarded " + currentRecord.getScore() + " points to " + currentRecord.getStudent_username() + "\'s record for " + currentRecord.getRecord_reason() + " at " + currentRecord.getTimestamp());
        } else {
            holder.pointIndicator.setImageResource(R.drawable.ic_removed_point);

            //show deduction
            holder.deductPoint.setVisibility(View.GONE);

            //text
            holder.scoreText.setText("@" + currentRecord.getStaff_username() + " removed " + currentRecord.getScore() + " points from " + currentRecord.getStudent_username() +  "\'s record at " + currentRecord.getTimestamp());
        }


    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {

        //widgets
        public ImageView pointIndicator, deductPoint;
        public TextView scoreText;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            pointIndicator = itemView.findViewById(R.id.pointIndicator);
            deductPoint = itemView.findViewById(R.id.deductPoint);
            scoreText = itemView.findViewById(R.id.scoreText);

        }

    }
}
