package activities.kartau.android.util;

import android.view.View;

import java.util.LinkedList;

import activities.kartau.android.staticdata.Groups;

/**
 * Created by Artom on 2015-10-01.
 */
public class ToggleButtonOnClickListener implements View.OnClickListener{
    //this is a custom OnClickListener interface that has a custom constructor
    //this is so the instance of the class has acess to the necessary fields
    public LinkedList<Groups> list;
    public int i;

    public ToggleButtonOnClickListener(LinkedList<Groups> list, int i) {
        this.list = list;
        this.i = i;
    }

    @Override
    public void onClick(View v)
    {

    }

}
