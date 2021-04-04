package esw.peeplotech.peerrecords.models;

public class User {

    private String username;
    private String password;
    private String avatar;
    private String sector;
    private String staff_id;

    public User() {
    }

    public User(String username, String password, String avatar, String sector, String staff_id) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.sector = sector;
        this.staff_id = staff_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }
}
