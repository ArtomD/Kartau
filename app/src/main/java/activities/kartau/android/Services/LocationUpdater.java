package activities.kartau.android.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationUpdater extends Service {
    public LocationUpdater() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
