package ib.project.pusher;

import android.app.Application;
import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

import static ib.project.pusher.EndPointData.getBody;
import static ib.project.pusher.EndPointData.getHeaders;

/**
 * The main class that the client use to initialize our feature to receive notification
 * The client should pass a Context and the SenderID
 */
public class Pusher extends Application implements InfoListener<String> {
    private static final String TAG = "Pusher";
    static final String URL = "http://api.pushbots.com/2/subscriptions";


    public void init(@NonNull Context context, @NonNull String senderId) {
        FirebaseApp.initializeApp(context,
                new FirebaseOptions.Builder().setProjectId(context.getPackageName())
                        .setGcmSenderId(senderId).setApplicationId(context.getPackageName())
                        .setGaTrackingId(context.getPackageName())
                        .setApiKey(context.getPackageName()).build()).getOptions();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = Objects.requireNonNull(task.getResult()).getToken();

                        // Log and toast
                        //String msg = context.getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        sendTokenToPusher(token);
                    }

                });

    }

    void sendTokenToPusher(String token) {
        HashMap<String, String> headers = getHeaders();
        String body = getBody(token);
        RegisterTokenAsync tokenAsync = new RegisterTokenAsync(this, headers, body);
        tokenAsync.execute(URL);
    }

    @Override
    public void updateFromPusher(String result) {
        Log.i(TAG, "updateFromPusher: " + result);
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
