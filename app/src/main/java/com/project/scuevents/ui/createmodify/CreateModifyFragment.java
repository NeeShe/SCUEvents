package com.project.scuevents.ui.createmodify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.CreateEventActivity;
import com.project.scuevents.R;
import com.project.scuevents.adapter.HostedEventAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.EventIDNameClass;
import com.project.scuevents.model.FireBaseUtilClass;

import java.util.ArrayList;
import java.util.HashSet;


public class CreateModifyFragment extends Fragment {
    RecyclerView eventRecyclerView ;
    private static final String DEBUG_TAG = "CreateModifyFragment";
    ArrayList<EventClass> eventList;
    DatabaseReference db;
    DatabaseReference db1;
    HostedEventAdapter eventAdapter;
    ProgressDialog nDialog;
    ValueEventListener valueEventListener;
    ValueEventListener valueListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_modify, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                startActivity(intent);
            }
        });
        eventRecyclerView = root.findViewById(R.id.createModifyRecyclerView);
        eventList = new ArrayList<>();
        return root;
    }


    @Override
    public void onResume() {
        eventRecyclerView.removeAllViewsInLayout();
        super.onResume();
        eventList.clear();
        if(eventAdapter != null){
            eventAdapter.notifyDataSetChanged();
        }

        SharedPreferences sh = getActivity().getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
        String userID = sh.getString("USER_ID", "");
        //connecting to the database
        db = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("hostedEvents");
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
                nDialog.dismiss();
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
        for(String eventID: eventIDList){
            db1 = FireBaseUtilClass.getDatabaseReference().child("Events").child(eventID);
            valueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EventClass eventClass = dataSnapshot.getValue(EventClass.class);
                    eventList.add(eventClass);
                    nDialog.dismiss();
                    eventAdapter = new HostedEventAdapter(eventList,getActivity());
                    eventRecyclerView.setAdapter(eventAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    eventRecyclerView.setLayoutManager(linearLayoutManager);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(DEBUG_TAG,databaseError.toString());
                    //Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
                }
            };
            db1.addListenerForSingleValueEvent(valueListener);
        }
    }

    @Override
    public void onPause() {
        //Log.e(DEBUG_TAG,"onPause");
        eventList.clear();
        if(eventAdapter!= null){eventAdapter.notifyDataSetChanged();}
        if(valueEventListener!= null){
            db.removeEventListener(valueEventListener);}
        if(valueListener!=null){
            db1.removeEventListener(valueListener);}
        super.onPause();
    }
}
