package esw.peeplotech.peerrecords.staff;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.SignIn;
import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.databinding.ActivityStaffDashboardBinding;
import esw.peeplotech.peerrecords.fragments.AllRecords;
import esw.peeplotech.peerrecords.fragments.MyRecords;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.util.Common;
import esw.peeplotech.peerrecords.util.Methods;
import io.paperdb.Paper;

public class StaffDashboard extends AppCompatActivity {

    //binding
    private ActivityStaffDashboardBinding activity;

    //fragment
    private MyRecords myRecords = new MyRecords();
    private AllRecords allRecords = new AllRecords();

    //dialogs
    private AlertDialog connectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_staff_dashboard);

        //init
        initialize();
    }

    private void initialize() {

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

                if (item.getItemId() == R.id.action_logs){

                    //go to connection logs
                    Intent logsIntent = new Intent(this, ConnectionLogs.class);
                    startActivity(logsIntent);
                    return true;

                } else

                if (item.getItemId() == R.id.action_logout){

                    //end session
                    new Database(this).logoutSession(Paper.book().read(Common.CURRENT_SESSION), Methods.getTimestamp());

                    //destroy local db
                    Paper.book().delete(Common.USER_ID);
                    Paper.book().delete(Common.CURRENT_USER);
                    Paper.book().delete(Common.CURRENT_SESSION);

                    //go to sign in
                    Intent logoutIntent = new Intent(this, SignIn.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    finish();
                    return true;

                }
                return false;
            });

            popupInvalidate.show();

        });
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
        View viewOptions = inflater.inflate(R.layout.connection_layout,null);

        //widgets
        RelativeLayout joinConnectionBtn = viewOptions.findViewById(R.id.joinConnectionBtn);
        TextView joinConnectionText = viewOptions.findViewById(R.id.joinConnectionText);
        AVLoadingIndicatorView joinConnectionProgress = viewOptions.findViewById(R.id.joinConnectionProgress);
        RelativeLayout hostConnectionBtn = viewOptions.findViewById(R.id.hostConnectionBtn);
        TextView hostConnectionText = viewOptions.findViewById(R.id.hostConnectionText);
        AVLoadingIndicatorView hostConnectionProgress = viewOptions.findViewById(R.id.hostConnectionProgress);
        RelativeLayout disconnectBtn = viewOptions.findViewById(R.id.disconnectBtn);
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

    }

    @Override
    public void onBackPressed() {
        if (!activity.getIsMyRecord()){

            setBaseFragment(myRecords);

        } else {

            finish();

        }
    }
}