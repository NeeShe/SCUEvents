package com.project.scuevents.ui.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.NotificationActivity;
import com.project.scuevents.R;
import com.project.scuevents.SearchActivity;
import com.project.scuevents.adapter.EventAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.service.MyFirebaseInstanceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HomeFragment extends Fragment {

    private static final String TAG = "SCUEvents";

    RecyclerView mainActivityRecyclerView ;
    ProgressDialog nDialog;
    TextView notificationCount;




    EventAdapter eventAdapter;
    ArrayList<EventClass> eventList;
    Set<String> viewedEventNames;

    int mNotificationCountOld;
    int mNotificationCountNew;
    String userID;

    SharedPreferences prefs;
    SharedPreferences prefsUser;
    SharedPreferences prefsNotification;


    DatabaseReference db;
    DatabaseReference dbNotifications;
    ValueEventListener valueEventListener;
    ValueEventListener notifyListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView");
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //checking whether user token is getting properly saved in shared preference
        SharedPreferences pref = getActivity().getSharedPreferences(MyFirebaseInstanceService.PREFERENCE_NAME, Activity.MODE_PRIVATE);
        String s = pref.getString("UserToken", "null");
        Log.d(TAG, "user fresh token upon Installation/reinstallation "+ s);

        //initializing the recycler view
        mainActivityRecyclerView = root.findViewById(R.id.HomeRecyclerView);
        viewedEventNames = new HashSet<>();
        eventList = new ArrayList<>();
        return root;
    }

    @Override
    public void onResume() {

        prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
        //prefsNotification.edit().commit();
        mNotificationCountNew = 0;
        mNotificationCountOld = prefsNotification.getInt("notificationCount", 0);
        prefsNotification.edit().clear().commit();

        Log.d(TAG ,"notification old after onResume "+mNotificationCountOld+" notification new after onResume"+mNotificationCountNew);


        //retrieving the previous displayed events on recycler view
        prefs= getActivity().getSharedPreferences("com.projects.scuevents", Context.MODE_PRIVATE);
        viewedEventNames= prefs.getStringSet("viewEventNames", new HashSet<String>());

        super.onResume();

        //connecting to notification database to get the notification counts to display in notification badge
        prefsUser = getActivity().getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
        userID= prefsUser.getString("USER_ID", "null");
        Log.d(TAG ,"logged userID in home fragment " + userID);

        //getting the no of notifications at present
        dbNotifications = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification");
        notifyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        //Log.d(TAG, "entering into notification exixting part ")
                        mNotificationCountNew++;
                    }
                }else{
                    Log.d(TAG, "entering into non notification existing part ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
            }
        };
        dbNotifications.addValueEventListener(notifyListener);

        //connecting to the database for events
        db = FireBaseUtilClass.getDatabaseReference().child("Events");
        nDialog = new ProgressDialog(getActivity());
        nDialog.setMessage("Loading..");
        nDialog.show();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    //Log.d(TAG ,"event data retrieved from database " + dataSnapshot1.getValue());
                    EventClass eventClass = dataSnapshot1.getValue(EventClass.class);
                    eventList.add(eventClass);
                }
                nDialog.dismiss();
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
        };
        db.addValueEventListener(valueEventListener);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigation, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_notify);
        View actionView = menuItem.getActionView();
        notificationCount = actionView.findViewById(R.id.notification_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
    }

    private void setupBadge() {
        //Log.d(TAG ,"notification old "+mNotificationCountOld+" notification new "+mNotificationCountNew);
       if(mNotificationCountNew == mNotificationCountOld || mNotificationCountOld>mNotificationCountNew){
           if (notificationCount.getVisibility() != View.INVISIBLE) {
               notificationCount.setVisibility(View.INVISIBLE);
           }else{
               notificationCount.setVisibility(View.INVISIBLE);
           }
       }else{
           if (notificationCount.getVisibility() != View.VISIBLE) {
               notificationCount.setText(String.valueOf(mNotificationCountNew-mNotificationCountOld));
               notificationCount.setVisibility(View.VISIBLE);
           }else{
               notificationCount.setText(String.valueOf(mNotificationCountNew-mNotificationCountOld));
           }
       }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.action_notify:
                notificationCount.setVisibility(View.INVISIBLE);
                prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorNotification = prefsNotification.edit().clear();
                //editorNotification.clear();
                editorNotification.putInt("notificationCount", mNotificationCountNew);
                editorNotification.apply();
                //notificationButtonClicked = true;
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG,"onPause");
        if(valueEventListener!= null){db.removeEventListener(valueEventListener);}
        if(notifyListener!=null){dbNotifications.removeEventListener(notifyListener);}
        super.onPause();

        //setting the shared preferences
        viewedEventNames.clear();
        for(int i = 0 ; i < eventList.size(); i++){
            viewedEventNames.add(eventList.get(i).getEventID());
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet("viewEventNames", viewedEventNames);
        editor.apply();
    }
}
