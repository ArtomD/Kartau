package activities.kartau.android.httpresources.jsonparser;

/**
 * Created by Artom on 2015-10-03.
 */
public class PullGroupsParser {
    public static class content {
        public static class Users{
            public String cryptId = "";
            public String strUsername = "";
            public double lat = 0;
            public double lon = 0;
        }
        public long created = 0;
        public int intStatus = 0;
        public int intType = 0;
        public String strName = "";
        public int token = 0;
        public Users[] users;

        public int code = 0;
        public String message = "";
    }
    public int type = 0;
    public content[] content;

}
