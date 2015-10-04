package activities.kartau.android.httpresources.jsonparser;

/**
 * Created by Artom on 2015-10-03.
 */
public class NewSessionParser {
    public static class content {
        public long expires = 0;
        public String signedSession = "";

        public int code = 0;
        public String message = "";
    }
    public int type = 0;
    public content content;
}
