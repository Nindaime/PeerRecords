package esw.peeplotech.peerrecords.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.models.Staff;
import esw.peeplotech.peerrecords.models.Student;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.util.Common;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "GENERAL_DB.db";
    private static final int DB_VER = 1;
    public static final String USERS_TABLE = "Users";
    public static final String STAFF_TABLE = "Staff";
    public static final String STUDENTS_TABLE = "Students";
    public static final String RECORDS_TABLE = "Records";

    //init database system
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }







    /*---   USER   ---*/
    //check if user exist
    public boolean userExists(String username){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + USERS_TABLE + " WHERE username = '%s';", username);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //register new user
    public void registerNewUser(String username, String password, String userType, String avatar){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + USERS_TABLE + " (username, password, user_type, avatar) VALUES('%s', '%s', '%s', '%s');",
                username,
                password,
                userType,
                avatar);
        db.execSQL(query);
    }

    //create student file
    public void registerNewStudent(String username, String matric, String department){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + STUDENTS_TABLE + " (username, matric, department) VALUES('%s', '%s', '%s');",
                username,
                matric,
                department);
        db.execSQL(query);
    }

    //create staff file
    public void registerNewStaff(String username, String staff_id, String sector){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + STAFF_TABLE + " (username, staff_id, sector) VALUES('%s', '%s', '%s');",
                username,
                staff_id,
                sector);
        db.execSQL(query);
    }

    //login user
    public boolean loginUser(String username, String password){
        boolean isSuccess = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + USERS_TABLE + " WHERE username = '%s' AND password = '%s';", username, password);

        //check if correct
        cursor = db.rawQuery(SQLQuery, null);
        isSuccess = cursor.getCount() > 0;
        cursor.close();

        return isSuccess;
    }

    //get user data
    public User getUserDetails(String username){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + USERS_TABLE + " WHERE username = '%s';", username);

        //make null user
        User currentUser = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentUser = new User(
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getString(cursor.getColumnIndex("password")),
                    cursor.getString(cursor.getColumnIndex("user_type")),
                    cursor.getString(cursor.getColumnIndex("avatar"))
            );

        }
        cursor.close();

        return currentUser;
    }

    //get student data
    public Student getStudentDetails(String username){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + STUDENTS_TABLE + " WHERE username = '%s';", username);

        //make null user
        Student currentUser = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentUser = new Student(
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getString(cursor.getColumnIndex("matric")),
                    cursor.getString(cursor.getColumnIndex("department"))
            );

        }
        cursor.close();

        return currentUser;
    }

    //get staff data
    public Staff getStaffDetails(String username){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + STAFF_TABLE + " WHERE username = '%s';", username);

        //make null user
        Staff currentUser = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentUser = new Staff(
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getString(cursor.getColumnIndex("staff_id")),
                    cursor.getString(cursor.getColumnIndex("sector"))
            );

        }
        cursor.close();

        return currentUser;
    }

    //get all users
    public List<User> getAllStudents() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + USERS_TABLE + " WHERE user_type = '%s';", Common.USER_TYPE_STUDENT);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<User> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new User(
                            cursor.getString(cursor.getColumnIndex("username")),
                            cursor.getString(cursor.getColumnIndex("password")),
                            cursor.getString(cursor.getColumnIndex("user_type")),
                            cursor.getString(cursor.getColumnIndex("avatar"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }









    /*---   RECORDS   ---*/
    //create record
    public void createNewRecord(String record_id, String staff_username, String student_username, int score, String timestamp){
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("INSERT INTO " + RECORDS_TABLE + " (record_id, staff_username, student_username, score, timestamp) VALUES('%s', '%s', '%s', '%d', '%s');",
                record_id,
                staff_username,
                student_username,
                score,
                timestamp);
        db.execSQL(query);
    }

    //get records
    public List<Record> getAllRecords(String username) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + RECORDS_TABLE + " WHERE student_username = '%s';", username);
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Record> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Record(
                            cursor.getString(cursor.getColumnIndex("record_id")),
                            cursor.getString(cursor.getColumnIndex("staff_username")),
                            cursor.getString(cursor.getColumnIndex("student_username")),
                            cursor.getInt(cursor.getColumnIndex("score")),
                            cursor.getString(cursor.getColumnIndex("timestamp"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }


}
