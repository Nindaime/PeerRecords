package esw.peeplotech.peerrecords.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Methods {

    //capitalize first letter
    public static String capitalizeFirst(String rawString){
        String s1 = rawString.substring(0, 1).toUpperCase();
        return s1 + rawString.substring(1);
    }

    //get today's date
    @SuppressLint("SimpleDateFormat")
    public static String getTodayDate(){
        return new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }

}
