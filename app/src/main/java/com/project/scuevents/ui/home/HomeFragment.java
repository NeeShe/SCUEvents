package com.project.scuevents.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.R;
import com.project.scuevents.adapter.EventAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HomeFragment extends Fragment {

    RecyclerView mainActivityRecyclerView ;
    private static final String TAG = "SCUEvents";
    ArrayList<EventClass> eventList;
    DatabaseReference db;
    EventAdapter eventAdapter;
    ProgressDialog nDialog;
    SharedPreferences prefs;
    Set<String> viewedEventNames;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //initializing the recycler view
        mainActivityRecyclerView = root.findViewById(R.id.HomeRecyclerView);
        viewedEventNames = new HashSet<>();
        eventList = new ArrayList<>();
        return root;
    }

    @Override
    public void onResume() {
        prefs= getActivity().getSharedPreferences("com.projects.scuevents", Context.MODE_PRIVATE);
        viewedEventNames= prefs.getStringSet("viewEventNames", new HashSet<String>());
        Log.d(TAG ,"already viewed events " + viewedEventNames);

        Log.d(TAG ,"Fragment is in onResume and connecting to database ");
        super.onResume();
        //connecting to the database
        db = FireBaseUtilClass.getDatabaseReference().child("Events");
        nDialog = new ProgressDialog(getActivity());
        nDialog.setMessage("Loading..");
        nDialog.show();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventClass eventClass = dataSnapshot1.getValue(EventClass.class);
                    eventList.add(eventClass);
                }
                nDialog.hide();
                Collections.reverse(eventList);
                Log.d(TAG ,"the viewedEvents passed to an adapter " + viewedEventNames);
                eventAdapter = new EventAdapter(eventList,getActivity(),viewedEventNames);
                mainActivityRecyclerView.setAdapter(eventAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                mainActivityRecyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Sorry Something went wrong ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG ,"EventList in onPause ");
        //setting the shared preferences
        viewedEventNames.clear();
        for(int i = 0 ; i < eventList.size(); i++){
            viewedEventNames.add(eventList.get(i).getEventID());
        }
        Log.d(TAG ,"newly added viewed events " + viewedEventNames);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet("viewEventNames", viewedEventNames);
        editor.apply();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(getActivity(), "Search Code", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_notify:
                Toast.makeText(getActivity(), "Notification Code", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
