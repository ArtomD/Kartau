package activities.kartau.android.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import activities.kartau.android.staticdata.CommonValues;
import activities.kartau.android.staticdata.Session;
import activities.kartau.android.staticdata.User;
import activities.kartau.android.util.ReadWrite;


public class Support extends ActionBarActivity {

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
        this.RW = new ReadWrite(this.getApplicationContext());
        User.setRW(RW);
        Session.setRW(RW);
        setContentView(R.layout.activity_support);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_support, menu);
        try{
            LinearLayout layout = (LinearLayout)findViewById(R.id.aboutNavButtonLayout);
            LinearLayout border = (LinearLayout)findViewById(R.id.border);
            if((RW.readData(CommonValues.IS_LOGIN)).equals(CommonValues.TRUE)) {
                showOption(R.id.action_logout);
                hideOption(R.id.action_login);
                showOption(R.id.action_status);
                layout.setVisibility(View.VISIBLE);
                border.setVisibility(View.VISIBLE);
            }else{
                showOption(R.id.action_login);
                hideOption(R.id.action_logout);
                hideOption(R.id.action_status);
                layout.setVisibility(View.GONE);
                border.setVisibility(View.GONE);
            }
        } catch (java.lang.NullPointerException e){
            e.printStackTrace();
        }
        updateStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                goLogIn();
                return true;
            case R.id.action_about:
                goAbout();
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

    public void goLogIn() {
        goNextActivity(Login.class);
    }

    public void goAbout() {
        goNextActivity(About.class);
    }
    public void goLogout(){
        RW.storeData(CommonValues.IS_LOGIN, CommonValues.FALSE);
        User.clearUser(RW);
        Session.clearSession();
        goNextActivity(Login.class);
    }

    public void goBroadcast(View view) {
        goNextActivity(BroadcastMaps.class);
    }

    public void goProfile(View view) {
        goNextActivity(Profile.class);
    }

    public void goNextActivity(Class nextView){
        Intent intent = new Intent(this, nextView);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    public void goWebsiteContactUs (View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kartau.com/contact"));
        startActivity(browserIntent);
        overridePendingTransition(0, 0);
    }

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
