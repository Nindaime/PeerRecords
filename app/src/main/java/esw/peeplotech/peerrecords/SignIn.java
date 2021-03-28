package esw.peeplotech.peerrecords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.databinding.ActivitySignInBinding;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.staff.StaffDashboard;
import esw.peeplotech.peerrecords.students.StudentDashboard;
import esw.peeplotech.peerrecords.util.Common;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    //data binding
    private ActivitySignInBinding activityBinding;

    //logged in
    private String userId, userType;

    //loading
    private android.app.AlertDialog theDialog;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        //set data
        userId = Paper.book().read(Common.USER_ID);
        userType = Paper.book().read(Common.USER_TYPE);

        //check if any user is logged in
        if (userId != null && !userId.isEmpty()){

            if (userType.equals(Common.USER_TYPE_STUDENT)){

                startActivity(new Intent(this, StudentDashboard.class));
                finish();

            } else {

                startActivity(new Intent(this, StaffDashboard.class));
                finish();

            }

        } else {

            //init
            intialize();

        }
    }

    private void intialize() {

        //register
        activityBinding.registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });

        //log in
        activityBinding.loginBtn.setOnClickListener(v -> {

            validate();

        });

    }

    private void validate() {

        String theUsername = activityBinding.userName.getText().toString().trim();
        String thePassword = activityBinding.userPassword.getText().toString().trim();

        //check
        if (TextUtils.isEmpty(theUsername)){

            activityBinding.userName.requestFocus();
            activityBinding.userName.setError("required");

        } else

        if (TextUtils.isEmpty(thePassword)){

            activityBinding.userPassword.requestFocus();
            activityBinding.userPassword.setError("required");

        } else {

            loginUser(theUsername, thePassword);

        }

    }

    private void loginUser(String theUsername, String thePassword) {

        //start loading
        activityBinding.setIsLoading(true);

        //check if user exists
        if (!new Database(this).userExists(theUsername)){

            //stop loading
            activityBinding.setIsLoading(false);

            //show error
            showInfoDialog("Error", "User with username \" " + theUsername + " \" does not exist in database. Please, register.");

        } else {

            //login
            if (new Database(this).loginUser(theUsername, thePassword)){

                //sign user in
                checkUserType(new Database(this).getUserDetails(theUsername));

            } else {
                //stop loading
                activityBinding.setIsLoading(false);

                //show error
                showInfoDialog("Wrong Password", "Password you provided is wrong. Enter correct one and try again.");
            }

        }

    }

    private void checkUserType(User currentUser) {

        //store in local
        Paper.book().write(Common.USER_ID, currentUser.getUsername());
        Paper.book().write(Common.USER_TYPE, currentUser.getUser_type());
        Paper.book().write(Common.CURRENT_USER, currentUser);

        //check
        Intent dashboardIntent = null;

        if (currentUser.getUser_type().equals(Common.USER_TYPE_STUDENT)){
            dashboardIntent = new Intent(this, StudentDashboard.class);
        } else {
            dashboardIntent = new Intent(this, StaffDashboard.class);
        }

        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashboardIntent);
        finish();

    }

    private void showInfoDialog(String title, String message){

        //change state
        isDialogShowing = true;

        //create dialog
        theDialog = new android.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.general_info_dialog,null);

        //widget
        TextView dialogTitle = viewOptions.findViewById(R.id.dialogTitle);
        TextView dialogText = viewOptions.findViewById(R.id.dialogText);
        TextView okayBtn = viewOptions.findViewById(R.id.okayBtn);

        //dialog props
        theDialog.setView(viewOptions);
        theDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        theDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set dialog listener
        theDialog.setOnCancelListener(dialogInterface -> isDialogShowing = false);
        theDialog.setOnDismissListener(dialogInterface -> isDialogShowing = false);

        //lock dialog
        theDialog.setCancelable(true);
        theDialog.setCanceledOnTouchOutside(true);

        //set message
        dialogTitle.setText(title);
        dialogText.setText(message);

        //okay
        okayBtn.setOnClickListener(view -> theDialog.dismiss());

        //show dialog
        theDialog.show();

    }
}