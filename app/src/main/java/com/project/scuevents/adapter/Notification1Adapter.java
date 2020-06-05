package com.project.scuevents.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.EventDetailActivity;
import com.project.scuevents.R;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.NotificationClass;

import java.util.ArrayList;
import java.util.Set;

public class Notification1Adapter extends RecyclerView.Adapter<Notification1Adapter.viewHolder> {
    ArrayList<NotificationClass> notificationList;
    ArrayList<String> notificationKeysList;
    Context context;
    SharedPreferences prefs;
    String userID;

    public Notification1Adapter(ArrayList<NotificationClass> notificationList, Context context,ArrayList<String> notificationKeysList,String userID) {
        this.notificationList = notificationList;
        this.context = context;
        this.notificationKeysList = notificationKeysList;
        this.userID = userID;
    }

    @NonNull
    @Override
    public Notification1Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_notification_recyclerview,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Notification1Adapter.viewHolder holder, int position) {
        final NotificationClass notificationClass = notificationList.get(position);
        holder.notificationBody.setText(notificationClass.getBody());

        final String notificationKeyStr = notificationKeysList.get(position);
        //checking whether the notification body is already present in the cache , if  yes then white background if not red background
        if(notificationClass.isView()){
            holder.notificationId.setBackgroundColor(Color.WHITE);
            holder.notificationBody.setTextColor(Color.parseColor("#B30738"));
        }

        final String eventId = notificationClass.getEventId();
        //assigning onClickListener to per event view card
        holder.notificationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"the notification for key "+notificationKeyStr+" is "+notificationClass.getBody()+" for event "+notificationClass.getEventName()
//                        +" view set from "+notificationClass.isView()+" to true",Toast.LENGTH_SHORT).show();
                //on click setting the background color from red to white and redirecting to event details page
                //saving the viewed notifications in shared preference
                holder.notificationId.setBackgroundColor(Color.WHITE);
                holder.notificationBody.setTextColor(Color.parseColor("#B30738"));
                //setting the value of view to true
                boolean viewed = true;
                FireBaseUtilClass.getDatabaseReference().child("Users").child(userID).child("notification").child(notificationKeyStr).child("view").setValue(viewed).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(context,"view updated successfully",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"view updation unsuccessfull",Toast.LENGTH_SHORT).show();
                    }
                });
                //getting the particular event object which needs to be passed on the event detail activity
                DatabaseReference db = FireBaseUtilClass.getDatabaseReference().child("Events").child(eventId);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            EventClass eventClass = dataSnapshot.getValue(EventClass.class);
                            Intent intent = new Intent(context, EventDetailActivity.class);
                            intent.putExtra("Object", eventClass);
                            context.startActivity(intent);
                        }else{
                            Toast.makeText(context,"The Event is cancelled ",Toast.LENGTH_SHORT).show();
                        }
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
    public int getItemCount() {
        return notificationList.size();
    }

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
