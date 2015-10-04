package activities.kartau.android.httpresources;

/**
 * Created by Artom on 2015-10-01.
 */
public class Response {
    private String json;
    private int errorCode;

    public void setJson(String json){
        this.json = json;
    }
    public void setErrorCode(int errorCode){
        this.errorCode = errorCode;
    }

    public String getJson(){ return this.json; }
    public long getErrorCode(){ return this.errorCode; }
}
