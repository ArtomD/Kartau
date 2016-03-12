package activities.kartau.android.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Artom on 2015-10-01.
 */
public class MyContext extends Application {

    private static Context context;

    public void onCreate(){
            super.onCreate();
            MyContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
            return MyContext.context;
        }

}
