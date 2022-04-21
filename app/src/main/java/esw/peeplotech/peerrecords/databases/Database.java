package esw.peeplotech.peerrecords.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import esw.peeplotech.peerrecords.models.Connection;
import esw.peeplotech.peerrecords.models.Record;
import esw.peeplotech.peerrecords.models.Student;
import esw.peeplotech.peerrecords.models.User;
import esw.peeplotech.peerrecords.util.Common;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "GENERAL_DB.db";
    private static final int DB_VER = 1;
    public static final String USERS_TABLE = "Users";
    public static final String RECORDS_TABLE = "Records";
    public static final String CONNECTIONS_TABLE = "Connections";

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
    public void registerNewUser(String username, String password, String avatar){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO " + USERS_TABLE + " (username, password, avatar) VALUES('%s', '%s', '%s');",
                username,
                password,
                avatar);
        db.execSQL(query);
    }

    //update staff sector
    public void registerNewStaff(String username, String sector, String staff_id){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + USERS_TABLE + " SET sector = '%s', staff_id = '%s'  WHERE username = '%s';",
                sector,
                staff_id,
                username);
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
                    cursor.getString(cursor.getColumnIndex("avatar")),
                    cursor.getString(cursor.getColumnIndex("sector")),
                    cursor.getString(cursor.getColumnIndex("staff_id"))
            );

        }
        cursor.close();

        return currentUser;
    }

    //get all users
    public List<User> getAllStaff() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = "SELECT * FROM " + USERS_TABLE + ";";
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
                            cursor.getString(cursor.getColumnIndex("avatar")),
                            cursor.getString(cursor.getColumnIndex("sector")),
                            cursor.getString(cursor.getColumnIndex("staff_id"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }











    /*---   RECORDS   ---*/
    //check if record id is already in use
    public boolean isRecordIdInUse(String recordId){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + RECORDS_TABLE + " WHERE record_id = '%s';", recordId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //create record
    public void createNewRecord(String record_id, String staff_username, String student_username, int score, String timestamp, String record_status, String record_reason){
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("INSERT INTO " + RECORDS_TABLE + " (record_id, staff_username, student_username, score, timestamp, record_status, record_reason) VALUES('%s', '%s', '%s', '%d', '%s', '%s', '%s');",
                record_id,
                staff_username,
                student_username,
                score,
                timestamp,
                record_status,
                record_reason);
        db.execSQL(query);
    }

    //get record data
    public Record getRecordDetails(String recordId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + RECORDS_TABLE + " WHERE record_id = '%s';", recordId);

        //make null user
        Record currentRecord = null;

        //run query
        cursor = db.rawQuery(SQLQuery, null);

        //check again if data exists
        if (cursor.getCount() > 0){

            cursor.moveToFirst();
            currentRecord = new Record(
                    cursor.getString(cursor.getColumnIndex("record_id")),
                    cursor.getString(cursor.getColumnIndex("staff_username")),
                    cursor.getString(cursor.getColumnIndex("student_username")),
                    cursor.getInt(cursor.getColumnIndex("score")),
                    cursor.getString(cursor.getColumnIndex("timestamp")),
                    cursor.getString(cursor.getColumnIndex("record_status")),
                    cursor.getString(cursor.getColumnIndex("record_reason"))
            );

        }
        cursor.close();

        return currentRecord;
    }

    //update record
    public void updateRecord(String recordId, String timestamp, String record_status){
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("UPDATE " + RECORDS_TABLE + " SET timestamp = '%s', record_status = '%s'  WHERE record_id = '%s';", timestamp, record_status, recordId);
        db.execSQL(query);
    }

    //deduct record
    public void deductRecord(String record_id, String record_status, String timestamp){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + RECORDS_TABLE + " SET record_status = '%s', timestamp = '%s'  WHERE record_id = '%s';",
                record_status,
                timestamp,
                record_id);
        db.execSQL(query);
    }

    //check if record already exists here
    public boolean isRecordAlreadyHere(String recordId, String staff_username, String student_username){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + RECORDS_TABLE + " WHERE record_id = '%s' AND staff_username = '%s' AND student_username = '%s';",
                recordId,
                staff_username,
                student_username);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //get student records
    public List<Record> getAllStudentRecords(String username) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + RECORDS_TABLE + " WHERE student_username = '%s' ORDER BY timestamp DESC;", username);
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
                            cursor.getString(cursor.getColumnIndex("timestamp")),
                            cursor.getString(cursor.getColumnIndex("record_status")),
                            cursor.getString(cursor.getColumnIndex("record_reason"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //get all records
    public List<Record> getAllRecords() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = "SELECT * FROM " + RECORDS_TABLE + " ORDER BY timestamp DESC;";
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
                            cursor.getString(cursor.getColumnIndex("timestamp")),
                            cursor.getString(cursor.getColumnIndex("record_status")),
                            cursor.getString(cursor.getColumnIndex("record_reason"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

    //search records
    public List<Record> getQueriedRecords(String searchQuery) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        //String SQLQuery = String.format(, searchQuery);
        String SQLQuery = "SELECT * FROM " + RECORDS_TABLE + " WHERE student_username LIKE '%" + searchQuery + "%' ORDER BY timestamp DESC;";
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
                            cursor.getString(cursor.getColumnIndex("timestamp")),
                            cursor.getString(cursor.getColumnIndex("record_status")),
                            cursor.getString(cursor.getColumnIndex("record_reason"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }












    /*---   connections   ---*/
    //check if record id is already in use
    public boolean isConnectionIdInUse(String session_id){
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM " + CONNECTIONS_TABLE + " WHERE session_id = '%s';", session_id);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    //create session
    public void createNewSession(String sessionId, String staffUsername, String nodeId, int pointsAdded, int pointsRemoved, String loginTimestamp, String logoutTimestamp){
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("INSERT INTO " + CONNECTIONS_TABLE + " (session_id, staff_username, node_id, points_added, points_removed, login_timestamp, logout_timestamp) " +
                        "VALUES('%s', '%s', '%s', '%d', '%d', '%s', '%s');",
                sessionId,
                staffUsername,
                nodeId,
                pointsAdded,
                pointsRemoved,
                loginTimestamp,
                logoutTimestamp);
        db.execSQL(query);
    }

    //logout session
    public void logoutSession(String sessionId, String logoutTimestamp){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE " + CONNECTIONS_TABLE + " SET logout_timestamp = '%s'  WHERE session_id = '%s';",
                logoutTimestamp,
                sessionId);
        db.execSQL(query);
    }

    //add to added points
    public void updateAddedRecord(String sessionId, int points){
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("UPDATE " + CONNECTIONS_TABLE + " SET points_added = points_added + '%d' WHERE session_id = '%s';", points, sessionId);
        db.execSQL(query);
    }

    //add to removed points
    public void updateRemovedRecord(String sessionId, int points){
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("UPDATE " + CONNECTIONS_TABLE + " SET points_added = points_added - '%d' WHERE session_id = '%s';", points, sessionId);
        db.execSQL(query);
    }

    //get all connection log
    public List<Connection> getConnectionLogs() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = "SELECT * FROM " + CONNECTIONS_TABLE + " ORDER BY login_timestamp DESC;";
        cursor = db.rawQuery(SQLQuery, null);

        //init list
        final List<Connection> result = new ArrayList<>();

        //if file exist
        if (cursor.getCount()>0) {

            if (cursor.moveToFirst()){
                do {
                    result.add(new Connection(
                            cursor.getString(cursor.getColumnIndex("session_id")),
                            cursor.getString(cursor.getColumnIndex("staff_username")),
                            cursor.getString(cursor.getColumnIndex("node_id")),
                            cursor.getInt(cursor.getColumnIndex("points_added")),
                            cursor.getInt(cursor.getColumnIndex("points_removed")),
                            cursor.getString(cursor.getColumnIndex("login_timestamp")),
                            cursor.getString(cursor.getColumnIndex("logout_timestamp"))
                    ));
                }while (cursor.moveToNext());
            }
        }

        return result;
    }

}
