package esw.peeplotech.peerrecords.models;

public class Connection {

    private String session_id;
    private String staff_username;
    private String node_id;
    private int points_added;
    private int points_removed;
    private String login_timestamp;
    private String logout_timestamp;

    public Connection() {
    }

    public Connection(String session_id, String staff_username, String node_id, int points_added, int points_removed, String login_timestamp, String logout_timestamp) {
        this.session_id = session_id;
        this.staff_username = staff_username;
        this.node_id = node_id;
        this.points_added = points_added;
        this.points_removed = points_removed;
        this.login_timestamp = login_timestamp;
        this.logout_timestamp = logout_timestamp;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getStaff_username() {
        return staff_username;
    }

    public void setStaff_username(String staff_username) {
        this.staff_username = staff_username;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public int getPoints_added() {
        return points_added;
    }

    public void setPoints_added(int points_added) {
        this.points_added = points_added;
    }

    public int getPoints_removed() {
        return points_removed;
    }

    public void setPoints_removed(int points_removed) {
        this.points_removed = points_removed;
    }

    public String getLogin_timestamp() {
        return login_timestamp;
    }

    public void setLogin_timestamp(String login_timestamp) {
        this.login_timestamp = login_timestamp;
    }

    public String getLogout_timestamp() {
        return logout_timestamp;
    }

    public void setLogout_timestamp(String logout_timestamp) {
        this.logout_timestamp = logout_timestamp;
    }
}
