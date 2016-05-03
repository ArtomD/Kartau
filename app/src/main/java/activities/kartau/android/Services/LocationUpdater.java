package activities.kartau.android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import activities.kartau.android.gui.BroadcastMaps;
import activities.kartau.android.gui.R;
import activities.kartau.android.httpresources.Controller;
import activities.kartau.android.httpresources.Response;
import activities.kartau.android.staticdata.CommonValues;
import activities.kartau.android.staticdata.Groups;
import activities.kartau.android.staticdata.Session;
import activities.kartau.android.staticdata.TrackingInformation;
import activities.kartau.android.staticdata.User;
import activities.kartau.android.util.ReadWrite;

public class LocationUpdater extends Service {
    ReadWrite RW;
    double tempLat;
    double tempLong;
    int status = CommonValues.UPDATER_THEAD_OFF;
    int updateStatus = CommonValues.UPDATER_THEAD_OFF;
    int oldStatus = CommonValues.UPDATER_THEAD_OFF;
    int counter = 0;
    Thread thread;
    boolean notificationRemoved = true;
    boolean setTime=false;
    boolean timedLoop = false;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        Notification notification = new Notification();
        createNotification(this, "Connection Established", "Kartau is running", "Connected to server");
        startForeground(1, notification);
        super.onCreate();
        this.RW = new ReadWrite(this);
        thread = new Thread() {
            @Override
            public void run() {
                while (true)
                {
                    //get the list of groups that are active to receive updates from this user
                    //record the current time
                    long time = System.currentTimeMillis();

                    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                    Controller comm = new Controller();
                    params.put(CommonValues.SESSION_TOKEN, Session.getToken());
                    //make an instance of the HTTP controller
                    comm.pullGroups(params);


                    String groupList = RW.readData(CommonValues.GROUPS);
                    status = CommonValues.UPDATER_THEAD_ON;
                    if (groupList == null || groupList.equals("")) {
                        //if no groups are selected skip call
                        System.out.println("NO GROUPS FOUND");
                        status = CommonValues.UPDATER_THEAD_OFF;
                    }else if(!Session.getProvider()){
                        System.out.println("GPS TURNED OFF");
                        status = CommonValues.UPDATER_THEAD_OFF;

                    } else if (true/*isLatLocationChanged(TrackingInformation.getLat(), tempLat) || isLonLocationChanged(TrackingInformation.getLon(), tempLong)*/) {
                        //make the LinkedList with the necessary call arguments
                        params = new LinkedHashMap<String, String>();
                        params.put(CommonValues.SESSION_TOKEN, Session.getToken());
                        params.put(CommonValues.GROUPS, groupList);
                        params.put(CommonValues.LAT, String.valueOf(TrackingInformation.getLat()));
                        params.put(CommonValues.LONG, String.valueOf(TrackingInformation.getLon()));
                        params.put(CommonValues.ACCURACY, String.valueOf(TrackingInformation.getAccuracy()));

                        //make an instance of the HTTP controller
                        comm = new Controller();

                        //run the HTTP request

                        if (comm.updateLocation(params) == CommonValues.PASS) {
                            status = CommonValues.UPDATER_THEAD_ON;
                            tempLat = TrackingInformation.getLat();
                            tempLong = TrackingInformation.getLon();
                        } else {
                            status = CommonValues.UPDATER_THEAD_PROBLEM;
                        }

                    }
                    //update the activites to the link status
                    updateStatus();
                    //sleep thread for the remaining time
                    if (System.currentTimeMillis() < time + (User.getInterval() * CommonValues.MILLISECONDS_ONE_SECOND)) {
                        try {
                            Thread.sleep((User.getInterval() * CommonValues.MILLISECONDS_ONE_SECOND - (System.currentTimeMillis() - time)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        thread.interrupt();
    }


    private double caclAccLat(double lat){
        double rlat = lat*Math.PI/180;
        return 111132.92 - 559.82 * Math.cos(2*rlat) + 1.175*Math.cos(4*rlat);
    }
    private double caclAccLon(double lon){
        double rlat = lon*Math.PI/180;
        return 111412.84 * Math.cos(rlat) - 93.5 * Math.cos(3*rlat);
    }
    private boolean isLatLocationChanged(double val1, double val2){
        if((caclAccLat(val1)*Math.abs(val1-val2))<CommonValues.MIN_DISTANCE)
            return false;
        return true;
    }
    private boolean isLonLocationChanged(double val1, double val2){
        if((caclAccLon(val1)*Math.abs(val1-val2))<CommonValues.MIN_DISTANCE)
            return false;
        return true;
    }

    private void updateStatus() {
        Intent intent = new Intent("update-maps");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        System.out.println("status: " + status);
        intent = new Intent("connection-status");
        if (status == CommonValues.UPDATER_THEAD_OFF) {
            intent.putExtra(CommonValues.INTENT_MESSAGE, CommonValues.UPDATER_STATUS_OFF);
            Session.setStatus(CommonValues.UPDATER_STATUS_OFF);
            updateStatus = CommonValues.UPDATER_THEAD_OFF;
            counter = 0;
        } else if (status == CommonValues.UPDATER_THEAD_ON) {
            intent.putExtra(CommonValues.INTENT_MESSAGE, CommonValues.UPDATER_STATUS_ON);
            Session.setStatus(CommonValues.UPDATER_STATUS_ON);
            updateStatus = CommonValues.UPDATER_THEAD_ON;
            counter = 0;
        } else if (status == CommonValues.UPDATER_THEAD_PROBLEM ) {
            intent.putExtra(CommonValues.INTENT_MESSAGE, CommonValues.UPDATER_STATUS_PROBLEM);
            Session.setStatus(CommonValues.UPDATER_STATUS_PROBLEM);
            updateStatus = CommonValues.UPDATER_THEAD_PROBLEM;
            counter++;
        }
        System.out.println("updateStatus: " + updateStatus);
        if(updateStatus!=oldStatus) {

            System.out.println("NOTIFICATION UPTIME IS: " + checkNotification());

            if (status == CommonValues.UPDATER_THEAD_PROBLEM ) {
                createNotification(this, "Connection Error", "Lost connection to server", "Connection to server failed");
            } else if (status != CommonValues.UPDATER_THEAD_PROBLEM) {
                createNotification(this, "Connection Established", "Kartau is running", "Connected to server");
            }

            oldStatus = updateStatus;
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    public void createNotification(Context context, String msg , String msgText , String msgAlert){


        PendingIntent notificIntent = PendingIntent.getActivities(context , 0 ,
                new Intent[]{new Intent(context, BroadcastMaps.class)},0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)

                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);
        if(msg.equals("Connection Established"))
            mBuilder.setSmallIcon(R.drawable.statuson);
        else
            mBuilder.setSmallIcon(R.drawable.statusproblem);


        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(0);
        mBuilder.setSound(null);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(false);
        //mBuilder.setDeleteIntent(notificIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(1,mBuilder.build());
    }

    private boolean checkNotification(){
        Intent notificationIntent = new Intent(this, BroadcastMaps.class);
        PendingIntent test = PendingIntent.getActivity(this, 2, notificationIntent, PendingIntent.FLAG_NO_CREATE);

        return test != null;
    }
}
