package activities.kartau.android.staticdata;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Artom on 2015-10-01.
 */
public class User {
    static private String
            username,
            managerUsername,
            email,
            password,
            firstName,
            lastName;

    private static final Object lockInterval = new Object();
    static private int interval = CommonValues.MIN_INTERVAL;

    static private LinkedList<Groups> groups;
    static private Util RW;

    public User(Util RW){
        User.RW = RW;
    }


    public static void setRW (Util RW){User.RW = RW;}
    public static void setPassword(String password){ User.password = password; }
    public static void setUsername(String username){ User.username = username; }
    public static void setEmail(String email){
        User.email = email;
    }
    public static void setFirstName(String firstName){
        User.firstName = firstName;
    }
    public static void setLastName(String lastName){
        User.lastName = lastName;
    }
    public static void setManagerUsername(String managerUsername) { User.managerUsername = managerUsername; }
    public static void setGroups(LinkedList<Groups> groups) { User.groups = groups; }

    public static  String getUsername(){
        return username;
    }
    public static void setInterval(int interval) { synchronized (lockInterval){ User.interval=interval;} }


    public static  String getEmail(){
        return email;
    }
    public static  String getPassword(){
        if(User.password==null || User.password.equals(""))
            User.password = RW.readData(CommonValues.PASSWORD);
        return User.password;
    }
    public static  String getFirstName(){
        System.out.println("FIRST NAME: " + RW.readData(CommonValues.FIRST_NAME));
        System.out.println("LAST NAME: " + RW.readData(CommonValues.LAST_NAME));
        if(User.firstName==null  || User.firstName.equals(""))
            User.firstName = RW.readData(CommonValues.FIRST_NAME);
        return firstName;
    }
    public static  String getLastName(){
        if(User.lastName==null  || User.lastName.equals(""))
            User.lastName = RW.readData(CommonValues.LAST_NAME);
        return lastName;
    }
    public static String  getManagerUsername() {
        if(User.managerUsername==null  || User.managerUsername.equals(""))
            User.managerUsername = RW.readData(CommonValues.MANAGER_USERNAME);
        return managerUsername; }
    public static LinkedList<Groups> getGroups() {
        return groups; }
    public static int getInterval(){
        synchronized (lockInterval){
            return interval; }
    }

    //this static method parses the response to the getInfo HTTP request
    //it takes a response object
    //it populates the static fields in the user class
    //it returns an error code, 1 is expected, 0 is if there is no response JSON
    public  static int getInfoParse(Response HTTPresponce){
        try {
            //creates a new JSONObject from the response
            JSONObject jObject = new JSONObject(HTTPresponce.getJson());
            //type 1 means no problems
            if(jObject.getInt("type")==1) {
                //creates a new JSONObject from the inner json
                JSONObject innerjObject = new JSONObject(jObject.getString("content"));
                //records the fields of the json
                User.managerUsername = innerjObject.getString("strUsername");
                User.email = innerjObject.getString("strEmail");
                User.firstName = innerjObject.getString("strFirstName");
                User.lastName = innerjObject.getString("strLastName");
                return 1;
            }else{
                //returns error code
                JSONObject innerjObject = new JSONObject(jObject.getString("content"));
                return innerjObject.getInt("code");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //code reached if there is no valid json to parse
        return 0;
    }

    //this static method parses the response to the updateUser HTTP request
    //it takes a response object
    //it populates the static fields in the user class
    //it returns an error code, 1 is expected, 0 is if there is no response json
    public  static int updateUserParse(Response HTTPresponce){
        try {
            //creates a new JSONObject from the response
            JSONObject jObject = new JSONObject(HTTPresponce.getJson());
            //type 1 means no problems
            if(jObject.getInt("type")==1) {
                //creates a new JSONObject from the inner json
                JSONObject innerjObject = new JSONObject(jObject.getString("content"));
                //records the fields of the json
                User.username = innerjObject.getString("strUsername");
                User.email = innerjObject.getString("strEmail");
                User.firstName = innerjObject.getString("strFirstName");
                User.lastName = innerjObject.getString("strLastName");
                return 1;
            }else{
                //returns error code
                JSONObject innerjObject = new JSONObject(jObject.getString("content"));
                return innerjObject.getInt("code");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //code reached if there is no valid json to parse
        return 0;
    }
    //this method saves the user login information on internal memory
    public static void saveUser(Util RW) {
        RW.storeData(CommonValues.USERNAME, User.username);
        RW.storeData(CommonValues.PASSWORD, User.password);
        System.out.println("ANDREW: " + User.firstName);
        System.out.println("ANDREW: " +  User.firstName);
        RW.storeData(CommonValues.FIRST_NAME, User.firstName);
        RW.storeData(CommonValues.LAST_NAME, User.lastName);
        RW.storeData(CommonValues.MANAGER_USERNAME, User.managerUsername);
        RW.storeData(CommonValues.UPDATE_INTERVAL, String.valueOf(User.interval));

        System.out.println("FIRST NAME: " + RW.readData(CommonValues.FIRST_NAME));
        System.out.println("LAST NAME: " + RW.readData(CommonValues.LAST_NAME));
    }
    //this method clears the internal and java memory regarding user profile and groups information
    public static void clearUser(Util RW) {
        User.managerUsername = "";
        User.username = "";
        User.password = "";
        User.email = "";
        User.firstName = "";
        User.lastName = "";
        User.interval = 0;

        RW.storeData(CommonValues.USERNAME, "");
        RW.storeData(CommonValues.PASSWORD, "");
        RW.storeData(CommonValues.MANAGER_USERNAME, "");
        RW.storeData(CommonValues.GROUPS, "");
        RW.storeData(CommonValues.UPDATE_INTERVAL, "");


    }
}
