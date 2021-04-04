package esw.peeplotech.peerrecords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.databinding.ActivitySignInBinding;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.staff.StaffDashboard;
import esw.peeplotech.peerrecords.util.Common;
import esw.peeplotech.peerrecords.util.Methods;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    //data binding
    private ActivitySignInBinding activityBinding;

    //logged in
    private String userId;
    private String sessionToken = "";
    private String android_id;

    //loading
    private android.app.AlertDialog theDialog;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        //set data
        userId = Paper.book().read(Common.USER_ID);

        //check if any user is logged in
        if (userId != null && !userId.isEmpty()){

            startActivity(new Intent(this, StaffDashboard.class));
            finish();

        } else {

            //init
            initialize();

        }
    }

    private void initialize() {

        //generate session token
        generateSessionToken();

        //device id
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //register
        activityBinding.registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });

        //log in
        activityBinding.loginBtn.setOnClickListener(v -> {

            validate();

        });

    }

    private void generateSessionToken() {

        //set token
        String tempToken = generateRandomToken();

        //get token
        if (!new Database(this).isConnectionIdInUse(tempToken)){

            sessionToken = tempToken;

        } else {

            generateSessionToken();

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

        //sign in session
        new Database(this).createNewSession(sessionToken, currentUser.getUsername(), android_id, 0, 0, Methods.getTimestamp(), "");

        //store in local
        Paper.book().write(Common.USER_ID, currentUser.getUsername());
        Paper.book().write(Common.CURRENT_USER, currentUser);
        Paper.book().write(Common.CURRENT_SESSION, sessionToken);

        //check
        Intent dashboardIntent = new Intent(this, StaffDashboard.class);
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