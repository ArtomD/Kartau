package activities.kartau.android.httpresources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import activities.kartau.android.httpresources.jsonparser.GetInfoParser;
import activities.kartau.android.httpresources.jsonparser.NewSessionParser;
import activities.kartau.android.httpresources.jsonparser.PullGroupsParser;
import activities.kartau.android.httpresources.jsonparser.UpdateLocationParser;
import activities.kartau.android.staticdata.CommonValues;
import activities.kartau.android.staticdata.Groups;
import activities.kartau.android.staticdata.Session;
import activities.kartau.android.staticdata.User;
import activities.kartau.android.staticdata.Users;

/**
 * Created by Artom on 2015-10-01.
 */
public class Controller {

    public Response login(){
        newSession();
        getProfile();
    }

    public Response pullGroups(LinkedHashMap<String, String> params){
        ObjectMapper mapper = new ObjectMapper();
        params.put(CommonValues.SESSION_TOKEN,getSession());
        PullGroupsParser groups = null;
        try {
            groups =  mapper.readValue(HTTPHandler.makeGetRequest(new Request(params, CommonValues.REQUEST_PULL_GROUPS)).getJson(), PullGroupsParser.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkedList<Groups> groupList = new LinkedList<Groups>();
        for(int i = 0; i < groups.content.length; i++){
            groupList.add(new Groups());
            groupList.get(i).setCreated(String.valueOf(groups.content[i].created));
            groupList.get(i).setStatus(String.valueOf(groups.content[i].intStatus));
            groupList.get(i).setName(groups.content[i].strName);
            groupList.get(i).setToken(String.valueOf(groups.content[i].token));
            groupList.get(i).setType(String.valueOf(groups.content[i].intType));
            for(int j = 0; i < groups.content[i].users.length; j++){
                groupList.get(i).userList.add(new Users());
                groupList.get(i).userList.get(j).setCryptID(groups.content[i].users[j].cryptId);
                groupList.get(i).userList.get(j).setUsername(groups.content[i].users[j].strUsername);
            }
        }

    }

    public Response pushUserInfo(){

    }


    public Response HTTPCall(LinkedHashMap<String, String> params){
        checkSession();
        params.put(CommonValues.SESSION_TOKEN, Session.getToken());
        Request HTTPrequest = new Request(params, requestType);
        Response HTTPresponse = HTTPHandler.makeGetRequest(HTTPrequest);
        return HTTPresponse;
    }

    public Response newSession(LinkedHashMap<String, String> params) {
        Request HTTPrequest = new Request(params, CommonValues.REQUEST_GET_SESSION);
        Response HTTPresponse = HTTPHandler.makeGetRequest(HTTPrequest);
        return HTTPresponse;
    }

    private Response checkSession() {
        Response HTTPResponse = new Response();
        if ((Session.getExpires() - System.currentTimeMillis() ) < 60000) {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put(CommonValues.USERNAME, User.getUsername());  //username)
            params.put(CommonValues.MANAGER_USERNAME, User.getManagerUsername());  //email)
            params.put(CommonValues.PASSWORD, User.getPassword()); //password)
            Response HTTPresponse = newSession(params);
        }
        return HTTPResponse;
    }

    private void ParseResponse(Response HTTPResponse, int callType){
        ObjectMapper mapper = new ObjectMapper();
        try {
        switch(callType){
            case 1:case 2: GetInfoParser infoParser = mapper.readValue(HTTPResponse.getJson(), GetInfoParser.class);
                break;
            case 3:
                break;
            case 4: PullGroupsParser groupParser = mapper.readValue(HTTPResponse.getJson(), PullGroupsParser.class);
                break;
            case 5: NewSessionParser sessionParser = mapper.readValue(HTTPResponse.getJson(), NewSessionParser.class);
                break;
            case 6: UpdateLocationParser locationParser = mapper.readValue(HTTPResponse.getJson(), UpdateLocationParser.class);
                break;
            default:
                break;
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
