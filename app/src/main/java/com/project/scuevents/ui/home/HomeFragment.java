package com.project.scuevents.ui.home;

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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.NotificationActivity;
import com.project.scuevents.NotificationTrial;
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
    DatabaseReference dbNotifications;
    EventAdapter eventAdapter;
    ProgressDialog nDialog;
    SharedPreferences prefs;
    Set<String> viewedEventNames;
    TextView notificationCount;
    int mNotificationCountOld;
    int mNotificationCountNew;
    ArrayList<String> notificationKeyList;
    boolean notificationButtonClicked;
    SharedPreferences prefsUser;
    SharedPreferences prefsNotification;
    String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        //initializing the recycler view
        mainActivityRecyclerView = root.findViewById(R.id.HomeRecyclerView);
        viewedEventNames = new HashSet<>();
        eventList = new ArrayList<>();
        return root;
    }
   /* @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("Notificationbutton" ,"notification button onViewStateRestored ");
        // This bundle has also been passed to onCreate.
        if(savedInstanceState != null) {
            Log.d("Notificationbutton" ,"enetering when savedInstance not null ");
            notificationButtonClicked = savedInstanceState.getBoolean("notificationBellCounter");
            mNotificationCountOld = savedInstanceState.getInt("notificationBellCount");
        }
        else {
            notificationButtonClicked = false;
            mNotificationCountOld =0;
            mNotificationCountNew = 0;
        }
    }*/

    @Override
    public void onResume() {
       // Log.d("Notificationbutton" ,"after retrieving onViewStateRestored values "+mNotificationCountOld+" "+mNotificationCountNew+" "+notificationButtonClicked);
        prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
        //prefsNotification.edit().commit();
        mNotificationCountNew = 0;
        mNotificationCountOld = prefsNotification.getInt("notificationCount", 0);
        prefsNotification.edit().clear().commit();

        Log.d(TAG ,"notification old after onResume "+mNotificationCountOld+" notification new after onResume"+mNotificationCountNew);


        //retrieving the previous displayed events on recycler view
        prefs= getActivity().getSharedPreferences("com.projects.scuevents", Context.MODE_PRIVATE);
        viewedEventNames= prefs.getStringSet("viewEventNames", new HashSet<String>());
        //Log.d(TAG ,"already viewed events " + viewedEventNames);
        //Log.d(TAG ,"Fragment is in onResume and connecting to database ");
        super.onResume();

        //connecting to notification database to get the notification counts to display in notification badge
        prefsUser = getActivity().getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
        userID= prefsUser.getString("USER_ID", "null");
        Log.d(TAG ,"logged userID in home fragment " + userID);

        //getting the no of notifications at present
        dbNotifications = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification");
        dbNotifications.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        //Log.d(TAG, "entering into notification exixting part ")
                        mNotificationCountNew++;
                    }
                }else{
                    Log.d(TAG, "entering into non notification exixting part ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
            }
        });

        //connecting to the database for events
        db = FireBaseUtilClass.getDatabaseReference().child("Events");
        nDialog = new ProgressDialog(getActivity());
        nDialog.setMessage("Loading..");
        nDialog.show();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    //Log.d(TAG ,"event data retrieved from database " + dataSnapshot1.getValue());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigation, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_notify);
        View actionView = menuItem.getActionView();
        notificationCount = (TextView) actionView.findViewById(R.id.notification_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
    }

    private void setupBadge() {
        //uncomment this if last attemp didn't work
       /* if(notificationCount != null){
            if(mNotificationCountOld < mNotificationCountNew ||
                    !notificationButtonClicked && mNotificationCountOld < mNotificationCountNew ){
                    notificationCount.setText(String.valueOf(Math.min(mNotificationCountNew, 20)));
                    if (notificationCount.getVisibility() != View.VISIBLE) {
                        notificationCount.setVisibility(View.VISIBLE);
                    }
            }else if(mNotificationCountNew == mNotificationCountOld||mNotificationCountNew == 0||notificationButtonClicked) {
                if (notificationCount.getVisibility() != View.INVISIBLE) {
                    notificationCount.setVisibility(View.INVISIBLE);
                }
                notificationButtonClicked = false;
            }
        }*/
        Log.d(TAG ,"notification old "+mNotificationCountOld+" notification new "+mNotificationCountNew);
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

        /*if (notificationCount != null) {
           // Log.d("Notificationbutton" ,"notificationCountOld "+ mNotificationCountOld + "notificationCountNew "+mNotificationCountNew );
            if (notificationButtonClicked && mNotificationCountNew == 0) {
                if (notificationCount.getVisibility() != View.INVISIBLE) {
                    notificationCount.setVisibility(View.INVISIBLE);
                }
            } else {
                notificationCount.setText(String.valueOf(Math.min(mNotificationCountNew, 20)));
                if (notificationCount.getVisibility() != View.VISIBLE) {
                    notificationCount.setVisibility(View.VISIBLE);
                }
            }
        }
        // mNotificationCount = 0;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(getActivity(), "Search Code", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_notify:
                notificationCount.setVisibility(View.INVISIBLE);
                prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorNotification = prefsNotification.edit().clear();
                //editorNotification.clear();
                editorNotification.putInt("notificationCount", mNotificationCountNew);
                editorNotification.apply();
                //notificationButtonClicked = true;
                Toast.makeText(getActivity(), "Going to notification activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //saving the UI state for notification badge update
   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        Log.d("Notificationbutton" ,"mNotificationCountNew in onSavedInstance "+notificationButtonClicked);
        savedInstanceState.putBoolean("notificationBellCounter", notificationButtonClicked);
        savedInstanceState.putInt("notificationBellCount",mNotificationCountNew);
    }*/


    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG ,"EventList in onPause ");
        //setting the shared preferences
        viewedEventNames.clear();
        for(int i = 0 ; i < eventList.size(); i++){
            viewedEventNames.add(eventList.get(i).getEventID());
        }
        //Log.d(TAG ,"newly added viewed events " + viewedEventNames);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet("viewEventNames", viewedEventNames);
        editor.apply();


        Log.d("Notificationbutton" ,"notification button onPause ");

       /* prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorNotification = prefsNotification.edit().clear();
        //editorNotification.clear();
        editorNotification.putInt("notificationCount", mNotificationCountNew);
        editorNotification.apply();*/
    }

   /* @Override
    public void onStop() {
        if(FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }
        super.onStop();
        prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorNotification = prefsNotification.edit().clear();
        //editorNotification.clear();
        editorNotification.putInt("notificationCount", mNotificationCountNew);
        editorNotification.apply();
    }*/


    //original code
   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        inflater.inflate(R.menu.navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/
}
