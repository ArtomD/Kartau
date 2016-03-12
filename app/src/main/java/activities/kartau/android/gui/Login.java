package activities.kartau.android.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedHashMap;

import activities.kartau.android.httpresources.Controller;
import activities.kartau.android.services.LocationTracker;
import activities.kartau.android.services.LocationUpdater;
import activities.kartau.android.staticdata.CommonValues;
import activities.kartau.android.staticdata.Session;
import activities.kartau.android.staticdata.User;
import activities.kartau.android.util.AlertBuilder;
import activities.kartau.android.util.ReadWrite;


public class Login extends ActionBarActivity {

    ReadWrite RW;
    private Menu menu;
    private User user;
    private Session session;
    Intent locationUpdater;
    Intent locationTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //call super method
        super.onCreate(savedInstanceState);
        //initialize instance of the Util class to read/write data to internal storage
        this.RW = new ReadWrite(this.getApplicationContext());
        this.user = new User(RW);
        this.session = new Session(RW);

        //set the main view target layout
        setContentView(R.layout.activity_loading);
        //check if a user is currently logged in and bypass login screen if true
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        locationUpdater = new Intent(this, LocationUpdater.class);
        locationTracker = new Intent(this, LocationTracker.class);

        if((RW.readData(CommonValues.IS_LOGIN)).equals(CommonValues.TRUE)) {
            try {
                //run the login method with arguments from internal storage
                new HTTPRequestTask(this).execute(RW.readData(CommonValues.USERNAME), RW.readData(CommonValues.MANAGER_USERNAME), RW.readData(CommonValues.PASSWORD));

            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }
        }else{
            setContentView(R.layout.activity_login);
            actionBar.setTitle(R.string.login_title);
            actionBar.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_login, menu);
        //hide the logout button on launch before login occurs
        hideOption(R.id.action_logout);
        hideOption(R.id.action_status);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //set up the menu logic
        switch (item.getItemId()) {
            case R.id.action_about:
                goAbout();
                return true;
            case R.id.action_support:
                goSupport();
                return true;
            case R.id.action_logout:
                goLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onRestart(){
        super.onRestart();
        //hide the logout button if no user is logged in when activity is visited from another activity
        if((RW.readData(CommonValues.IS_LOGIN)).equals(CommonValues.FALSE)) {
            hideOption(R.id.action_logout);
        }
    }

    @Override
    public void onDestroy(){
        stopService(locationTracker);
        stopService(locationUpdater);
        super.onDestroy();
    }


    //go to the about page
    public void goAbout() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    //go to the support page
    public void goSupport() {
        Intent intent = new Intent(this, Support.class);
        startActivity(intent);
    }


    //runs the login method
    public void goLogin(View view){


        //check if all the fields are filled in
        if (((EditText) findViewById(R.id.loginUsername)).getText().toString().trim().equals("") ||
                ((EditText) findViewById(R.id.loginDevice)).getText().toString().trim().equals("") ||
                ((EditText) findViewById(R.id.loginPassword)).getText().toString().trim().equals("")) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.popup_missing_cred_title)
                            .setMessage(R.string.popup_missing_cred_message)
                            .setPositiveButton(R.string.popup_button_okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
        }else {
            //run the login method with the arguments entered by user

            new HTTPRequestTask(this).execute(((EditText) findViewById(R.id.loginUsername)).getText().toString().trim(),
                    ((EditText) findViewById(R.id.loginDevice)).getText().toString().trim(),
                    ((EditText) findViewById(R.id.loginPassword)).getText().toString().trim());
            setContentView(R.layout.activity_loading);
        }
    }

    //main async thread to login and populate user info
    private class HTTPRequestTask extends AsyncTask<String, Void, Integer> {

        private Login activity;
        private String username,device;


        //constructor, used to give access to the main thread
        public HTTPRequestTask(Login activity){
            this.activity = activity;
        }
        //does on execute call
        //passes to onPostExecute when done
        @Override
        protected Integer doInBackground(String... data) {
            //build arguments for HTTP call in LinkedHashMap
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put(CommonValues.USERNAME, data[0]);  //username)
            params.put(CommonValues.MANAGER_USERNAME, data[1]);  //manager_username)
            params.put(CommonValues.PASSWORD, data[2]); //password)
            //set the users password and manager username
            //the user username is set when his account information is populated
            User.setUsername(data[0]);
            User.setManagerUsername(data[1]);
            User.setPassword(data[2]);
            //make an instance of the HTTP controller
            Controller comm = new Controller();
            //set loading screen

            //try to get a new session
            System.out.println("Getting Session");
            int error = comm.login();
            if(error != CommonValues.PASS){
                //if the session call fails return error code
                //the code is a string because the AsyncTask class does not pass primitive types
                this.username = data[0];
                this.device = data[1];
            }
            return error;
        }

        //starts when doInBackground finishes
        //has access to the main views
        @Override
        protected void onPostExecute(Integer error) {
            if(error == CommonValues.PASS) {
                //if get session is successful save the user's login information to internal storage
                try {
                    User.setInterval(Integer.parseInt(RW.readData(CommonValues.UPDATE_INTERVAL)));
                } catch (NumberFormatException e) {
                    User.setInterval(CommonValues.MIN_INTERVAL);
                }
                User.saveUser(RW);
                //record the state as logged in
                RW.storeData(CommonValues.IS_LOGIN, CommonValues.TRUE);

                startService(activity.locationTracker);
                startService(activity.locationUpdater);

                Intent intent = new Intent(activity,BroadcastMaps.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }else {
                boolean isLoading;
                try{
                    isLoading = activity.findViewById(R.id.loadingLayout).getVisibility()==View.VISIBLE;
                }catch(Exception e){
                    isLoading = true;
                }
                if(isLoading) {
                    ActionBar actionBar = activity.getSupportActionBar();
                    activity.setContentView(R.layout.activity_login);
                    EditText usertext = (EditText)activity.findViewById(R.id.loginUsername);
                    EditText devicetext = (EditText)activity.findViewById(R.id.loginDevice);
                    usertext.setText(username, TextView.BufferType.EDITABLE);
                    devicetext.setText(device, TextView.BufferType.EDITABLE);
                    actionBar.setTitle(R.string.login_title);
                    actionBar.show();
                }

                else {
                    //if get session failed display error message
                    System.out.println("ERROR CODE: " + error);
                    AlertBuilder.makeNew(activity,error);

                }
            }
        }
    }

    // ------ These methods are avaliable once the user logs in --------
//
//    //go to the profile page
//    public void goProfile(View view) {
//        Intent intent = new Intent(this, Profile.class);
//        startActivity(intent);
//    }
//
//    //go to the broadcast groups page
//    public void goBroadcast(View view) {
//        Intent intent = new Intent(this,BroadcastMaps.class);
//        startActivity(intent);
//    }
//
//    public void goWebsiteLogin (View view) {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kartau.com/login"));
//        startActivity(browserIntent);
//
//    }


    //this methods logs the user out by clearing all java runtime an internal system memory
    public void goLogout() {
        //login state is set to false
        RW.storeData(CommonValues.IS_LOGIN, "false");
        //clear internal memory
        User.clearUser(RW);
        Session.clearSession();
        //hide the menu button to logout
        hideOption(R.id.action_logout);
        //switch the vie to the login layout
        //findViewById(R.id.login).setVisibility(View.GONE);
        //findViewById(R.id.layout1).setVisibility(View.VISIBLE);
    }

    //these two methods are for hiding and showing menu buttons
    //they use the menu object already set by the class
    private void hideOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }
}
