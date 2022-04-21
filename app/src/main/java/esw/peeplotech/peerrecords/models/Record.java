package esw.peeplotech.peerrecords.models;

public class Record {

    private String record_id;
    private String staff_username;
    private String student_username;
    private int score;
    private String timestamp;
    private String record_status;
    private String record_reason;

    public Record() {
    }

    public Record(String record_id, String staff_username, String student_username, int score, String timestamp, String record_status, String record_reason) {
        this.record_id = record_id;
        this.staff_username = staff_username;
        this.student_username = student_username;
        this.score = score;
        this.timestamp = timestamp;
        this.record_status = record_status;
        this.record_reason = record_reason;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getStaff_username() {
        return staff_username;
    }

    public void setStaff_username(String staff_username) {
        this.staff_username = staff_username;
    }

    public String getStudent_username() {
        return student_username;
    }

    public void setStudent_username(String student_username) {
        this.student_username = student_username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecord_status() {
        return record_status;
    }

    public void setRecord_status(String record_status) {
        this.record_status = record_status;
    }

    public String getRecord_reason() {
        return record_reason;
    }

    public void setRecord_reason(String record_reason) {
        this.record_reason = record_reason;
    }
}
