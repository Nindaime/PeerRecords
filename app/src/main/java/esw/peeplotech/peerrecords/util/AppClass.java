package esw.peeplotech.peerrecords.util;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.models.Student;
import io.paperdb.Paper;

public class AppClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //context paper db init
        Paper.init(getApplicationContext());

        //picasso cache mode
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        //create default channel
        createNotificationChannel();

        //set activity listener
        setupActivityListener();

        //init students
        if (Paper.book().read(Common.STUDENTS_LIST) == null) {
            initStudents();
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Common.DEFAULT_NOTIFICATION_CHANNEL,
                    "DefaultChannel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setShowBadge(false);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                }
            @Override
            public void onActivityStarted(Activity activity) {
            }
            @Override
            public void onActivityResumed(Activity activity) {

            }
            @Override
            public void onActivityPaused(Activity activity) {

            }
            @Override
            public void onActivityStopped(Activity activity) {
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private void initStudents() {

        //create student list
        List<Student> studentList = new ArrayList<>();

        //create record list
        List<Record> recordList = new ArrayList<>();

        //create students
        studentList.add(new Student("AdeolaKoker", "Adeola Koker", R.drawable.adeola, "Computer Science", recordList));
        studentList.add(new Student("AyoSimon", "Ayo Simon", R.drawable.ayo, "Cell Biology and Genetics", recordList));
        studentList.add(new Student("CarlyShay", "Carly Shay", R.drawable.carly, "English Education", recordList));
        studentList.add(new Student("DamiDave", "Damilola David", R.drawable.dami, "Mass Communication", recordList));
        studentList.add(new Student("JessRobb", "Jessica Robert", R.drawable.jess, "Industrial Chemistry", recordList));
        studentList.add(new Student("SaintLammy", "Lamide Agboola", R.drawable.lamide, "Arts and Cultural Education", recordList));
        studentList.add(new Student("ModupeMary", "Modupe Afolabi", R.drawable.modupe, "International Relations", recordList));
        studentList.add(new Student("TatePower", "Ian Tate", R.drawable.tate, "Business Administration", recordList));
        studentList.add(new Student("Tejj26", "Tejj Parker", R.drawable.tejj, "History and Strategic Studies", recordList));

        //save to local
        Paper.book().write(Common.STUDENTS_LIST, studentList);

    }
}


