package esw.peeplotech.peerrecords.models;

public class Student {

    private String username;
    private String matric;
    private String department;

    public Student() {
    }

    public Student(String username, String matric, String department) {
        this.username = username;
        this.matric = matric;
        this.department = department;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMatric() {
        return matric;
    }

    public void setMatric(String matric) {
        this.matric = matric;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
