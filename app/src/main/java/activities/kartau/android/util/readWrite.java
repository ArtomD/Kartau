package activities.kartau.android.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Artom on 2015-09-28.
 */
public class ReadWrite {
    Context context;

    public ReadWrite(Context context) {
        this.context = context;
    }
    //this method writes data to internal storage
    //it takes two strings, the key which is the name of the file being written and the value
    public void storeData(String key, String value){
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            System.out.println("VALUES: " + value);
            fos.write(value.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //this method reads data from internal storage
    //it takes a string, the key, which is the name of the file to read
    public String readData(String key){
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput(key);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);//.append("\n");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
