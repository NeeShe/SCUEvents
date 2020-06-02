package com.project.scuevents.ui.registeredevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.R;
import com.project.scuevents.adapter.RegisteredEventClassifiedAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.EventIDNameClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.RegisteredEventClassified;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RegisteredEventsFragment extends Fragment {

    RecyclerView eventCLassifiedRecyclerView ;
    Calendar startDateCal;
    long todayTimestamp;
    DatabaseReference db;
    DatabaseReference db1;
    ProgressDialog nDialog;
    TreeMap<String,ArrayList<EventClass>> classified;
    ArrayList<EventClass> eventList;
    ArrayList<RegisteredEventClassified> registeredEventClassifiedList;
    RegisteredEventClassifiedAdapter registeredEventClassifiedAdapter;
    ValueEventListener valueEventListener;
    ValueEventListener valueListener;
    private static final String TAG = "Registered";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,c.get(Calendar.YEAR));
        c.set(Calendar.MONTH,c.get(Calendar.MONTH));
        c.set(Calendar.DATE,c.get(Calendar.DATE));
        startDateCal = c;
        todayTimestamp = startDateCal.getTimeInMillis();

        Log.d(TAG ,"eneteredRegistered ");
        View root = inflater.inflate(R.layout.fragment_registered_events, container, false);
        eventList = new ArrayList<>();
        registeredEventClassifiedList = new ArrayList<>();
        eventCLassifiedRecyclerView = root.findViewById(R.id.registeredEvents);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        registeredEventClassifiedAdapter = new RegisteredEventClassifiedAdapter(buildClassifiedEventList());
        eventCLassifiedRecyclerView.setAdapter(registeredEventClassifiedAdapter);
        eventCLassifiedRecyclerView.setLayoutManager(layoutManager);*/
        return root;
    }

    @Override
    public void onResume() {
        eventCLassifiedRecyclerView.removeAllViewsInLayout();
        super.onResume();
        registeredEventClassifiedList.clear();
        eventList.clear();
        if(registeredEventClassifiedAdapter != null){
            registeredEventClassifiedAdapter.notifyDataSetChanged();
        }

        SharedPreferences sh = getActivity().getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
        String userID = sh.getString("USER_ID", "");

        db = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("registeredEvents");
        nDialog = new ProgressDialog(getActivity());
        nDialog.setMessage("Loading..");
        nDialog.show();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                HashSet<String> eventIDList = new HashSet<>();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventIDNameClass eventIDNameClass = dataSnapshot1.getValue(EventIDNameClass.class);
                    eventIDList.add(eventIDNameClass.getEventID());
                }

                setEventAdapter(eventIDList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        };
        db.addValueEventListener(valueEventListener);

    }

    private void setEventAdapter(final HashSet<String >eventIDList) {
        Log.d(TAG ,"eventIDs retrieved from user table " );
        Iterator<String> i = eventIDList.iterator();
        while (i.hasNext()){
            Log.d(TAG ,"eventIDs are " + i.next() );
        }

        for(String eventID: eventIDList){
            db1 = FireBaseUtilClass.getDatabaseReference().child("Events").child(eventID);
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EventClass eventClass = dataSnapshot.getValue(EventClass.class);
                   // eventList.clear(); //this is clearing the whole eventlist , for which it was rendering only one event
                    eventList.add(eventClass);
                    registeredEventClassifiedAdapter = new RegisteredEventClassifiedAdapter(buildClassifiedEventList(eventList));
                    eventCLassifiedRecyclerView.setAdapter(registeredEventClassifiedAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    eventCLassifiedRecyclerView.setLayoutManager(layoutManager);
                    nDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
                }
            };
            db1.addListenerForSingleValueEvent(valueListener);
        }
    }

    private ArrayList<RegisteredEventClassified> buildClassifiedEventList(ArrayList<EventClass> eventList) {
//        Log.d(TAG ,"eventList retrieved from user table " );
//        for(EventClass eventClass:eventList){
//            Log.d(TAG ,"eventId " + eventClass.getEventID() );
//        }

        registeredEventClassifiedList = new ArrayList<>();
        classified = buildEventList(eventList);

        for(Map.Entry mapElement:classified.entrySet()){
            String key = (String)mapElement.getKey();
            ArrayList<EventClass> values= (ArrayList)mapElement.getValue();
            RegisteredEventClassified registeredEventClassified1 = new RegisteredEventClassified(key,values);
            registeredEventClassifiedList.add(registeredEventClassified1);
        }

        Log.d(TAG ,"registeredClassifiedList formed " );
        for(RegisteredEventClassified registeredEventClassified:registeredEventClassifiedList){
            Log.d(TAG ,"registered keys " + registeredEventClassified.getClassifiedTitle() );
            ArrayList<EventClass> registeredValues = registeredEventClassified.getEventClassList();
            for(EventClass eventClass:registeredValues) {
                Log.d(TAG, "registered values " +eventClass.getEventID());
            }
        }

        return registeredEventClassifiedList;
    }

    private TreeMap<String,ArrayList<EventClass>> buildEventList(ArrayList<EventClass> eventList){
        Log.d(TAG ,"eventList past to form the map " );
        for(EventClass eventClass:eventList){
            Log.d(TAG ,"eventId " + eventClass.getEventID() );
        }

        HashMap<String,ArrayList<EventClass>> tempMap = new HashMap<>();
        for(EventClass eventClass:eventList){
            long startTimeStamp = eventClass.getStartTimestamp();
            if(Long.signum(startTimeStamp-todayTimestamp ) != -1){
                if(!tempMap.containsKey("Upcoming Events")){
                    tempMap.put("Upcoming Events",new ArrayList<EventClass>());
                }
                tempMap.get("Upcoming Events").add(eventClass);
            }else{
                if(!tempMap.containsKey("Past Events")){
                    tempMap.put("Past Events",new ArrayList<EventClass>());
                }
                tempMap.get("Past Events").add(eventClass);
            }
        }
        TreeMap<String, ArrayList<EventClass>> sortedTempMap = new TreeMap<>(Collections.<String>reverseOrder());
        sortedTempMap.putAll(tempMap);
        return sortedTempMap;
    }

    @Override
    public void onPause() {
        registeredEventClassifiedList.clear();
        eventList.clear();
        if(registeredEventClassifiedAdapter!= null){registeredEventClassifiedAdapter.notifyDataSetChanged();}
        if(valueEventListener!= null){db.removeEventListener(valueEventListener);}
        if(valueListener!=null){db1.removeEventListener(valueListener);}
        super.onPause();
    }


}
