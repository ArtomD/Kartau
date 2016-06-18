package activities.kartau.android.staticdata;

/**
 * Created by Artom on 2015-10-01.
 */
public class CommonValues {

    /*ERROR MESSAGE MAPPING*/
    public static final int PASS = 1;
    public static final int FAIL = 0;

    /*HTTP API CALL MAPPING*/
    public static final int REQUEST_USER_DATA = 1;
    public static final int REQUEST_UPDATE_USER = 2;
    public static final int REQUEST_UPDATE_PASSWORD = 3;
    public static final int REQUEST_PULL_GROUPS = 4;
    public static final int REQUEST_GET_SESSION = 5;
    public static final int REQUEST_SET_POSSITION = 6;

    /*READ/WRITE TO MEMORY MAPPING*/
    public static final String USERNAME = "username";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String MANAGER_USERNAME = "managerEmail";
    public static final String PASSWORD = "password";
    public static final String USER_CRYPTID = "cryptId";
    public static final String UPDATE_INTERVAL = "updateInterval";
    public static final String SESSION_TOKEN = "token";
    public static final String SESSION_EXPIRES = "expires";
    public static final String IS_LOGIN = "login";
    public static final String GROUPS = "groups";
    public static final String LAT = "lat";
    public static final String LONG = "long";
    public static final String ACCURACY = "accuracy";

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /*BROADCAST SETUP*/
    public static final int MAX_INTERVAL = 60;
    public static final int MIN_INTERVAL = 5;
    public static final int MIN_DISTANCE = 5;
    public static final int MILLISECONDS_ONE_SECOND = 1000;
    public static final int MILLISECONDS_TEN_MINUTES = 60000;

    /*CONNECTION UPDATE MAPPING*/
    public static final String INTENT_MESSAGE = "message";
    public static final String UPDATER_STATUS_OFF = "off";
    public static final String UPDATER_STATUS_ON = "on";
    public static final String UPDATER_STATUS_PROBLEM = "problem";

    /*UPDATER THREAD ERROR MAPPING*/
    public static final int UPDATER_THEAD_OFF = 0;
    public static final int UPDATER_THEAD_ON = 1;
    public static final int UPDATER_THEAD_PROBLEM = 2;

    /* BROADCAST MAPS COLORS */
    public static final String ACTIVE_TEXT_COLOR = "#a15912";
    public static final String ACTIVE_BACKGROUND_COLOR = "#ffd24d";
    public static final String INACTIVE_TEXT_COLOR = "#000000";
    public static final String INACTIVE_BACKGROUND_COLOR = "#e6e6e6";
    public static final String MAP_LAYOUT_BACKGROUND_COLOR = "#f2cc5a";

    /*BROADCAST MAPS ANIMATION SETUP*/
    public static int MAP_LAYOUT_HEIGHT = 300;
    public static final int RESIZE_ANIMATION_GROW = 650;
    public static final int RESIZE_ANIMATON_SHRINK = 450;
}
