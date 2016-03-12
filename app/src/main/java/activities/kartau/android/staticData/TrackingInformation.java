package activities.kartau.android.staticdata;

/**
 * Created by Artom on 2015-10-01.
 */
public class TrackingInformation {
    private static double lat = 0;
    private static double lon = 0;
    private static double accuracy = 0;
    private static final Object lockLat = new Object();
    private static final Object lockLong = new Object();
    private static final Object lockAcc = new Object();


    public static double getLat(){
        synchronized (lockLat){return lat;}
    }
    public static double getLon(){
        synchronized (lockLong) {return lon;}
    }
    public static double getAccuracy(){
        synchronized (lockAcc) {return accuracy;}
    }


    public static void setLat(double lat){
        synchronized (lockLat) {TrackingInformation.lat=lat;}
    }
    public static void setLon(double lon) {
        synchronized (lockLong){ TrackingInformation.lon=lon;}
    }
    public static void setAccuracy(double accuracy) {
        synchronized (lockAcc){ TrackingInformation.accuracy=accuracy;}
    }
}
