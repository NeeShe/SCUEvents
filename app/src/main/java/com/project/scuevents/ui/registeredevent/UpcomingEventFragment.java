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
import com.project.scuevents.adapter.RegisteredEventAdapter;
import com.project.scuevents.adapter.RegisteredEventAdapter1;
import com.project.scuevents.adapter.RegisteredEventClassifiedAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.EventIDNameClass;
import com.project.scuevents.model.FireBaseUtilClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingEventFragment extends Fragment {
    RecyclerView eventDetailsRecyclerView ;
    Calendar startDateCal;
    long todayTimestamp;
    DatabaseReference db;
    DatabaseReference db1;
    ProgressDialog nDialog;
    ValueEventListener valueEventListener;
    ValueEventListener valueListener;
    //ArrayList<EventClass> eventList;
    RegisteredEventAdapter1 registeredEventAdapter;
    private static final String TAG = "Registered";

    public UpcomingEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,c.get(Calendar.YEAR));
        c.set(Calendar.MONTH,c.get(Calendar.MONTH));
        c.set(Calendar.DATE,c.get(Calendar.DATE));
        startDateCal = c;
        todayTimestamp = startDateCal.getTimeInMillis();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_upcoming_event, container, false);
        eventDetailsRecyclerView = root.findViewById(R.id.registeredEventsUpcoming);
        //eventList = new ArrayList<>();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        eventDetailsRecyclerView.removeAllViewsInLayout();
        //eventList.clear();
        if(registeredEventAdapter != null){
            registeredEventAdapter.notifyDataSetChanged();
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
                //registeredEventClassifiedList.clear();
                //eventList.clear();
                HashSet<String> eventIDList = new HashSet<>();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventIDNameClass eventIDNameClass = dataSnapshot1.getValue(EventIDNameClass.class);
                    eventIDList.add(eventIDNameClass.getEventID());
                }

                setEventAdapter(eventIDList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //this toast was throwing the
                // Attempt to invoke virtual method 'java.lang.String android.content.Context.getPackageName()' on a null object reference error
                Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        };
        db.addValueEventListener(valueEventListener);

    }
    private void setEventAdapter(final HashSet<String >eventIDList) {
        //Log.d(TAG ,"eventIDs retrieved from user table " );
        Iterator<String> i = eventIDList.iterator();
        while (i.hasNext()){
            Log.d(TAG ,"eventIDs retireved are " + i.next() );
        }

        //declaring a new arraylist solved the problem of duplicate values getting rendered
        final ArrayList<EventClass> eventList = new ArrayList<>();
        for(String eventID: eventIDList){
            db1 = FireBaseUtilClass.getDatabaseReference().child("Events").child(eventID);
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EventClass eventClass = dataSnapshot.getValue(EventClass.class);
                    if(eventClass != null) {
                        long startTimeStamp = eventClass.getStartTimestamp();
                        if (Long.signum(startTimeStamp - todayTimestamp) != -1) {
                            //Log.d(TAG ,"adding this particular upcoming event " + eventClass.getEventTitle());
                            eventList.add(eventClass);
                            registeredEventAdapter = new RegisteredEventAdapter1(eventList,getActivity());
                            eventDetailsRecyclerView.setAdapter(registeredEventAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            eventDetailsRecyclerView.setLayoutManager(linearLayoutManager);
                        } else {
                            //Log.d(TAG ,"not adding this particular event " + eventClass.getEventTitle());
                        }
                    }else {
                        Log.d(TAG ,"eventclass null");
                    }
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

    @Override
    public void onPause() {
        Log.d(TAG,"onPause");
        //eventList.clear();
        if(registeredEventAdapter!= null){registeredEventAdapter.notifyDataSetChanged();}
        if(valueEventListener!= null){
            db.removeEventListener(valueEventListener);}
        if(valueListener!=null){
            db1.removeEventListener(valueListener);}
        super.onPause();
    }
}

