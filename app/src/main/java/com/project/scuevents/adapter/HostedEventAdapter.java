package com.project.scuevents.adapter;

import android.content.Context;
import android.content.Intent;
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


import com.project.scuevents.R;
import com.project.scuevents.model.EventClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Set;

public class HostedEventAdapter extends RecyclerView.Adapter<HostedEventAdapter.hostViewHolder>{

    ArrayList<EventClass> eventList;
    Context context;


    //initializing the eventAdapter constructor
    public HostedEventAdapter(ArrayList<EventClass> eventList, Context context) {

        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    //inflating the view on the recyclerview
    public hostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_create_modify_recyclerview,parent,false);
        return new hostViewHolder(view);
    }

    //binding each event to the view holder
    @Override
    public void onBindViewHolder(@NonNull hostViewHolder holder, int position) {
        final EventClass eventClass = eventList.get(position);
        Picasso.get().load(eventClass.getImageUrl()).into(holder.eventImg);
        holder.eventTimeDate.setText(eventClass.getEventDate());
        holder.eventName.setText(eventClass.getEventTitle());
        holder.eventVenue.setText(eventClass.getEventLocation());
        //assigning onClickListener to per event view card
        holder.eventId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Item " + eventClass.getEventTitle()+ " is clicked!",Toast.LENGTH_SHORT).show();
 //ToDo: fix datetime format
//                Log.e("DEBUG","Event ID:"+eventClass.getEventID());
//                Log.e("DEBUG","Event Title:"+eventClass.getEventTitle());
//                Log.e("DEBUG","Event Description:"+eventClass.getEventDescription());
//                Log.e("DEBUG","Host Name:"+eventClass.getHostName());
//                Log.e("DEBUG","Host ID:"+eventClass.getHostID());
//                Log.e("DEBUG","Host Token:"+eventClass.getHostToken());
//                Log.e("DEBUG","Event Date:"+eventClass.getEventDate());
//                Log.e("DEBUG","Event Time:"+eventClass.getEventTime());
//                Log.e("DEBUG","End Date:"+eventClass.getEndDate());
//                Log.e("DEBUG","End Time:"+eventClass.getEndTime());
//                Log.e("DEBUG","Event Location:"+eventClass.getEventLocation());
//                Log.e("DEBUG","Event Type:"+eventClass.getEventType());
//                Log.e("DEBUG","Department:"+eventClass.getDepartment());
//                Log.e("DEBUG","Image URL:"+eventClass.getImageUrl());
//                Log.e("DEBUG","Total seats:"+eventClass.getTotalSeats());
//                Log.e("DEBUG","Available seats:"+eventClass.getAvailableSeats());
                //Intent intent = new Intent(context, EventDetailActivity.class);
                //context.startActivity(intent);
            }
        });
    }

    @Override
    //returning the size of the eventList
    public int getItemCount() {
        return eventList.size();
    }

    //assignning the views variables required to be passed to the viewHolder class
    public class hostViewHolder extends RecyclerView.ViewHolder {

        ImageView eventImg;
        TextView eventTimeDate;
        TextView eventName;
        TextView eventVenue;
        LinearLayout eventId;
        TextView eventUpdateSign;

        public hostViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImg = itemView.findViewById(R.id.eventImg);
            eventTimeDate = itemView.findViewById(R.id.eventDate);
            eventName = itemView.findViewById(R.id.eventName);
            eventVenue = itemView.findViewById(R.id.eventVenue);
            eventId = itemView.findViewById(R.id.eventId);
            eventUpdateSign = itemView.findViewById(R.id.eventSign);
        }
    }
}
