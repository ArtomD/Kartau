package activities.kartau.android.staticdata;

import activities.kartau.android.httpresources.jsonparser.NewSessionParser;
import activities.kartau.android.util.ReadWrite;

/**
 * Created by Artom on 2015-10-01.
 */
public class Session {
    //this class stores the session token and expiry times as static fields
    private static String token = "";
    private static long expires = 0;
    private static String status = "off";
    private static boolean provider = true;
    private static long update = 0;
    private static final Object lockStatus = new Object();
    private static final Object lockProvider = new Object();
    private static ReadWrite RW;
    private static boolean runThread = true;
    private static final Object lockRunThread = new Object();

    public Session(ReadWrite RW){
        Session.RW = RW;
    }
    public static void setRW(ReadWrite RW){Session.RW = RW;}

    //this static method parses the response to the getSession HTTP request
    //it takes a response object
    //it populates its own static session and expiry fields
    //it returns an error code, 1 is expected, 0 is if there is no response json

    public static void updateSession(NewSessionParser session){
        Session.token = session.content.signedSession;
        Session.expires = session.content.expires;
    }

    public static String getToken(){
        return token;
    }
    public static long getExpires(){ return expires; }
    public static long getUpdate(){ return update; }
    public static String getStatus(){ synchronized (lockStatus){ return status;} }
    public static boolean getProvider(){ synchronized (lockProvider){ return provider;} }
    public static boolean getRunThread(){ synchronized (lockRunThread){ return runThread;} }

    public static void clearSession(){
        Session.token ="";
        Session.expires = 0;
    }

    public static void setUpdate(long update){ Session.update=update; }

    public static void setStatus(String status){
        synchronized (lockStatus) {Session.status = status;}
    }

    public static void setProvider(boolean provider){
        synchronized (lockProvider) {Session.provider = provider;}
    }
    public static void setRunThread(boolean runThread){
        synchronized (lockRunThread) {Session.runThread = runThread;}
    }

}
