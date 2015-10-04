package activities.kartau.android.httpresources.jsonparser;

/**
 * Created by Artom on 2015-10-03.
 */
public class GetInfoParser {
    public static class content {
        public String cryptId = "";
        public String strEmail = "";
        public String strFirstName = "";
        public String strLastName = "";
        public String strUsername = "";

        public int code = 0;
        public String message = "";
    }
    public int type = 0;
    public content content;
}
