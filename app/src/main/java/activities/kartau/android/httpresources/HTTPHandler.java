package activities.kartau.android.httpresources;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Artom on 2015-09-28.
 */
public class HTTPHandler {

    //makes a HTTP request out of a request object
    //takes a request object
    //returns a response object
    public static Response makeGetRequest(Request HTTPRequest) {
        //uses the request object ot make the target URL
        Log.i("GET request", HTTPRequest.buildAddressString());
        URL url;
        String result ="";
        try {
            url = new URL(HTTPRequest.buildAddressString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                String line = null;
                while ((line = r.readLine()) != null) {
                    total.append(line + "\n");
                }
                //records the result
                result = total.toString();
                Log.i("GET response",result);
            }
            finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //makes a new response object
        Response HTTPresponce = new Response();
        //record the string inside the response object and returns it
        HTTPresponce.setJson(result);
        return HTTPresponce;
    }


}
