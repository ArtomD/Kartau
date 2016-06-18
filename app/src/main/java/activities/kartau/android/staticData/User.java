package activities.kartau.android.staticdata;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;

import activities.kartau.android.httpresources.jsonparser.GetInfoParser;
import activities.kartau.android.httpresources.jsonparser.PullGroupsParser;
import activities.kartau.android.util.ReadWrite;

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
            lastName,
            cryptId;

    private static final Object lockInterval = new Object();
    static private int interval = CommonValues.MIN_INTERVAL;

    static private LinkedList<Groups> groups;
    static private ReadWrite RW;

    public User(ReadWrite RW){
        User.RW = RW;
    }


    public static void setRW (ReadWrite RW){User.RW = RW;}
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
    public static void setCryptId(String cryptId) { User.cryptId = cryptId; }

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

    public static String getCryptId() {
        return cryptId; }

    public static int getInterval(){
        synchronized (lockInterval){
            return interval; }
    }

    public static void updateGroups(PullGroupsParser groups){
        LinkedList<Groups> groupList = new LinkedList<Groups>();
        for(int i = 0; i < groups.content.length; i++){
            groupList.add(new Groups());
            groupList.get(i).setCreated(String.valueOf(groups.content[i].created));
            groupList.get(i).setStatus(String.valueOf(groups.content[i].intStatus));
            groupList.get(i).setName(groups.content[i].strName);
            groupList.get(i).setToken(String.valueOf(groups.content[i].token));
            groupList.get(i).setType(String.valueOf(groups.content[i].intType));
            for(int j = 0; j < groups.content[i].usersGroups.length; j++){

                    groupList.get(i).userList.add(new Users());
                    groupList.get(i).userList.get(j).setCryptID(groups.content[i].usersGroups[j].member.cryptId);
                    groupList.get(i).userList.get(j).setUsername(groups.content[i].usersGroups[j].member.strUsername);
                    groupList.get(i).userList.get(j).setLat(groups.content[i].usersGroups[j].latitude);
                    groupList.get(i).userList.get(j).setLon(groups.content[i].usersGroups[j].longitude);
                    groupList.get(i).userList.get(j).setActive(groups.content[i].usersGroups[j].active);

            }
        }
        Collections.sort(groupList, Groups.GroupNameComparator);
        User.groups = groupList;
    }

    public static void updateUser(GetInfoParser user){
        User.username = user.content.strUsername;
        User.firstName = user.content.strFirstName;
        User.lastName = user.content.strLastName;
        User.email = user.content.strEmail;
        User.cryptId = user.content.cryptId;
    }

    //this method saves the user login information on internal memory
    public static void saveUser(ReadWrite RW) {
        RW.storeData(CommonValues.USERNAME, User.username);
        RW.storeData(CommonValues.PASSWORD, User.password);
        RW.storeData(CommonValues.FIRST_NAME, User.firstName);
        RW.storeData(CommonValues.LAST_NAME, User.lastName);
        RW.storeData(CommonValues.MANAGER_USERNAME, User.managerUsername);
        RW.storeData(CommonValues.UPDATE_INTERVAL, String.valueOf(User.interval));
        RW.storeData(CommonValues.USER_CRYPTID, cryptId);
    }
    //this method clears the internal and java memory regarding user profile and groups information
    public static void clearUser(ReadWrite RW) {
        User.managerUsername = "";
        User.username = "";
        User.password = "";
        User.email = "";
        User.firstName = "";
        User.lastName = "";
        User.interval = 0;
        User.cryptId = "";

        RW.storeData(CommonValues.USERNAME, "");
        RW.storeData(CommonValues.PASSWORD, "");
        RW.storeData(CommonValues.MANAGER_USERNAME, "");
        RW.storeData(CommonValues.GROUPS, "");
        RW.storeData(CommonValues.UPDATE_INTERVAL, "");
        RW.storeData(CommonValues.USER_CRYPTID, "");


    }
}
