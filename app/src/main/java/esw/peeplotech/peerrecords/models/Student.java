package esw.peeplotech.peerrecords.models;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {

    private String student_username;
    private String student_name;
    private int student_image;
    private String student_department;
    private List<Record> student_record;

    public Student() {
    }

    public Student(String student_username, String student_name, int student_image, String student_department, List<Record> student_record) {
        this.student_username = student_username;
        this.student_name = student_name;
        this.student_image = student_image;
        this.student_department = student_department;
        this.student_record = student_record;
    }

    public String getStudent_username() {
        return student_username;
    }

    public void setStudent_username(String student_username) {
        this.student_username = student_username;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public int getStudent_image() {
        return student_image;
    }

    public void setStudent_image(int student_image) {
        this.student_image = student_image;
    }

    public String getStudent_department() {
        return student_department;
    }

    public void setStudent_department(String student_department) {
        this.student_department = student_department;
    }

    public List<Record> getStudent_record() {
        return student_record;
    }

    public void setStudent_record(List<Record> student_record) {
        this.student_record = student_record;
    }
}
