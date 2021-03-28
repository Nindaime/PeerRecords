package esw.peeplotech.peerrecords.models;

public class Staff {

    private String username;
    private String staff_id;
    private String sector;

    public Staff() {
    }

    public Staff(String username, String staff_id, String sector) {
        this.username = username;
        this.staff_id = staff_id;
        this.sector = sector;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
