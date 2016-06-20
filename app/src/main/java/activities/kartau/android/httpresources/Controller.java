package activities.kartau.android.httpresources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;

import activities.kartau.android.httpresources.jsonparser.GetInfoParser;
import activities.kartau.android.httpresources.jsonparser.NewSessionParser;
import activities.kartau.android.httpresources.jsonparser.PullGroupsParser;
import activities.kartau.android.httpresources.jsonparser.UpdateLocationParser;
import activities.kartau.android.staticdata.CommonValues;
import activities.kartau.android.staticdata.Session;
import activities.kartau.android.staticdata.User;


/**
 * Created by Artom on 2015-10-01.
 */
public class Controller {

    public int login(){
        System.out.println("LOGGING IN");
       int errorCode = getSession();
        if(errorCode == CommonValues.PASS) {
            System.out.println("SESSION VALID, GETTING USER INFO");
            errorCode = getUserInfo();
        }
       return errorCode;
    }

    public int pullGroups(LinkedHashMap<String, String> params){
        ObjectMapper mapper = new ObjectMapper();
        int errorCode = getSession();
        if (errorCode==CommonValues.PASS) {
            params.put(CommonValues.SESSION_TOKEN, Session.getToken());
            PullGroupsParser groups = null;
            try {
                groups = mapper.readValue(HTTPHandler.makeGetRequest(new Request(params, CommonValues.REQUEST_PULL_GROUPS)).getJson(), PullGroupsParser.class);
            } catch (IOException e) {
                System.out.println("ERROR:");
                e.printStackTrace();
                return CommonValues.FAIL;
            }
            User.updateGroups(groups);
            System.out.println("GROUP SIZE: " + User.getGroups().size());
            if(groups.type!=1){
                return groups.content[0].code;
            }
            return groups.type;
        }
        return errorCode;
    }

    public int pushUserInfo(LinkedHashMap<String, String> params){
        ObjectMapper mapper = new ObjectMapper();
        int errorCode = getSession();
        if(errorCode==CommonValues.PASS) {
            params.put(CommonValues.SESSION_TOKEN, Session.getToken());
            GetInfoParser user = null;
            try {
                user = mapper.readValue(HTTPHandler.makeGetRequest(new Request(params, CommonValues.REQUEST_UPDATE_USER)).getJson(), GetInfoParser.class);
            } catch (IOException e) {
                return CommonValues.FAIL;
            }
            User.updateUser(user);
            if(user.type!=1){
                return user.content.code;
            }
            return user.type;
        }
        return errorCode;
    }
    public int getUserInfo(){
        LinkedHashMap<String, String> params = new LinkedHashMap<String,String>();
        params.put(CommonValues.SESSION_TOKEN, Session.getToken());
        ObjectMapper mapper = new ObjectMapper();
        int errorCode = getSession();
        if(errorCode==CommonValues.PASS) {
            params.put(CommonValues.SESSION_TOKEN, Session.getToken());
            GetInfoParser user = null;
            try {
                user = mapper.readValue(HTTPHandler.makeGetRequest(new Request(params, CommonValues.REQUEST_USER_DATA)).getJson(), GetInfoParser.class);
            } catch (IOException e) {
                return CommonValues.FAIL;
            }
            User.updateUser(user);
            if(user.type!=1){
                return user.content.code;
            }
            return user.type;
        }
        return errorCode;
    }

    public int newSession(LinkedHashMap<String, String> params) {
        ObjectMapper mapper = new ObjectMapper();
        NewSessionParser session = null;
        try {
            session = mapper.readValue(HTTPHandler.makeGetRequest(new Request(params, CommonValues.REQUEST_GET_SESSION)).getJson(), NewSessionParser.class);
        } catch (IOException e) {
            return CommonValues.FAIL;
        }
        System.out.println("SESSION CODE: " + session.type);
        System.out.println("SESSION TOKEN IN PARSER: " + session.content.signedSession);
        System.out.println("SESSION EXPIRES IN PARSER: " + session.content.expires);
        Session.updateSession(session);
        System.out.println("SESSION TOKEN: " + Session.getToken());
        System.out.println("SESSION EXPIRES: " + Session.getExpires());
        if(session.type!=1){
            return session.content.code;
        }
        return session.type;
    }


    private int getSession() {
        if ((Session.getExpires() - System.currentTimeMillis() ) < CommonValues.MILLISECONDS_TEN_MINUTES) {
            System.out.println("SESSION EXPIRED, GETTING NEW SESSION");
            System.out.println("USERNAME: " + User.getUsername());
            System.out.println("MANAGER: " + User.getDevice());
            System.out.println("PASSWORD: " + User.getPassword());
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put(CommonValues.USERNAME, User.getUsername());  //username)
            params.put(CommonValues.DEVICE, User.getDevice());  //email)
            params.put(CommonValues.PASSWORD, User.getPassword()); //password)
            return newSession(params);
        }
        return CommonValues.PASS;
    }

    public int updateLocation(LinkedHashMap<String, String> params){
        System.out.println("SETTING LOCATION");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("GETTING SESSION");
        int errorCode = getSession();
        if(errorCode==CommonValues.PASS) {
            System.out.println("SESSION FOUND, CONTACTING SERVER");
            params.put(CommonValues.SESSION_TOKEN, Session.getToken());
            UpdateLocationParser location = null;
            try {
                location = mapper.readValue(HTTPHandler.makeGetRequest(new Request(params, CommonValues.REQUEST_SET_POSSITION)).getJson(), UpdateLocationParser.class);
            } catch (IOException e) {
                System.out.println("Error code is "+errorCode);
                e.printStackTrace();
                return CommonValues.FAIL;
            }
            System.out.println("Error code is "+errorCode);

            return location.type;
        }

        return errorCode;
    }

}
