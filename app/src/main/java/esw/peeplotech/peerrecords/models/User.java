package esw.peeplotech.peerrecords.models;

public class User {

    private String username;
    private String password;
    private String user_type;
    private String avatar;

    public User() {
    }

    public User(String username, String password, String user_type, String avatar) {
        this.username = username;
        this.password = password;
        this.user_type = user_type;
        this.avatar = avatar;
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

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
