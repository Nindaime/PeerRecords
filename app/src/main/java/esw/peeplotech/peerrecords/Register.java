package esw.peeplotech.peerrecords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
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

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.databinding.ActivityRegisterBinding;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.staff.StaffDashboard;
import esw.peeplotech.peerrecords.util.Common;
import esw.peeplotech.peerrecords.util.Methods;
import io.paperdb.Paper;

public class Register extends AppCompatActivity {

    //data binding
    private ActivityRegisterBinding activityBinding;

    //values
    private String selectedSector;
    private String sessionToken = "";
    private String android_id;

    //loading
    private AlertDialog theDialog, studentDialog, staffDialog;
    private boolean isDialogShowing = false;

    //image upload
    private static final int VERIFY_PERMISSIONS_REQUEST = 757;
    private static final int GALLERY_REQUEST_CODE = 665;
    private Uri imageUri;
    private String imageLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        //init
        initialize();
    }

    private void initialize() {

        //generate session token
        generateSessionToken();

        //device id
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //back
        activityBinding.backButton.setOnClickListener(v -> onBackPressed());

        //login link
        activityBinding.loginLink.setOnClickListener(v -> onBackPressed());

        //add avatar
        activityBinding.changeAvatar.setOnClickListener(v -> {

            //check permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                showGallery();

            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, VERIFY_PERMISSIONS_REQUEST);

            }

        });

        //register
        activityBinding.registerBtn.setOnClickListener(v -> validateParams());

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

    private void validateParams() {

        //string
        String theUsername = activityBinding.userName.getText().toString().trim();
        String thePassword = activityBinding.userPassword.getText().toString().trim();
        String thePasswordConfirm = activityBinding.confirmPassword.getText().toString().trim();

        //check
        if (TextUtils.isEmpty(theUsername)){

            activityBinding.userName.requestFocus();
            activityBinding.userName.setError("required");

        } else

        if (TextUtils.isEmpty(thePassword)){

            activityBinding.userPassword.requestFocus();
            activityBinding.userPassword.setError("required");

        } else

        if (!thePasswordConfirm.equals(thePassword)){

            activityBinding.confirmPassword.requestFocus();
            activityBinding.confirmPassword.setError("mismatch");

        } else {

            registerUser(theUsername, thePassword);

        }

    }

    private void registerUser(String theUsername, String thePassword) {

        //start loading
        activityBinding.setIsLoading(true);

        //check username
        if (!new Database(this).userExists(theUsername)){

            //register new user
            new Database(this).registerNewUser(theUsername, thePassword, imageLink);

            //show finish dialog
            openStaffDialog(theUsername);

        } else {

            //stop loading
            activityBinding.setIsLoading(false);

            //show info
            showInfoDialog("Username taken", "Sorry, username \"" + theUsername + "\" you picked is already in use by another user.");

        }

    }

    private void showGallery() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , GALLERY_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == VERIFY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                showGallery();

            } else {

                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){

            if (data.getData() != null) {
                imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(Register.this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //get data
                Uri resultUri = result.getUri();


                //avatar folder
                File avatarFolder = new File(Environment.getExternalStorageDirectory(), Common.BASE_FOLDER + "/" + Common.AVATAR_FOLDER);
                if (!avatarFolder.exists()){
                    avatarFolder.mkdirs();
                }

                //avatar file
                File avatarFile = new File(avatarFolder.getAbsolutePath(), generateRandomToken() + ".jpg");

                //copy file
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new FileInputStream(resultUri.getPath());
                    out = new FileOutputStream(avatarFile.getAbsolutePath());

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;

                } catch (FileNotFoundException fileError) {
                    Log.d("FileError", "File Error: " + fileError.getMessage());
                } catch (Exception e) {
                    Log.d("FileError", "Process Error: " + e.getMessage());
                }


                //create image uri
                Uri uri = Uri.fromFile(new File(avatarFile.getAbsolutePath()));
                imageLink = uri.toString();


                //set image
                activityBinding.setUserAvatarUrl(imageLink);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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

    private void openStaffDialog(String username) {

        staffDialog = new AlertDialog.Builder(this, R.style.DialogTheme).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.register_staff_layout,null);

        //widgets
        EditText staffNumber = viewOptions.findViewById(R.id.staffNumber);
        Spinner staffSector = viewOptions.findViewById(R.id.staffSector);
        RelativeLayout completeBtn = viewOptions.findViewById(R.id.completeBtn);

        //add view properties
        staffDialog.setView(viewOptions);
        staffDialog.getWindow().getAttributes().windowAnimations = R.style.SlideDialogAnimation;
        staffDialog.getWindow().setGravity(Gravity.BOTTOM);
        staffDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //add windows properties
        WindowManager.LayoutParams layoutParams = staffDialog.getWindow().getAttributes();
        staffDialog.getWindow().setAttributes(layoutParams);

        //show dialog
        staffDialog.show();

        //populate sector
        final List<String> sectorList = new ArrayList<>();
        sectorList.add(0, "Job Sector");
        sectorList.add(1, Common.SECTOR_ACADEMIC);
        sectorList.add(2, Common.SECTOR_HOSTEL);
        sectorList.add(3, Common.SECTOR_CLASS);
        sectorList.add(4, Common.SECTOR_BURSARY);

        //adapter
        final ArrayAdapter<String> dataAdapterUser;
        dataAdapterUser = new ArrayAdapter(this, R.layout.custom_spinner_list_item, sectorList);
        dataAdapterUser.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        //set adapter
        staffSector.setAdapter(dataAdapterUser);
        dataAdapterUser.notifyDataSetChanged();

        //selector
        staffSector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!parent.getItemAtPosition(position).toString().equals("Job Sector")) {

                    selectedSector = parent.getItemAtPosition(position).toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //add
        completeBtn.setOnClickListener(v -> {

            //validate
            String theStaffId = staffNumber.getText().toString().trim();

            //check
            if (TextUtils.isEmpty(theStaffId)){

                staffNumber.requestFocus();
                staffNumber.setError("required");

            } else

            if (TextUtils.isEmpty(selectedSector)){

                Toast.makeText(this, "Please, select your sector", Toast.LENGTH_SHORT).show();

            } else {

                //register student
                new Database(this).registerNewStaff(username, selectedSector, theStaffId);

                //dismiss dialog
                staffDialog.dismiss();

                //update
                updateUI(username);

            }

        });

    }

    private void updateUI(String username) {

        //sign in session
        new Database(this).createNewSession(sessionToken, username, android_id, 0, 0, Methods.getTimestamp(), "");

        //get user data
        User currentUser = new Database(this).getUserDetails(username);

        //store in local
        Paper.book().write(Common.USER_ID, currentUser.getUsername());
        Paper.book().write(Common.CURRENT_USER, currentUser);
        Paper.book().write(Common.CURRENT_SESSION, sessionToken);

        //check
        Intent dashboardIntent =  new Intent(this, StaffDashboard.class);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dashboardIntent);
        finish();

    }

    private void showInfoDialog(String title, String message){

        //change state
        isDialogShowing = true;

        //create dialog
        theDialog = new AlertDialog.Builder(this).create();
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