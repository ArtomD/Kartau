package activities.kartau.android.staticdata;

/**
 * Created by Artom on 2015-10-04.
 */
public class Users {
    private String
            cryptID,
            username;
    private double
            lat,
            lon;

    private static final Object lockLat = new Object();
    private static final Object lockLon = new Object();
    private static final Object lockName = new Object();

    public void setCryptID(String cryptID){
        this.cryptID = cryptID;
    }
    public void setUsername(String username){
        synchronized(lockName){this.username = username;}
    }
    public void setLat(double lat){
        synchronized (lockLat){this.lat = lat;}
    }
    public void setLon(double lon){
        synchronized (lockLon){this.lon = lon;}
    }

    public String getCryptID(){
        return cryptID;
    }
    public String getUsername(){
        synchronized(lockName){ return username;}
    }
    public double getLat(){
        synchronized (lockLat){return lat;}
    }
    public double getLon() { synchronized (lockLon){return lon;} }
}
