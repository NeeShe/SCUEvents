package com.project.scuevents.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.EventDetailActivity;
import com.project.scuevents.NotificationActivity;
import com.project.scuevents.R;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder>{
    ArrayList<String> notificationBodyList;
    ArrayList<String> eventIds;
    ArrayList<String> notificationKeyList;
    Set<String> viewedNotifications;
    SharedPreferences prefs;
  // ArrayList<NotificationClass> notificationClassList;
    Context context;
    private static final String TAG = "NotificationModule";



    //initializing the eventAdapter constructor
    public NotificationAdapter(ArrayList<String> notificationBodyList, Context context,ArrayList<String> eventIds,ArrayList<String> notificationKeyList, Set<String> viewedNotifications) {
        this.notificationBodyList = notificationBodyList;
        this.eventIds = eventIds;
        this.context = context;
        this.notificationKeyList = notificationKeyList;
        this.viewedNotifications = viewedNotifications;

    }

  /*public NotificationAdapter(ArrayList<NotificationClass> notificationClassList,Context context){
      this.notificationClassList = notificationClassList;
      this.context = context;
  }*/

    @NonNull
    @Override
    //inflating the view on the recyclerview
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_notification_recyclerview,parent,false);

        return new viewHolder(view);
    }

    //binding each event to the view holder
    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.viewHolder holder, int position) {
      /*final String notificationBodyItem = notificationClassList.get(position).getNotificationBody();
      holder.notificationBody.setText(notificationBodyItem);*/
        final String notificationKeyStr = notificationKeyList.get(position);
        final String notificationBodyStr = notificationBodyList.get(position);
        final String eventId = eventIds.get(position);
        holder.notificationBody.setText(notificationBodyStr);

        //checking whether the notification body is already present in the cache , if  yes then white background if not red background

        if(viewedNotifications!= null && viewedNotifications.contains(notificationKeyStr)){
            holder.notificationId.setBackgroundColor(Color.WHITE);
            holder.notificationBody.setTextColor(Color.parseColor("#B30738"));
        }

        //assigning onClickListener to per event view card
        holder.notificationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on click setting the background color from red to white and redirecting to event details page
                //saving the viewed notifications in shared preference
               Toast.makeText(context,"eventId" +eventId + "is clicked!",Toast.LENGTH_SHORT).show();
               holder.notificationId.setBackgroundColor(Color.WHITE);
               holder.notificationBody.setTextColor(Color.parseColor("#B30738"));
               viewedNotifications.add(notificationKeyStr);
               prefs = context.getSharedPreferences("USER_NOTIFICATIONS", Context.MODE_PRIVATE);
               SharedPreferences.Editor editor = prefs.edit();
               editor.clear();
               editor.putStringSet("viewNotifiationNames", viewedNotifications);
               editor.apply();
               //getting the particular event object which needs to be passed on the event detail activity
                DatabaseReference db = FireBaseUtilClass.getDatabaseReference().child("Events").child(eventId);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        EventClass eventClass = dataSnapshot.getValue(EventClass.class);
                        Log.d(TAG ,"event name " + eventClass.getEventTitle());
                        Log.d(TAG ,"event id " + eventClass.getEventID());
                        Intent intent = new Intent(context, EventDetailActivity.class);
                        intent.putExtra("Object", eventClass);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context,"Sorry Something went wrong ",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    //returning the size of the eventList
    public int getItemCount() {
        return notificationBodyList.size();
        //return notificationClassList.size();
    }

    //assignning the views variables required to be passed to the viewHolder class
    public class viewHolder extends RecyclerView.ViewHolder {
        TextView notificationBody;
        LinearLayout notificationId;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            notificationBody =itemView.findViewById(R.id.notificationBody);
            notificationId = itemView.findViewById(R.id.notificationId);
        }
    }

}
