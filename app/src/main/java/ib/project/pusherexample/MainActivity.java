package ib.project.pusherexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ib.project.pusher.Pusher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Pusher().init(this, "167560535142" /*Sender ID in Firebase console*/);

    }
}
