package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.adapter.EventAdapter;
import com.project.scuevents.adapter.NotificationAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.NotificationClass;
import com.project.scuevents.model.UserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView notificationActivityRecyclerView ;
    private static final String TAG = "SCUEvents";
    ArrayList<NotificationClass> notificationList;
    ArrayList<String> notificationBodyList;
    ArrayList<String> notificationKeys;
    ArrayList<String> eventIdList;
    ArrayList<String> eventNameList;
    DatabaseReference db;
    NotificationAdapter notificationAdapter;
    NotificationClass notificationClass;
    ProgressDialog nDialog;
    SharedPreferences prefs;
    Set<String> viewedNotifications;
    String userID;
    TextView nullNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notificationActivityRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationList = new ArrayList<>();
        notificationBodyList = new ArrayList<>();
        notificationKeys = new ArrayList<>();
        eventIdList = new ArrayList<>();
        eventNameList = new ArrayList<>();
        nullNotification = findViewById(R.id.nullNotification);

        //enabling the back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        prefs= getSharedPreferences("USER_NOTIFICATIONS", Context.MODE_PRIVATE);
        viewedNotifications= prefs.getStringSet("viewNotifiationNames", new HashSet<String>());
        Log.d(TAG ,"already viewed notifications " + viewedNotifications);

       // Log.d(TAG ,"Notification Activity is in onResume and connecting to database ");
        super.onResume();
        //getting userId from HomeFragment
        if(getIntent().hasExtra("userID"))
            userID = getIntent().getStringExtra("userID");
        //TODO can retrieve arraylist from HomeFragment , can be updated if got time
        //I require to get the instance in order to check whether notification child exists or not
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("notification");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Log.d(TAG ,"enetring if block of having notification child ");
                    //notification child is there
                    Query dbNotificationQuery = db.limitToFirst(20);
                    nDialog = new ProgressDialog(NotificationActivity.this);
                    nDialog.setMessage("Loading..");
                    nDialog.show();

                   //Log.d(TAG ,"notification query retrieved from database " + dbNotificationQuery);
                    dbNotificationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            notificationBodyList.clear();
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                               //Log.d(TAG, "notification data retrieved from database " + dataSnapshot1.getValue());
                                String notificationKey = dataSnapshot1.getKey();
                                notificationKeys.add(notificationKey);
                                //if not work remove from here, retrieving evenetIds first, working now but need to recheck
                                DatabaseReference dbEventId = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification").child(notificationKey).child("eventId");
                                dbEventId.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Log.d(TAG, "event id retrieved from database " + dataSnapshot.getValue());
                                        eventIdList.add(dataSnapshot.getValue().toString());
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(NotificationActivity.this, "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                notificationBodyList.clear();
                                //taking different database reference because of async way of retrieving data
                                DatabaseReference dbNotificationBody = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification").child(notificationKey).child("body");
                                dbNotificationBody.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                      //  Log.d(TAG, "notification body retrieved from database " + dataSnapshot.getValue());
                                        notificationBodyList.add(dataSnapshot.getValue().toString());
                                        nDialog.hide();

                                        //passing the notificationBodyList, viewedNotifications, notificationkeys and eventId list  to the adapter
                                        notificationAdapter = new NotificationAdapter(notificationBodyList, NotificationActivity.this, eventIdList,notificationKeys,viewedNotifications);
                                        notificationActivityRecyclerView.setAdapter(notificationAdapter);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationActivity.this);
                                        notificationActivityRecyclerView.setLayoutManager(linearLayoutManager);

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(NotificationActivity.this, "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //retrieving eventId , this required for event description but doubtful whether will work or not , if not then notification keys is passed need to
                                //get eventId from the databse in eventDescription page , then with the eventId need to get event details from event table
                                /*DatabaseReference dbEventId = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification").child(notificationKey).child("eventId");
                                dbEventId.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       // Log.d(TAG, "event id retrieved from database " + dataSnapshot.getValue());
                                        eventIdList.add(dataSnapshot.getValue().toString());
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(NotificationActivity.this, "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                                //Most probably not required
                                DatabaseReference dbName = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification").child(notificationKey).child("eventName");
                                dbName.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       // Log.d(TAG, "event name retrieved from database " + dataSnapshot.getValue());
                                        eventNameList.add(dataSnapshot.getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(NotificationActivity.this, "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(NotificationActivity.this,"Sorry Something went wrong ",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //end of if notification child is there
                else{
                    //if no notification child is there
                   // Log.d(TAG ,"enetring else block of not having notification child ");
                    nullNotification.setVisibility(View.VISIBLE);
                    notificationActivityRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
            //end of datasnapshot of referncing to user->userId
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotificationActivity.this,"Sorry Something went wrong ",Toast.LENGTH_SHORT).show();
            }
        });
        //end of the first db reference where checking whether notification child is there or not
    }



    @Override
    public void onPause() {
        super.onPause();
       // Log.d(TAG ,"Notification in onPause ");
    }

}
