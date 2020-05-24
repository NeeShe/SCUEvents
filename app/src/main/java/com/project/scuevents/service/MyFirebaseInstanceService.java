package com.project.scuevents.service;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceService extends FirebaseMessagingService {
    String TAG = "SCUEvents";
    public static final String PREFERENCE_NAME = "MyPreferenceFileName";
    public MyFirebaseInstanceService() {

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);

        SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UserToken", token);
        editor.commit();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG,remoteMessage.getNotification().getBody());
    }
}
