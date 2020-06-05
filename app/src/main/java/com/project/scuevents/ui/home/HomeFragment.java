package com.project.scuevents.ui.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.project.scuevents.Notification1Activity;
import com.project.scuevents.NotificationActivity;
import com.project.scuevents.R;
import com.project.scuevents.SearchActivity;
import com.project.scuevents.adapter.EventAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.NotificationClass;
import com.project.scuevents.service.MyFirebaseInstanceService;

import java.util.ArrayList;
import java.util.Calendar;
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
    SharedPreferences prefsUser;
    SharedPreferences prefsNotification;
    SharedPreferences prefs;
    DatabaseReference db;
    DatabaseReference dbNotifications;
    ValueEventListener valueEventListener;
    ValueEventListener notifyListener;
    SharedPreferences prefsNotificationClicked;
    String userID;
    Calendar startDateCal;
    int notificationNewCount = 0;
    int displayCount = 0;
    long todayTimestamp;
    boolean isClicked;
    private Handler mHandler;
    ArrayList<NotificationClass> notificationClassArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mHandler = new Handler();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,c.get(Calendar.YEAR));
        c.set(Calendar.MONTH,c.get(Calendar.MONTH));
        c.set(Calendar.DATE,c.get(Calendar.DATE));
        c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE));
        startDateCal = c;
        todayTimestamp = startDateCal.getTimeInMillis();

        //checking whether user token is getting properly saved in shared preference
        SharedPreferences pref = getActivity().getSharedPreferences(MyFirebaseInstanceService.PREFERENCE_NAME, Activity.MODE_PRIVATE);
        String s = pref.getString("UserToken", "null");
        Log.d(TAG, "user fresh token upon Installation/reinstallation "+ s);

        //initializing the recycler view
        mainActivityRecyclerView = root.findViewById(R.id.HomeRecyclerView);
        viewedEventNames = new HashSet<>();
        eventList = new ArrayList<>();
        notificationClassArrayList = new ArrayList<>();
        return root;
    }

    @Override
    public void onResume() {
        //addign the remove listner was the last change made to make runnable work properly
        if(eventAdapter!= null){eventAdapter.notifyDataSetChanged();}
        if(valueEventListener!= null){
            Log.e(TAG,"entering into  onresume removelistner");
            db.removeEventListener(valueEventListener);}
        if(notifyListener!=null){
            Log.e(TAG,"entering into onresume  removelistner1");
            dbNotifications.removeEventListener(notifyListener);}

       // Log.d("Notificationbutton" ,"after retrieving onViewStateRestored values "+mNotificationCountOld+" "+mNotificationCountNew+" "+notificationButtonClicked);
        prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
        mNotificationCountOld = prefsNotification.getInt("notificationCount", 0);
        prefsNotification.edit().clear().commit();
        Log.d(TAG, "old notification count "+mNotificationCountOld);
        /*if(mNotificationCountOld > 0)
            notificationNewCount = mNotificationCountOld;*/

        prefsNotificationClicked = getActivity().getSharedPreferences("NOTIFICATION_BOOLEAN", Context.MODE_PRIVATE);
        isClicked = prefsNotificationClicked.getBoolean("notificationClicked",false);
        Log.d(TAG, "value of isClicked "+isClicked);

        //retrieving the previous displayed events on recycler view
        prefs= getActivity().getSharedPreferences("com.projects.scuevents", Context.MODE_PRIVATE);
        viewedEventNames= prefs.getStringSet("viewEventNames", new HashSet<String>());

        super.onResume();

        //connecting to notification database to get the notification counts to display in notification badge
        prefsUser = getActivity().getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
        userID= prefsUser.getString("USER_ID", "null");
       // Log.d(TAG ,"logged userID in home fragment " + userID);

        //getting the no of notifications at present
        dbNotifications = FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification");
        notifyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(notificationNewCount>0)
                    notificationNewCount = 0;
                notificationClassArrayList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        //Log.d(TAG, "entering into notification exixting part ")
                        //mNotificationCountNew++;
                        NotificationClass notificationClass = dataSnapshot1.getValue(NotificationClass.class);
                        notificationClassArrayList.add(notificationClass);
                    }
                }else{
                    Log.d(TAG, "entering into non notification exixting part ");
                }
                for(NotificationClass notificationClass:notificationClassArrayList){
                    if(!notificationClass.isView()){
                        Log.d(TAG, "not viewed notification "+notificationClass.getBody());
                        notificationNewCount++;
                    }else{
                        Log.d(TAG, "notification viewed "+notificationClass.getBody());
                    }
                }
                if(notificationNewCount>mNotificationCountOld){
                   // notificationNewCount = notificationNewCount-mNotificationCountOld;
                    Log.d(TAG, "entered when notification new > old ");
                    isClicked = false;
                    displayCount = notificationNewCount;
                    prefsNotificationClicked = getActivity().getSharedPreferences("NOTIFICATION_BOOLEAN", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorNotification = prefsNotificationClicked.edit().clear();
                    editorNotification.putBoolean("notificationClicked", false);
                    editorNotification.apply();
                }else{
                    displayCount = notificationNewCount;
                }
                Log.d(TAG, "new notification count "+notificationNewCount);
                Log.d(TAG, "latest isClicked "+isClicked);
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
                    //eventList.add(eventClass);
                    long startTimeStamp = eventClass.getStartTimestamp();
                    if(Long.signum(startTimeStamp-todayTimestamp ) != -1) {
                        //Log.d(TAG ,"adding this particular upcoming event " + eventClass.getEventTitle());
                        eventList.add(eventClass);
                    }else{
                        //Log.d(TAG ,"not adding this particular event " + eventClass.getEventTitle());
                    }
                }
                nDialog.dismiss();
                Collections.reverse(eventList);
                //Log.d(TAG ,"the viewedEvents passed to an adapter " + viewedEventNames);
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

  /*  private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
           setupBadge();
        }
    };*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigation, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_notify);
        View actionView = menuItem.getActionView();
        notificationCount = actionView.findViewById(R.id.notification_badge);
        notificationCount.setVisibility(View.INVISIBLE);
       // mHandler.postDelayed(mUpdateTimeTask, 1000);
        //last update made to run the setup badge after frequent interval , so that if once it doesn't work second time it will work and also to give realtime
        //notification count
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "delaying the setup badge");
                mHandler.postDelayed(this,1000);
                setupBadge();
            }
        };
        mHandler.postDelayed(r,1000);
       // setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
    }

    private void setupBadge() {
        if (notificationCount != null) {
            Log.d(TAG, "entering the setBadge");
            if (isClicked || displayCount == 0) {
                Log.d(TAG, "entering the setBadge to make invisible ");
                if (notificationCount.getVisibility() != View.INVISIBLE) {
                    notificationCount.setVisibility(View.INVISIBLE);
                }
            } else {
                Log.d(TAG, "entering the setBadge to make visible ");
                notificationCount.setText(String.valueOf(displayCount));
                if (notificationCount.getVisibility() != View.VISIBLE) {
                    notificationCount.setVisibility(View.VISIBLE);
                }
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
                prefsNotificationClicked = getActivity().getSharedPreferences("NOTIFICATION_BOOLEAN", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorNotification = prefsNotificationClicked.edit().clear();
                editorNotification.putBoolean("notificationClicked", true);
                editorNotification.apply();

                //passing to the notification activity
                Intent intent = new Intent(getActivity(), Notification1Activity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
         nDialog.dismiss();
         prefsNotification = getActivity().getSharedPreferences("NOTIFICATION_COUNT", Context.MODE_PRIVATE);
         SharedPreferences.Editor editorNotification = prefsNotification.edit().clear();
         editorNotification.putInt("notificationCount", notificationNewCount);
         editorNotification.apply();

        //setting the shared preferences
        viewedEventNames.clear();
        for(int i = 0 ; i < eventList.size(); i++){
            viewedEventNames.add(eventList.get(i).getEventID());
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putStringSet("viewEventNames", viewedEventNames);
        editor.apply();


            eventList.clear();
            if(eventAdapter!= null){eventAdapter.notifyDataSetChanged();}
            if(valueEventListener!= null){
                Log.e(TAG,"entering into removelistner");
                db.removeEventListener(valueEventListener);}
            if(notifyListener!=null){
                Log.e(TAG,"entering into removelistner1");
                dbNotifications.removeEventListener(notifyListener);}

        mHandler.removeCallbacksAndMessages(null);
    }
}
