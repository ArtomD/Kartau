package activities.kartau.android.util;

import android.view.View;
import android.widget.ToggleButton;

import java.util.LinkedList;

import activities.kartau.android.staticdata.Groups;

/**
 * Created by Artom on 2015-10-01.
 */
public class CancelButtonOnClickListener implements View.OnClickListener{
    //this is a custom OnClickListener interface that has a custom constructor
    //this is so the instance of the class has acess to the necessary fields
    public LinkedList<Groups> list;
    public int i;
    public ToggleButton button;

    public CancelButtonOnClickListener(LinkedList<Groups> list, int i, ToggleButton button) {
        this.list = list;
        this.i = i;
        this.button = button;
    }

    @Override
    public void onClick(View v)
    {

    }
}
