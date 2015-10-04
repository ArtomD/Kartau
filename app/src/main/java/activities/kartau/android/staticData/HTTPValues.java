package activities.kartau.android.staticdata;

/**
 * Created by Artom on 2015-10-01.
 */
public class HTTPValues {
    public static final String SERVER = "https://desolate-everglades-1774.herokuapp.com/";
    public static final int DEFAULT_CALL_TYPE = 1;
    public static final int GET_CALL = 1;
    public static final int GET_CALL_COMPLEX = 2;
    public static final int POST_CALL = 3;


    public static String getAPIFunctionName(int type){
        if(type == 0){
            return "";
        }else if(type == CommonValues.REQUEST_USER_DATA){
            return "user/data/";
        }else if(type == CommonValues.REQUEST_UPDATE_USER){
            return "user/update/";
        }else if(type == CommonValues.REQUEST_UPDATE_PASSWORD){
            return "user/password/";
        }else if(type == CommonValues.REQUEST_PULL_GROUPS){
            return "group/getall/";
        }else if(type == CommonValues.REQUEST_GET_SESSION){
            return "user/session/";
        }else if(type == CommonValues.REQUEST_SET_POSSITION){
            return "tracking/get/setpositions/";
        }
        else return "ERROR";
    }

    public static String getAPIFunctionNameCallType(int type){
        if(type == GET_CALL){
            return "GET";
        }else if(type == GET_CALL_COMPLEX){
            return "GET";
        }else if(type == POST_CALL){
            return "POST";
        }
        else return "ERROR";

    }
}
