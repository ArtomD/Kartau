package activities.kartau.android.gui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import activities.kartau.android.httpresources.Controller;
import activities.kartau.android.services.LocationTracker;
import activities.kartau.android.services.LocationUpdater;
import activities.kartau.android.staticdata.CommonValues;
import activities.kartau.android.staticdata.Groups;
import activities.kartau.android.staticdata.Session;
import activities.kartau.android.staticdata.User;
import activities.kartau.android.util.AlertBuilder;
import activities.kartau.android.util.CancelButtonOnClickListener;
import activities.kartau.android.util.ReadWrite;
import activities.kartau.android.util.ResizeAnimation;
import activities.kartau.android.util.ToggleButtonOnClickListener;


public class BroadcastMaps extends ActionBarActivity implements KartauMapFragment.OnHeadlineSelectedListener {

    ReadWrite RW;
    private SeekBar intervalBar;
    private TextView intervalTracking;
    private Menu menu;
    LinkedHashMap<String, Integer> tokensToID = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Boolean> mapStatus = new LinkedHashMap<String, Boolean>();
    Intent locationUpdater;




    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(intent.getStringExtra("message"));
            updateStatus();
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call super method
        super.onCreate(savedInstanceState);
        this.RW = new ReadWrite(this.getApplicationContext());
        User.setRW(RW);
        Session.setRW(RW);
        Session.setRunThread(true);

        locationUpdater = new Intent(this, LocationUpdater.class);

        //to clear internal memory
        //RW.storeData(CommonValues.GROUPS,"");
        //run the HTTP request to get groups for user
        new HTTPRequestTask(this).execute();
        //set the main view target layout
        setContentView(R.layout.activity_broadcast_maps);
        TextView errorMessage = (TextView)findViewById(R.id.mapsMapLayoutMessage);
        errorMessage.setText("Loading");
        errorMessage.setVisibility(View.VISIBLE);
        //setup the seek bar
        intervalBar = (SeekBar)findViewById(R.id.mapsControlSeekBar);
        intervalTracking = (TextView)findViewById(R.id.mapsControlSeekBarText);
        intervalTracking.setText("Set Notification Interval (" + User.getInterval() + " seconds)");
        intervalBar.setMax(CommonValues.MAX_INTERVAL - CommonValues.MIN_INTERVAL);
        intervalBar.setProgress(User.getInterval() - CommonValues.MIN_INTERVAL);
        intervalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                User.setInterval(seekBar.getProgress() + CommonValues.MIN_INTERVAL);
                RW.storeData(CommonValues.UPDATE_INTERVAL, String.valueOf(seekBar.getProgress() + CommonValues.MIN_INTERVAL));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                intervalTracking.setText("Set Notification Interval (" + (progress + CommonValues.MIN_INTERVAL) + " seconds)");

            }



        });


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_broadcast_maps, menu);
        updateStatus();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //set up menu logic
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
        User.setForceMap(true);
        User.setMapsActive(true);
        CommonValues.MAP_LAYOUT_HEIGHT = getWindowManager().getDefaultDisplay().getHeight()/3;
        if(!Session.getRunThread()) {
            Session.setRunThread(true);
            startService(Session.getUpdater());
            startService(Session.getTracker());
        }
        try{
            updateStatus();
        }catch(Exception e){
        }
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("connection-status"));
        if(!Session.getProvider()){
            AlertBuilder.makeNew(this,20);
            new AlertDialog.Builder(this);
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        User.setMapsActive(false);
        removeFragment();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Session.setRunThread(false);
        stopService(Session.getUpdater());
        super.onDestroy();
    }

    private void removeFragment(){
        LinkedList<Groups> list = User.getGroups();
        LinearLayout frameLayoutView = (LinearLayout) findViewById(R.id.mapsMapLayoutInsideWrapper);
        try {
            int i =0;
            for(LinkedHashMap.Entry<String,Boolean> entry : mapStatus.entrySet()) {

                if(entry.getValue()){
                    System.out.println("TURNING OFF FRAGMENT: " + entry.getKey());
                    System.out.println("I: " + i);
                    System.out.println("BUTTON ID: " + tokensToID.get(list.get(i).getToken()));
                    System.out.println("LAYOUT ID: " + (tokensToID.get(list.get(i).getToken()) + 1000));
                    View view = (frameLayoutView.getChildAt(i));
                    ToggleButton exteriorButton = (ToggleButton) view.findViewById(tokensToID.get(list.get(i).getToken()));
                    View exteriorLayout = ((View) view.getParent()).findViewById(tokensToID.get(list.get(i).getToken()) + 1000);
                    turnOffMap(exteriorButton, exteriorLayout, i,list);
                }
                i++;
            }

        }catch(Exception e){

        }
    }

    public void updateStatus(){
        try {
            switch (Session.getStatus()) {
                case CommonValues.UPDATER_STATUS_OFF:
                    menu.findItem(R.id.action_status).setIcon(R.drawable.statusoff);
                    break;
                case CommonValues.UPDATER_STATUS_ON:
                    menu.findItem(R.id.action_status).setIcon(R.drawable.statuson);
                    break;
                case CommonValues.UPDATER_STATUS_PROBLEM:
                    menu.findItem(R.id.action_status).setIcon(R.drawable.statusproblem);
                    break;
                case "down":
                    menu.findItem(R.id.action_status).setIcon(R.drawable.statusproblem);
                    break;
                default:  //do nothing
                    break;
            }
        }catch(Exception e){

        }
    }

    public void refresh(View view) {
        //run the HTTP request to get groups for user
        TextView errorMessage = (TextView)findViewById(R.id.mapsMapLayoutMessage);
        errorMessage.setText(R.string.loading);
        new HTTPRequestTask(this).execute();
    }

    //go to the about page
    public void goAbout() {
        goNextActivity(About.class);
    }

    //go to the support page
    public void goSupport() {
        goNextActivity(Support.class);
    }

    //go to the profile page
    public void goProfile(View view) {
        goNextActivity(Profile.class);
    }


    //log user out
    public void goLogout() {
        RW.storeData(CommonValues.IS_LOGIN, CommonValues.FALSE);
        User.clearUser(RW);
        Session.clearSession();
        stopService(Session.getTracker());
        stopService(Session.getUpdater());
        Session.setRunThread(false);
        goNextActivity(Login.class);
    }

    public void goNextActivity(Class nextView){
        Intent intent = new Intent(this, nextView);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed(){
//        super.onBackPressed();
//        overridePendingTransition(0, 0);
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onArticleSelected(int position) {

    }


    private class HTTPRequestTask extends AsyncTask<String, Void, Integer> {

        private BroadcastMaps activity;

        //constructor, used to give access to the main thread
        public HTTPRequestTask(BroadcastMaps activty)
        {
            this.activity = activty;
        }

        //does on execute call
        //passes to onPostExecute when done
        @Override
        protected Integer doInBackground(String... data) {
            //build arguments for HTTP call in LinkedHashMap
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put(CommonValues.SESSION_TOKEN, Session.getToken()); //session
            //make an instance of the HTTP controller

            Controller comm = new Controller();
            return comm.pullGroups(params);
        }

        //starts when doInBackground finishes
        //has access to the main views
        @Override
        protected void onPostExecute(Integer error) {
            //get the group list
            LinkedList<Groups> list = User.getGroups();
            int counter =0;
            while(counter<list.size()){
                if(!list.get(counter).getStatus().equals("1"))
                    list.remove(counter);
                else
                    counter++;
            }
            tokensToID = new LinkedHashMap();
            mapStatus = new LinkedHashMap();
            for(int i=0; i<list.size();i++) {
                tokensToID.put(list.get(i).getToken(), i);
                mapStatus.put(list.get(i).getToken(), false);
            }
            //check the list of active groups and delete any that the user is no longer part of
            if (error == 1) {


                //hide the loading text

                TextView errorMessage = (TextView)findViewById(R.id.mapsMapLayoutMessage);
                errorMessage.setVisibility(View.GONE);
                //get the active groups as a comma delimited string from memory
                String groupList = RW.readData(CommonValues.GROUPS);
                //check the list of active groups and delete any that the user is no longer part of
                String[] activeGroups = groupList.split(",");
                String updatedGroupList = "";
                String finalUpdatedGroupList = "";
                for(int i = 0; i<activeGroups.length;i++){
                    for(int j =0;j<list.size();j++){
                        if(activeGroups[i].equals(list.get(j).getToken())){
                            updatedGroupList+=activeGroups[i] + ",";
                            break;
                        }
                    }
                }
                if(updatedGroupList.length()>0)
                    finalUpdatedGroupList = updatedGroupList.substring(0, updatedGroupList.length()-1);
                RW.storeData(CommonValues.GROUPS, finalUpdatedGroupList);

                LinearLayout frameLayoutView = (LinearLayout) findViewById(R.id.mapsMapLayoutInsideWrapper);
                frameLayoutView.removeAllViews();
                LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                System.out.println("LIST SIZE: " + list.size());
                View[] views = new View[list.size()];
                Button[] buttons = new Button[list.size()];

                for(int i =0; i< list.size();i++){
                    System.out.println("ADDING LAYOUT " + (i + 1));
                    View view = inflater.inflate(R.layout.inner_maps_layout,
                            frameLayoutView,false);
                    frameLayoutView.addView(view, params);
                    setupExpandingMapLayout(list, i, tokensToID);
                    setupMainButton(list, i,tokensToID);
                    setupOnOffButton(list, i, groupList,tokensToID);
                    setupClearButton(list, i, activity, tokensToID);
                }

            }else{
                //run error message
                LinearLayout frameLayoutView = (LinearLayout) findViewById(R.id.mapsMapLayoutInsideWrapper);
                frameLayoutView.removeAllViews();
                TextView errorMessage = (TextView)findViewById(R.id.mapsMapLayoutMessage);
                errorMessage.setText("Unable to connect to the server. Please try again.");
                errorMessage.setVisibility(View.VISIBLE);


            }

        }
    }


    private void setupExpandingMapLayout(LinkedList<Groups> list, int i,LinkedHashMap<String, Integer> mapping){

        LinearLayout layout = (LinearLayout) findViewById(R.id.expandingMapLayout);
        layout.setId(mapping.get(list.get(i).getToken()) + 1000);
        layout.setBackgroundColor(Color.parseColor(CommonValues.MAP_LAYOUT_BACKGROUND_COLOR));



    }

    private void setupMainButton(LinkedList<Groups> list, int i,final LinkedHashMap<String, Integer> mapping){
        ToggleButton button = (ToggleButton) findViewById(R.id.mapsDisplayButton);
        button.setId(mapping.get(list.get(i).getToken()));
        button.setText(list.get(i).getName());
        button.setTextOff(list.get(i).getName());
        button.setTextOn(list.get(i).getName());
        setButtonColor(button, CommonValues.INACTIVE_TEXT_COLOR, CommonValues.INACTIVE_BACKGROUND_COLOR);
        button.setOnClickListener(new ToggleButtonOnClickListener(list, i) {
                                      @Override
                                      //call to the click method when toggle is clicked
                                      public void onClick(View v) {
                                          //selects the current button by id

                                          //checks if the button is on or off and run the method to remove or add to group list
                                          ToggleButton button = (ToggleButton) v.findViewById(mapping.get(list.get(i).getToken()));
                                          View layout = findViewById(mapping.get(list.get(i).getToken()) + 1000);


                                          if (!mapStatus.get(list.get(i).getToken())) {
                                              ViewGroup parent = (ViewGroup) v.getParent().getParent().getParent();
                                              System.out.println("CHILD COUNT: " + parent.getChildCount());
                                              for (int j = 0; j < list.size(); j++) {

                                                  if (j != i) {
                                                      View view = parent.getChildAt(j);
                                                      ToggleButton exteriorButton = (ToggleButton) view.findViewById(mapping.get(list.get(j).getToken()));
                                                      View exteriorLayout = ((View) view.getParent()).findViewById(mapping.get(list.get(j).getToken()) + 1000);
                                                      if (mapStatus.get(list.get(j).getToken())) {
                                                          turnOffMap(exteriorButton, exteriorLayout, j, list);
                                                      }
                                                  }

                                              }
                                              setButtonColor(button, CommonValues.ACTIVE_TEXT_COLOR, CommonValues.ACTIVE_BACKGROUND_COLOR);
                                              ResizeAnimation resizeAnimation = new ResizeAnimation(layout, CommonValues.MAP_LAYOUT_HEIGHT, 0);
                                              resizeAnimation.setDuration(CommonValues.RESIZE_ANIMATION_GROW);
                                              layout.startAnimation(resizeAnimation);
                                              FragmentManager fragMan = getFragmentManager();

                                              FragmentTransaction fragTransaction = fragMan.beginTransaction();
                                              String fragmentName = "fragment" + mapping.get(list.get(i).getToken());
                                              Fragment myFrag = new KartauMapFragment().newInstance(list.get(i).getToken());
                                              fragTransaction.add(mapping.get(list.get(i).getToken()) + 1000, myFrag, fragmentName);
                                              fragTransaction.commit();

                                              mapStatus.put(list.get(i).getToken(), true);

                                              System.out.println("TURNING ON FRAGMENT: " + (list.get(i).getToken()));
                                              System.out.println("I: " + i);
                                              System.out.println("BUTTON ID: " + tokensToID.get(list.get(i).getToken()));
                                              System.out.println("LAYOUT ID: " + (tokensToID.get(list.get(i).getToken()) + 1000));

                                          } else {
                                              turnOffMap(button, layout, i, list);
                                          }
                                      }
                                  }
        );
    }

    private void turnOffMap(ToggleButton button, View layout, int i,LinkedList<Groups> list){

            setButtonColor(button, CommonValues.INACTIVE_TEXT_COLOR, CommonValues.INACTIVE_BACKGROUND_COLOR);
            ResizeAnimation resizeAnimation = new ResizeAnimation(layout, -CommonValues.MAP_LAYOUT_HEIGHT, CommonValues.MAP_LAYOUT_HEIGHT);
            resizeAnimation.setDuration(CommonValues.RESIZE_ANIMATON_SHRINK);
            layout.startAnimation(resizeAnimation);

        String fragmentName = "fragment" + tokensToID.get(list.get(i).getToken());
        Fragment fragment = getFragmentManager().findFragmentByTag(fragmentName);
        if(fragment!=null && fragment.isAdded()) {

            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        mapStatus.put(list.get(i).getToken(), false);
    }

    private void setupOnOffButton(LinkedList<Groups> list, int i, String groupList,final LinkedHashMap<String, Integer> mapping){
        ToggleButton button = (ToggleButton) findViewById(R.id.mapsOnOffButton);
        button.setId(mapping.get(list.get(i).getToken()) + 2000);
        setButtonColor(button, CommonValues.INACTIVE_TEXT_COLOR, CommonValues.INACTIVE_BACKGROUND_COLOR);
        button.setTextSize(12);

        //check which toggle buttons need to be turned on
        //and turns them on
        if (groupList == null || groupList.equals("")) {
        } else {
            String[] groupArray = groupList.split(",");
            for (int j = 0; j < groupArray.length; j++) {
                if (groupArray[j].equals(list.get(i).getToken())) {
                    button.setChecked(true);
                    setButtonColor(button, CommonValues.ACTIVE_TEXT_COLOR, CommonValues.ACTIVE_BACKGROUND_COLOR);
                    break;
                }
            }
        }
        button.setOnClickListener(new ToggleButtonOnClickListener(list, i) {
                                      @Override
                                      //call to the click method when toggle is clicked
                                      public void onClick(View v) {
                                          //selects the current button by id

                                          //checks if the button is on or off and run the method to remove or add to group list
                                          ToggleButton button = (ToggleButton) v.findViewById(mapping.get(list.get(i).getToken()) + 2000);
                                          if (!button.isChecked()) {
                                              setButtonColor(button, CommonValues.INACTIVE_TEXT_COLOR, CommonValues.INACTIVE_BACKGROUND_COLOR);
                                              turnOff(list, i);
                                          } else {
                                              setButtonColor(button, CommonValues.ACTIVE_TEXT_COLOR, CommonValues.ACTIVE_BACKGROUND_COLOR);
                                              turnOn(list, i);
                                          }
                                      }
                                  }
        );
    }

    private void setupClearButton(LinkedList<Groups> list, int i, final BroadcastMaps activity, final LinkedHashMap<String, Integer> mapping){
        Button button = (Button) findViewById(R.id.mapsClearButton);
        button.setId(mapping.get(list.get(i).getToken()) + 3000);
        setButtonColor(button, CommonValues.INACTIVE_TEXT_COLOR, CommonValues.INACTIVE_BACKGROUND_COLOR);
        button.setOnClickListener(new CancelButtonOnClickListener(list, i, (ToggleButton) findViewById(mapping.get(list.get(i).getToken()) + 2000)) {
                                      View v;

                                      @Override
                                      //call to the click method when toggle is clicked
                                      public void onClick(View v) {


                                          new AlertDialog.Builder(activity)
                                                  .setTitle("Remove From Map")
                                                  .setMessage("This will remove your current location form the map\nAre you sure?")
                                                  .setPositiveButton(R.string.popup_button_okay, new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          run();
                                                      }
                                                  })
                                                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          // do nothing
                                                      }
                                                  })
                                                  .setIcon(android.R.drawable.ic_dialog_alert)
                                                  .show();
                                      }

                                      public void run() {
                                          button.setChecked(false);
                                          setButtonColor(button, CommonValues.INACTIVE_TEXT_COLOR, CommonValues.INACTIVE_BACKGROUND_COLOR);
                                          turnOff(list, i);
                                          new HTTPRemoveLocation(activity).execute(list.get(i).getToken());

                                      }
                                  }
        );
    }

    private void setButtonColor(Button button,String textColor, String BackgroundColor){
        button.setTextColor(Color.parseColor(textColor));
        button.getBackground().setColorFilter(Color.parseColor(BackgroundColor), PorterDuff.Mode.MULTIPLY);
    }



    private void turnOff(LinkedList<Groups> list, int num) {
        //get the current list of active groups
        String groupList = RW.readData(CommonValues.GROUPS);
        //parse the list into a string array
        String[] groupArray = groupList.split(",");
        //make the new group list string to be filled
        String groups = "";
        int numComma = groupArray.length-2;
        for (int j = 0; j < groupArray.length; j++) {
            //goes through the group list and remakes it minus the group currently selected
            //which is taken out
            if (!(groupArray[j].equals(list.get(num).getToken()))) {
                groups += groupArray[j];
                if (numComma > 0) {
                    groups += ",";
                    numComma--;
                }

            }
        }
        //stores the new group list
        if(groups.equals("")) {
            menu.findItem(R.id.action_status).setIcon(R.drawable.statusoff);
            Session.setStatus("off");
        }
        RW.storeData(CommonValues.GROUPS, groups);
    }

    private void turnOn(LinkedList<Groups> list, int num){
        //get the current list of active groups

        String groupList = RW.readData(CommonValues.GROUPS);
        //if group list is empty store this as the first group
        if (groupList == null || groupList.equals("")) {
            menu.findItem(R.id.action_status).setIcon(R.drawable.statuson);
            Session.setStatus("on");
            //if list is empty creates the first entry
            RW.storeData(CommonValues.GROUPS, list.get(num).getToken());
        } else {
            //adds the group to the end of the list and writes it to memory
            RW.storeData(CommonValues.GROUPS, groupList + "," + list.get(num).getToken());
        }
    }

    private class HTTPRemoveLocation extends AsyncTask<String, Void, Integer> {

        private BroadcastMaps activity;

        //constructor, used to give access to the main thread
        public HTTPRemoveLocation(BroadcastMaps activty) {
            this.activity = activty;
        }

        //does on execute call
        //passes to onPostExecute when done
        @Override
        protected Integer doInBackground(String... data) {
            //build arguments for HTTP call in LinkedHashMap
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put(CommonValues.SESSION_TOKEN, Session.getToken()); //session
            params.put(CommonValues.GROUPS, data[0]);
            params.put(CommonValues.LAT, "0");
            params.put(CommonValues.LONG, "0");
            params.put(CommonValues.ACCURACY, "0");
            //make an instance of the HTTP controller
            Controller comm = new Controller();

            //run the HTTP request

            int counter = 0;
            Integer error = 0;

            do{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }error = comm.updateLocation(params);
                counter ++;
            }while(error == 0 && counter < 3);

            return error;


        }

        //starts when doInBackground finishes
        //has access to the main views
        @Override
        protected void onPostExecute(Integer error) {

        }
    }
}
