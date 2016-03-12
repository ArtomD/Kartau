package activities.kartau.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.HashMap;
import java.util.Map;

import activities.kartau.android.gui.R;

/**
 * Created by Artom on 2016-02-09.
 */
public class AlertBuilder {

    static int[][] errorCodes = {
            {R.string.popup_connection_failed_title, R.string.popup_connection_failed_message},
            {R.string.popup_username_exists_title, R.string.popup_username_exits_message},
            {R.string.popup_email_exists_title, R.string.popup_email_exits_message},
            {R.string.popup_empty_username_title,R.string.popup_empty_username_message},
            {R.string.popup_invalid_username_length_title,R.string.popup_invalid_username_length_message},
            {R.string.popup_invalid_email_format_title,R.string.popup_invalid_email_format_message},
            {R.string.popup_invalid_username_format_title,R.string.popup_invalid_username_format_message},
            {R.string.popup_unknown_error_title,R.string.popup_unkown_error_message},
            {R.string.popup_GPS_OFF_title,R.string.popup_GPS_OFF_message},
            {R.string.popup_remove_from_map_title,R.string.popup_remove_from_map_message}
    };

    static final Map<Integer , int[]> ERRORS = new HashMap<Integer , int[]>() {{
        put(0,    errorCodes[0]);
        put(101, errorCodes[1]);
        put(102,   errorCodes[2]);
        put(104,   errorCodes[3]);
        put(105,   errorCodes[4]);
        put(107,   errorCodes[5]);
        put(108,   errorCodes[6]);
        put(10,   errorCodes[7]);
        put(20,   errorCodes[8]);
        put(30,   errorCodes[9]);
    }};

   public static void makeNew(Activity activity, Integer error){
        new AlertDialog.Builder(activity)
                .setTitle(ERRORS.get(error)[0])
                .setMessage(ERRORS.get(error)[1])
                .setPositiveButton(R.string.popup_button_okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
