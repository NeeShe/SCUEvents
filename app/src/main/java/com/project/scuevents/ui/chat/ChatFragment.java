package com.project.scuevents.ui.chat;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.R;
import com.project.scuevents.adapter.ChatGroupAdapter;

import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.EventIDNameClass;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    private static final String DEBUG_TAG = "ChatFragment";
    RecyclerView chatGroupRecyclerView ;
    ArrayList<EventIDNameClass> groupList;
    ChatGroupAdapter groupAdapter;
    DatabaseReference db;
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        chatGroupRecyclerView = root.findViewById(R.id.ChatRecyclerView);
        groupList = new ArrayList<>();
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sh = getActivity().getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
        userID = sh.getString("USER_ID", "");
        //first add hosted events group
        db = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("hostedEvents");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventIDNameClass eventIDNameClass = dataSnapshot1.getValue(EventIDNameClass.class);
                    groupList.add(eventIDNameClass);
                }
                addRegEventsGroups();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void addRegEventsGroups(){
        DatabaseReference db1 = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("registeredEvents");
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventIDNameClass eventIDNameClass = dataSnapshot1.getValue(EventIDNameClass.class);
                    groupList.add(eventIDNameClass);
                }
                groupAdapter = new ChatGroupAdapter(getActivity(),groupList);
                chatGroupRecyclerView.setAdapter(groupAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                chatGroupRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}


