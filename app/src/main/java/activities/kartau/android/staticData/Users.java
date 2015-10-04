package activities.kartau.android.staticdata;

/**
 * Created by Artom on 2015-10-04.
 */
public class Users {
    private String
            cryptID,
            username;

    public void setCryptID(String cryptID){
        this.cryptID = cryptID;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public String getCryptID(){
        return cryptID;
    }
    public String getUsername(){
        return username;
    }
}
