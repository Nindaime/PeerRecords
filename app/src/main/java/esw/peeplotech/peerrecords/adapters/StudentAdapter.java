package esw.peeplotech.peerrecords.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.interfaces.ItemClickListener;
import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.models.Student;
import esw.peeplotech.peerrecords.students.StudentDetails;
import esw.peeplotech.peerrecords.util.Common;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder>{

    private Activity activity;
    private Context ctx;
    private List<Student> studentList;

    public StudentAdapter(Activity activity, Context context, List<Student> studentList) {
        this.activity = activity;
        this.ctx = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.student_item, parent, false);

        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {

        //get current student
        Student currentStudent = studentList.get(position);
        int totalRecord = 0;

        //bind data
        holder.studentImage.setImageResource(currentStudent.getStudent_image());
        holder.studentName.setText(currentStudent.getStudent_name());
        holder.studentDepartment.setText(currentStudent.getStudent_department());

        //get points
        List<Record> studentRecords = new Database(ctx).getAllStudentRecords(currentStudent.getStudent_username());
        if (studentRecords.size() > 0) {
            for (Record theRecord : studentRecords){

                if (theRecord.getRecord_status().equals(Common.RECORD_AWARDED)) {
                    totalRecord = totalRecord + theRecord.getScore();
                }

            }

            holder.studentPoints.setText(String.valueOf(totalRecord));
        } else {
            holder.studentPoints.setText(String.valueOf(totalRecord));
        }

        //click
        holder.setItemClickListener((view, position1, isLongClick) -> {

            Intent detailsIntent = new Intent(ctx, StudentDetails.class);
            detailsIntent.putExtra(Common.INTENT_STUDENT, currentStudent);
            ctx.startActivity(detailsIntent);

        });

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //widgets
        private ItemClickListener itemClickListener;
        public RoundedImageView studentImage;
        public TextView studentName, studentDepartment, studentPoints;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            //init widgets
            studentImage = itemView.findViewById(R.id.studentImage);
            studentName = itemView.findViewById(R.id.studentName);
            studentDepartment = itemView.findViewById(R.id.studentDepartment);
            studentPoints = itemView.findViewById(R.id.studentPoints);

            //click
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
