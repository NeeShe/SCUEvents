package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.adapter.EventAdapter;
import com.project.scuevents.model.EventClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    RecyclerView mainActivityRecyclerView ;
    private static final String TAG = "SCUEvents";
    ArrayList<EventClass> eventList;
    DatabaseReference db;
    EventAdapter eventAdapter;
    ProgressDialog nDialog;
    //Set<String> viewedEventNames;
    SharedPreferences prefs;
    Set<String> viewedEventNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG ,"Activity started is in onCreate ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initializing the recycler view
        mainActivityRecyclerView = findViewById(R.id.mainActivityRecyclerView);
        viewedEventNames = new HashSet<>();
        eventList = new ArrayList<>();
       /* for(int i = 0 ; i < eventList.size() ; i++)
        Log.d(TAG ,"The event is  " + eventList.get(i));*/

    }

  /*  @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG ,"The activity is in onStart() and fetching data from shared preference  ");
        prefs= getSharedPreferences("com.projects.scuevents", Context.MODE_PRIVATE);
        viewedEventNames= prefs.getStringSet("viewEventNames", new HashSet<String>());
        Log.d(TAG ,"already viewed events " + viewedEventNames);
    }*/

    @Override
    protected void onResume() {
        Log.d(TAG ,"The activity is in onStart() and fetching data from shared preference  ");
        prefs= getSharedPreferences("com.projects.scuevents", Context.MODE_PRIVATE);
        viewedEventNames= prefs.getStringSet("viewEventNames", new HashSet<String>());
        Log.d(TAG ,"already viewed events " + viewedEventNames);

        Log.d(TAG ,"Activity is in onResume and connecting to database ");
        super.onResume();
        //connecting to the database
        db = FirebaseDatabase.getInstance().getReference().child("Events");
        nDialog = new ProgressDialog(MainActivity.this);
        nDialog.setMessage("Loading..");
        nDialog.show();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d(TAG ,"The events are  "+ dataSnapshot);
                //before clearing the list its taking the old values from the eventList
               /* for(int i = 0 ; i < eventList.size(); i++){
                    if(!viewedEventNames.contains(eventList.get(i).getEventID()))
                        viewedEventNames.add(eventList.get(i).getEventID());
                }*/
                eventList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventClass eventClass = dataSnapshot1.getValue(EventClass.class);
                    eventList.add(eventClass);
                    //setViewedEventName.add(eventClass.getEventTitle());
                }
                //eventAdapter = new EventAdapter(eventList,MainActivity.this);
                // Log.d(TAG ,"The list  "+ eventList);
                nDialog.hide();
                //Collections.sort(eventList, Collections.reverseOrder());
                Collections.reverse(eventList);
                Log.d(TAG ,"the viewedEvents passed to an adapter " + viewedEventNames);
                eventAdapter = new EventAdapter(eventList,MainActivity.this,viewedEventNames);
                mainActivityRecyclerView.setAdapter(eventAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                mainActivityRecyclerView.setLayoutManager(linearLayoutManager);

                //setting the shared preferences
               /* for(int i = 0 ; i < eventList.size(); i++){
                    if(!viewedEventNames.contains(eventList.get(i).getEventID()))
                        viewedEventNames.add(eventList.get(i).getEventID());
                }
                Log.d(TAG ,"newly added viewed events " + viewedEventNames);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet("viewEventNames", viewedEventNames);
                editor.apply();*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Sorry Something went wrong ",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG ,"Activity is in onPause ");
        Log.d(TAG ,"EventList in onPause ");
        /*for(int i = 0 ; i < eventList.size() ; i++)
            Log.d(TAG ,"The event is  " + eventList.get(i));*/
       /* for(int i = 0 ; i < eventList.size(); i++){
            if(!viewedEventNames.contains(eventList.get(i).getEventID()))
                viewedEventNames.add(eventList.get(i).getEventID());
        }*/
        //setting the shared preferences
        viewedEventNames.clear();
        for(int i = 0 ; i < eventList.size(); i++){
            //if(!viewedEventNames.contains(eventList.get(i).getEventID()))
            viewedEventNames.add(eventList.get(i).getEventID());
        }
        Log.d(TAG ,"newly added viewed events " + viewedEventNames);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet("viewEventNames", viewedEventNames);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG ,"Activity is in onStop ");
        Log.d(TAG ,"EventList in onStop ");
        /*for(int i = 0 ; i < eventList.size() ; i++)
            Log.d(TAG ,"The event is  " + eventList.get(i));*/
        //setting the shared preferences
           /*    for(int i = 0 ; i < eventList.size(); i++){
                    if(!viewedEventNames.contains(eventList.get(i).getEventID()))
                        viewedEventNames.add(eventList.get(i).getEventID());
                }
                Log.d(TAG ,"newly added viewed events " + viewedEventNames);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet("viewEventNames", viewedEventNames);
                editor.apply();*/
    }
}
