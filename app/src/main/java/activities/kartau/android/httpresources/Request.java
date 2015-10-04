package activities.kartau.android.httpresources;

import java.util.LinkedHashMap;
import java.util.Map;

import activities.kartau.android.staticdata.HTTPValues;

/**
 * Created by Artom on 2015-10-01.
 */
public class Request {

    private String server = HTTPValues.SERVER;
    private int callType = HTTPValues.DEFAULT_CALL_TYPE;
    private String function;
    private LinkedHashMap<String,String> params;

    //default constructor
    public Request(){
    }

    //constructor takes a linked hash map object with the call parameters and the type of function call to make
    public Request(LinkedHashMap<String,String> params, int type){
        this.params = params;
        this.function = HTTPValues.getAPIFunctionName(type);
        buildAddressString();
    }

    //constructor takes a linked hash map object with the call parameters, the type of function call to make, the server an the HTTP call type
    public Request(LinkedHashMap<String,String> params, int type, String server, int callType){
        this.params = params;
        this.function = HTTPValues.getAPIFunctionName(type);
        this.server = server;
        this.callType = callType;
        buildAddressString();
    }

    void setServer(String server){ this.server = server; }
    void setCallType(int callType){ this.callType = callType; }
    void setfunction(String function){ this.function = function; }
    void setParams(LinkedHashMap<String,String> params){ this.params = params; }

    public String getServer(){
        return this.server;
    }
    public String getFunction(){
        return this.function;
    }
    public Map<String,String> getParams(){
        return this.params;
    }
    public int getCallType(){
        return this.callType;
    }

    //this method returns a sting built from the objects current fields
    public String buildAddressString(){
        String address = this.server;
        if(this.callType == HTTPValues.GET_CALL) {
            address += this.function;
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                address += entry.getValue();
                if (i < (this.params.size() - 1))
                    address += "/";
                i++;
            }
        }else if(this.callType == HTTPValues.GET_CALL_COMPLEX) {

        }else if(this.callType == HTTPValues.POST_CALL) {

        }
        return address;
    }

}
