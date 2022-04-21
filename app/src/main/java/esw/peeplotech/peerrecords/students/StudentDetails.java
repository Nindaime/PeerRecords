package esw.peeplotech.peerrecords.students;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.adapters.RecordAdapter;
import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.databinding.ActivityStudentDetailsBinding;
import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.models.Student;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.util.Common;
import esw.peeplotech.peerrecords.util.Methods;
import io.paperdb.Paper;

public class StudentDetails extends AppCompatActivity {

    //student
    private Student currentStudent;
    private User currentUser;
    
    //binding
    private ActivityStudentDetailsBinding activity;

    //data
    private RecordAdapter adapter;
    private List<Record> recordList;

    //value
    private int points = 0;
    private String selectedReason = "";
    private String recordToken = "";

    //dialogs
    private AlertDialog awardDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_student_details);

        //intent
        currentStudent = (Student) getIntent().getSerializableExtra(Common.INTENT_STUDENT);
        currentUser = Paper.book().read(Common.CURRENT_USER);
        
        //init
        initialize();
    }

    private void initialize() {
        
        //back
        activity.backBtn.setOnClickListener(v -> onBackPressed());
        
        //data
        activity.setStudentData(currentStudent);
        
        //image
        activity.studentImage.setImageResource(currentStudent.getStudent_image());
        
        //set point
        loadRecords();

        //add points
        activity.awardFab.setOnClickListener(v -> {
            openAwardDialog();
        });
        
    }

    private void openAwardDialog() {

        awardDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.award_layout,null);

        //widgets
        EditText pointsToAward = viewOptions.findViewById(R.id.pointsToAward);
        Spinner reasonSpinner = viewOptions.findViewById(R.id.reasonSpinner);
        RelativeLayout awardBtn = viewOptions.findViewById(R.id.awardBtn);

        //add view properties
        awardDialog.setView(viewOptions);
        awardDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        awardDialog.getWindow().setGravity(Gravity.BOTTOM);
        awardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = awardDialog.getWindow().getAttributes();
        awardDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        awardDialog.show();

        //get token
        generateRecordToken();

        //populate
        switch (currentUser.getSector()){

            case Common.SECTOR_ACADEMIC:
                populateAcademicSpinner(reasonSpinner);
                break;

            case Common.SECTOR_BURSARY:
                populateBursarySpinner(reasonSpinner);
                break;

            case Common.SECTOR_CLASS:
                populateClassSpinner(reasonSpinner);
                break;

            case Common.SECTOR_HOSTEL:
                populateHostelSpinner(reasonSpinner);
                break;

        }

        //award
        awardBtn.setOnClickListener(v -> {

            String thePoint = pointsToAward.getText().toString().trim();

            if (TextUtils.isEmpty(thePoint)){

                pointsToAward.requestFocus();
                pointsToAward.setError("required");

            } else

            if (Integer.parseInt(thePoint) < 1){

                pointsToAward.requestFocus();
                pointsToAward.setError("invalid");

            } else

            if (TextUtils.isEmpty(selectedReason)){

                Toast.makeText(this, "Please give reason for point", Toast.LENGTH_SHORT).show();

            } else {

                awardPoints(thePoint);

                awardDialog.dismiss();

            }

        });

    }

    private void awardPoints(String thePoint) {

        int intPoints = Integer.parseInt(thePoint);

        new Database(this).createNewRecord(recordToken, currentUser.getUsername(), currentStudent.getStudent_username(), intPoints, Methods.getTimestamp(),
                Common.RECORD_AWARDED, selectedReason);

        //update recorde
        recordList.add(new Record(recordToken, currentUser.getUsername(), currentStudent.getStudent_username(), intPoints, Methods.getTimestamp(),
                Common.RECORD_AWARDED, selectedReason));
        adapter.notifyDataSetChanged();

        //update session
        new Database(this).updateAddedRecord(Paper.book().read(Common.CURRENT_SESSION), intPoints);

        selectedReason = "";
        recordToken = "";

    }

    public void updateDeduction(int recordPosition){

        //clear
        recordList.clear();

        //clear
        points = 0;

        loadRecords();

    }

    private void generateRecordToken() {

        //set token
        String tempToken = generateRandomToken();

        //get token
        if (!new Database(this).isRecordIdInUse(tempToken)){

            recordToken = tempToken;

        } else {

            generateRecordToken();

        }


    }

    private String generateRandomToken(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 9;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    private void populateHostelSpinner(Spinner reasonSpinner) {

        final List<String> reasonList = new ArrayList<>();
        reasonList.add(0, "Reason for point");
        reasonList.add(1, Common.HOSTEL_REASON_HYGIENE);
        reasonList.add(2, Common.HOSTEL_REASON_RULES);
        reasonList.add(3, Common.HOSTEL_REASON_SANITATION);
        final ArrayAdapter<String> dataAdapterReason;
        dataAdapterReason = new ArrayAdapter(this, R.layout.custom_spinner_list_item, reasonList);
        dataAdapterReason.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        reasonSpinner.setAdapter(dataAdapterReason);
        dataAdapterReason.notifyDataSetChanged();
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!parent.getItemAtPosition(position).toString().equals("Reason for point")) {

                    selectedReason = parent.getItemAtPosition(position).toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void populateClassSpinner(Spinner reasonSpinner) {

        final List<String> reasonList = new ArrayList<>();
        reasonList.add(0, "Reason for point");
        reasonList.add(1, Common.CLASS_REASON_ATTENDANCE);
        reasonList.add(2, Common.CLASS_REASON_ENGAGEMENT);
        reasonList.add(3, Common.CLASS_REASON_GROUP);
        final ArrayAdapter<String> dataAdapterReason;
        dataAdapterReason = new ArrayAdapter(this, R.layout.custom_spinner_list_item, reasonList);
        dataAdapterReason.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        reasonSpinner.setAdapter(dataAdapterReason);
        dataAdapterReason.notifyDataSetChanged();
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!parent.getItemAtPosition(position).toString().equals("Reason for point")) {

                    selectedReason = parent.getItemAtPosition(position).toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void populateBursarySpinner(Spinner reasonSpinner) {

        final List<String> reasonList = new ArrayList<>();
        reasonList.add(0, "Reason for point");
        reasonList.add(1, Common.BURSARY_REASON_EARLY);
        reasonList.add(2, Common.BURSARY_REASON_FULL);
        final ArrayAdapter<String> dataAdapterReason;
        dataAdapterReason = new ArrayAdapter(this, R.layout.custom_spinner_list_item, reasonList);
        dataAdapterReason.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        reasonSpinner.setAdapter(dataAdapterReason);
        dataAdapterReason.notifyDataSetChanged();
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!parent.getItemAtPosition(position).toString().equals("Reason for point")) {

                    selectedReason = parent.getItemAtPosition(position).toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void populateAcademicSpinner(Spinner reasonSpinner) {

        final List<String> reasonList = new ArrayList<>();
        reasonList.add(0, "Reason for point");
        reasonList.add(1, Common.ACADEMIC_REASON_ASSIGNMENT);
        reasonList.add(2, Common.ACADEMIC_REASON_PERFORMANCE);
        reasonList.add(3, Common.ACADEMIC_REASON_RESULT);
        reasonList.add(4, Common.ACADEMIC_REASON_STANDING);
        final ArrayAdapter<String> dataAdapterReason;
        dataAdapterReason = new ArrayAdapter(this, R.layout.custom_spinner_list_item, reasonList);
        dataAdapterReason.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        reasonSpinner.setAdapter(dataAdapterReason);
        dataAdapterReason.notifyDataSetChanged();
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!parent.getItemAtPosition(position).toString().equals("Reason for point")) {

                    selectedReason = parent.getItemAtPosition(position).toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadRecords() {

        //init recycler
        activity.recordRecycler.setHasFixedSize(true);
        activity.recordRecycler.setLayoutManager(new LinearLayoutManager(this));

        //populate
        recordList = new Database(this).getAllStudentRecords(currentStudent.getStudent_username());

        //total points
        if (recordList.size() > 0) {
            for (Record theRecord : recordList){

                if (theRecord.getRecord_status().equals(Common.RECORD_AWARDED)) {
                    points = points + theRecord.getScore();
                }

            }

            activity.studentPoints.setText(String.valueOf(points));
        } else {
            activity.studentPoints.setText(String.valueOf(points));
        }

        //adapter
        adapter = new RecordAdapter(this, this, recordList);
        activity.recordRecycler.setAdapter(adapter);

    }
}