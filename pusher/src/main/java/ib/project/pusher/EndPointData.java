package ib.project.pusher;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * EndPointData.claas a class that hold our server headers, body, auth etc...
 *
 * Should always be a package private
 */
class EndPointData {


    static String getBody(String token) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("platform", 1);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    static HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("x-pushbots-appid", "5d258e58b7941208c73fcfb7");
        return headers;
    }
}
