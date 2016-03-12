package activities.kartau.android.httpresources;

/**
 * Created by Artom on 2015-10-01.
 */
public class Response {
    private String json;
    private int errorCode;
    private String message;

    public void setJson(String json){
        this.json = json;
    }
    public void setErrorCode(int errorCode){
        this.errorCode = errorCode;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public String getJson(){ return this.json; }
    public int getErrorCode(){ return this.errorCode; }
    public String getMessage(){ return this.message; }
}
