package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.adapter.EventAdapter;
import com.project.scuevents.adapter.Notification1Adapter;
import com.project.scuevents.adapter.NotificationAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.NotificationClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Notification1Activity extends AppCompatActivity {
    RecyclerView notificationActivityRecyclerView ;
    ArrayList<NotificationClass> notificationClassList;
    DatabaseReference db;
    Notification1Adapter notification1Adapter;
    ValueEventListener valueEventListener;
    String userID;
    ArrayList<String> notificationKeysList;
    TextView nullNotification;
    private static final String TAG = "NotificationDebug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification1);
        notificationClassList = new ArrayList<>();
        notificationActivityRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationKeysList = new ArrayList<>();
        nullNotification = findViewById(R.id.nullNotification);

        //enabling the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationActivityRecyclerView.removeAllViewsInLayout();
        notificationClassList.clear();
        notificationKeysList.clear();
        if(notification1Adapter != null){
            notification1Adapter.notifyDataSetChanged();
        }
        if(getIntent().hasExtra("userID"))
            userID = getIntent().getStringExtra("userID");
        db = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    notificationClassList.clear();
                    notificationKeysList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String notificationKey = dataSnapshot1.getKey();
                        notificationKeysList.add(notificationKey);
                        NotificationClass notificationClass = dataSnapshot1.getValue(NotificationClass.class);
                        notificationClassList.add(notificationClass);
                    }
                    Collections.reverse(notificationClassList);
                    Collections.reverse(notificationKeysList);
                    for (int i = 0; i < notificationClassList.size(); i++) {
                        Log.d(TAG, "notification key " + notificationKeysList.get(i));
                        Log.d(TAG, "notification body " + notificationClassList.get(i).getBody());
                        Log.d(TAG, "notification viewed? " + notificationClassList.get(i).isView());
                        Log.d(TAG, "eventName is " + notificationClassList.get(i).getEventName());
                        Log.d(TAG, "eventID is " + notificationClassList.get(i).getEventId());
                    }
                    notification1Adapter = new Notification1Adapter(notificationClassList, Notification1Activity.this, notificationKeysList, userID);
                    notificationActivityRecyclerView.setAdapter(notification1Adapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Notification1Activity.this);
                    notificationActivityRecyclerView.setLayoutManager(linearLayoutManager);
                }else{
                    nullNotification.setVisibility(View.VISIBLE);
                    notificationActivityRecyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Notification1Activity.this, "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
            }
        };
        db.addValueEventListener(valueEventListener);
    }

    @Override
    public void onPause() {
        notificationClassList.clear();
        notificationKeysList.clear();
        if(notification1Adapter!= null){
            notification1Adapter.notifyDataSetChanged();
        }
        if(valueEventListener!= null){db.removeEventListener(valueEventListener);}
        super.onPause();
    }
}
