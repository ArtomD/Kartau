package activities.kartau.android.gui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;

import activities.kartau.android.httpresources.Controller;
import activities.kartau.android.staticdata.CommonValues;
import activities.kartau.android.staticdata.Session;
import activities.kartau.android.staticdata.User;
import activities.kartau.android.util.AlertBuilder;
import activities.kartau.android.util.ReadWrite;


public class Profile extends ActionBarActivity{


    ReadWrite RW;
    private Menu menu;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(intent.getStringExtra(CommonValues.INTENT_MESSAGE));
            updateStatus();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //initialize instance of the Util class to read/write data to internal storage
        this.RW = new ReadWrite(this.getApplicationContext());
        User.setRW(RW);
        Session.setRW(RW);
        //set the main view target layout
        setContentView(R.layout.activity_profile);
        //find the profile information fields
        TextView username = (TextView)findViewById(R.id.profileUsernameText);
        TextView firstName = (TextView)findViewById(R.id.profileFirstNameText);
        TextView lastName = (TextView)findViewById(R.id.profileLastNameText);
        TextView email = (TextView)findViewById(R.id.profileEmailText);
        //set the user profile infomation from memory
        username.setText(User.getManagerUsername());
        firstName.setText(User.getFirstName());
        lastName.setText(User.getLastName());
        email.setText(User.getEmail());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        updateStatus();
        return super.onCreateOptionsMenu(menu);
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
    public void onResume() {
        super.onResume();
        try{
            updateStatus();
        }catch(Exception e){
        }
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("connection-status"));
    }
    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        //repopulate user information fields from java memeory
        TextView username = (TextView)findViewById(R.id.profileUsernameText);
        TextView firstName = (TextView)findViewById(R.id.profileFirstNameText);
        TextView lastName = (TextView)findViewById(R.id.profileLastNameText);
        TextView email = (TextView)findViewById(R.id.profileEmailText);

        username.setText(User.getManagerUsername(), TextView.BufferType.EDITABLE);
        firstName.setText(User.getFirstName(),TextView.BufferType.EDITABLE);
        lastName.setText(User.getLastName(), TextView.BufferType.EDITABLE);
        email.setText(User.getEmail(), TextView.BufferType.EDITABLE);
    }

    public void updateStatus(){
        switch (Session.getStatus()) {
            case CommonValues.UPDATER_STATUS_OFF:  menu.findItem(R.id.action_status).setIcon(R.drawable.statusoff);
                break;
            case CommonValues.UPDATER_STATUS_ON:  menu.findItem(R.id.action_status).setIcon(R.drawable.statuson);
                break;
            case CommonValues.UPDATER_STATUS_PROBLEM:  menu.findItem(R.id.action_status).setIcon(R.drawable.statusproblem);
                break;
            case "down":  menu.findItem(R.id.action_status).setIcon(R.drawable.statusproblem);
                break;
            default:  //do nothing
                break;
        }
    }

    public void edit(View view){
        TextView username = (TextView)findViewById(R.id.profileUsernameText);
        TextView firstName = (TextView)findViewById(R.id.profileFirstNameText);
        TextView lastName = (TextView)findViewById(R.id.profileLastNameText);
        TextView email = (TextView)findViewById(R.id.profileEmailText);
        Button buttonEdit = (Button)findViewById(R.id.profileButtonEdit);

        EditText usernameEdit = (EditText)findViewById(R.id.profileUsernameEdit);
        EditText firstNameEdit = (EditText)findViewById(R.id.profileFirstNameEdit);
        EditText lastNameEdit = (EditText)findViewById(R.id.profileLastNameEdit);
        EditText emailEdit = (EditText)findViewById(R.id.profileEmailEdit);
        Button buttonSave = (Button)findViewById(R.id.profileButtonSave);

        username.setVisibility(View.GONE);
        firstName.setVisibility(View.GONE);
        lastName.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        buttonEdit.setVisibility(View.GONE);

        usernameEdit.setText(User.getManagerUsername(), TextView.BufferType.EDITABLE);
        firstNameEdit.setText(User.getFirstName(), TextView.BufferType.EDITABLE);
        lastNameEdit.setText(User.getLastName(), TextView.BufferType.EDITABLE);
        emailEdit.setText(User.getEmail(), TextView.BufferType.EDITABLE);

        usernameEdit.setVisibility(View.VISIBLE);
        firstNameEdit.setVisibility(View.VISIBLE);
        lastNameEdit.setVisibility(View.VISIBLE);
        emailEdit.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);

    }

    public void update(View view){
        //run the update profile HTTP request
        String username, firstName, lastName, email;
        String displayUsername, displayFirstName, displayLastName, displayEmail;
        if(((EditText) findViewById(R.id.profileUsernameEdit)).getText().toString().trim().equals("")){
            username = "%20";
            displayUsername = "";
        }
        else {
            username = ((EditText) findViewById(R.id.profileUsernameEdit)).getText().toString().trim();
            displayUsername = username;
        }
        if(((EditText) findViewById(R.id.profileFirstNameEdit)).getText().toString().trim().equals("")){
            firstName = "%20";
            displayFirstName = "";
        }
        else {
            firstName = ((EditText) findViewById(R.id.profileFirstNameEdit)).getText().toString().trim();
            displayFirstName = firstName;
        }
        if(((EditText) findViewById(R.id.profileLastNameEdit)).getText().toString().trim().equals("")){
            lastName = "%20";
            displayLastName = "";
        }
        else {
            lastName = ((EditText) findViewById(R.id.profileLastNameEdit)).getText().toString().trim();
            displayLastName = lastName;
        }
        if(((EditText) findViewById(R.id.profileEmailEdit)).getText().toString().trim().equals("")){
            email = "%20";
            displayEmail = "";
        }
        else {
            email = ((EditText) findViewById(R.id.profileEmailEdit)).getText().toString().trim();
            displayEmail = email;
        }

        TextView usernameText = (TextView)findViewById(R.id.profileUsernameText);
        TextView firstNameText = (TextView)findViewById(R.id.profileFirstNameText);
        TextView lastNameText = (TextView)findViewById(R.id.profileLastNameText);
        TextView emailText = (TextView)findViewById(R.id.profileEmailText);
        Button buttonEdit = (Button)findViewById(R.id.profileButtonEdit);

        EditText usernameEdit = (EditText)findViewById(R.id.profileUsernameEdit);
        EditText firstNameEdit = (EditText)findViewById(R.id.profileFirstNameEdit);
        EditText lastNameEdit = (EditText)findViewById(R.id.profileLastNameEdit);
        EditText emailEdit = (EditText)findViewById(R.id.profileEmailEdit);
        Button buttonSave = (Button)findViewById(R.id.profileButtonSave);

        usernameText.setText(displayUsername);
        firstNameText.setText(displayFirstName);
        lastNameText.setText(displayLastName);
        emailText.setText(displayEmail);

        usernameText.setVisibility(View.VISIBLE);
        firstNameText.setVisibility(View.VISIBLE);
        lastNameText.setVisibility(View.VISIBLE);
        emailText.setVisibility(View.VISIBLE);
        buttonEdit.setVisibility(View.VISIBLE);

        usernameEdit.setVisibility(View.GONE);
        firstNameEdit.setVisibility(View.GONE);
        lastNameEdit.setVisibility(View.GONE);
        emailEdit.setVisibility(View.GONE);
        buttonSave.setVisibility(View.GONE);
        new HTTPRequestTask(this).execute(Session.getToken(), username, firstName, lastName, email);
    }



    //log user out
    public void goLogout(){
        RW.storeData(CommonValues.IS_LOGIN, "false");
        User.clearUser(RW);
        Session.clearSession();
        goNextActivity(Login.class);
    }

    //go to the about page
    public void goAbout() {
        goNextActivity(About.class);
    }

    //go to the support page
    public void goSupport() {
        goNextActivity(Support.class);
    }

    //go to the broadcast groups page
    public void goBroadcast(View view) {
        goNextActivity(BroadcastMaps.class);

    }

    public void goNextActivity(Class nextView){
        Intent intent = new Intent(this, nextView);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }



    //async thread to update user info
    private class HTTPRequestTask extends AsyncTask<String, Void, Integer> {

        private Profile activity;

        //constructor, used to give access to the main thread
        public HTTPRequestTask(Profile activity){
            this.activity = activity;
        }

        //does on execute call
        //passes to onPostExecute when done
        @Override
        protected Integer doInBackground(String... data) {
            //build arguments for HTTP call in LinkedHashMap
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put(CommonValues.SESSION_TOKEN, data[0]); //session
            params.put(CommonValues.USERNAME, data[1]);      //username
            params.put(CommonValues.FIRST_NAME, data[2]);    //first name
            params.put(CommonValues.LAST_NAME, data[3]);     //last name
            params.put(CommonValues.EMAIL, data[4]);         //email
            //make an instance of the HTTP controller
            Controller comm = new Controller();
            //tries to update the user information
            return comm.pushUserInfo(params);

        }

        //starts when doInBackground finishes
        //has access to the main views
        @Override
        protected void onPostExecute(Integer error) {
            if (error == 1) {
                //if the call is successful save the user's login information to internal storage
                User.saveUser(RW);
            } else{
                AlertBuilder.makeNew(activity, error);
            }
        }

    }


}
