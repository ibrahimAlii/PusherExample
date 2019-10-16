package ib.project.pusher;

import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static ib.project.pusher.EndPointData.getBody;
import static ib.project.pusher.EndPointData.getHeaders;


public class PushMessagingService extends FirebaseMessagingService
        implements InfoListener<String> {

    private static final String TAG = "PushMessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(TAG, remoteMessage.toString());
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        Log.i(TAG, s);
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        Log.e(TAG, "Error While Sending...", e);
        super.onSendError(s, e);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }


    /*

     */
    @Override
    public void onNewToken(@NonNull String s) {
        Log.i(TAG, s);
        sendTokenToPusher(s);
    }

    void sendTokenToPusher(String token) {
        HashMap<String, String> headers = getHeaders();
        String body = getBody(token);
        RegisterTokenAsync tokenAsync = new RegisterTokenAsync(this, headers, body);
        tokenAsync.execute(Pusher.URL);
    }


    @Override
    public void updateFromPusher(String result) {
        Log.i(TAG, "Response: " + result);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        return null;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

    }

    @Override
    public void finishRequest() {

    }
}
