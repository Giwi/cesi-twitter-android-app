package giwi.org.cesitwitter.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 07/12/15.
 */
public class TwitterDAO {
    private static String URL = "http://cesi-giwisoft.rhcloud.com";
    private HTTPHelper httpHelper = new HTTPHelper();

    public RestResponse login(String username, String password) {
        try {
           // Map<String, String> headers = new HashMap<>();
            // headers.put("Authorization", "Bearer "  + token);

            JSONObject request = new JSONObject();
            request.put("username", username);
            request.put("password", password);

            return httpHelper.send(Methods.POST,URL+"/api/auth", request, null);
        } catch (Exception e) {
            return httpHelper.handleException(e);
        }
    }

}
