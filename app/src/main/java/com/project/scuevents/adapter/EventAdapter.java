package com.project.scuevents.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.scuevents.model.EventModel;
import com.project.scuevents.R;

import java.util.ArrayList;
import java.util.HashSet;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.viewHolder> {
    ArrayList<EventModel> eventList;
    Context context;
    HashSet<String> oldEventId;

    //initializing the eventAdapter constructor
    public EventAdapter(ArrayList<EventModel> eventList, Context context, HashSet<String> oldEventId) {
        this.eventList = eventList;
        this.context = context;
        this.oldEventId = oldEventId;
    }

    @NonNull
    @Override
    //inflating the view on the recyclerview
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mainactivity_recyclerview,parent,false);
        return new viewHolder(view);
    }

    //binding each event to the view holder
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final EventModel eventModel = eventList.get(position);
        holder.eventImg.setImageResource(eventModel.getEventImg());
        holder.eventTimeDate.setText(eventModel.getEventDateTime());
        holder.eventName.setText(eventModel.getEventName());
        holder.eventVenue.setText(eventModel.getEventVenue());
        //checking whether the event is already present in the cache , if  yes then red dot is not displayed else displayed
        if(oldEventId!= null && oldEventId.contains(eventModel.getEventId())){
            holder.eventUpdateSign.setBackgroundColor(Color.WHITE);
        }

        //assigning onClickListener to per event view card
        holder.eventId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Item " + eventModel.getEventName()+ " is clicked!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    //returning the size of the eventList
    public int getItemCount() {
        return eventList.size();
    }

    //assignning the views variables required to be passed to the viewHolder class
    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView eventImg;
        TextView eventTimeDate;
        TextView eventName;
        TextView eventVenue;
        LinearLayout eventId;
        TextView eventUpdateSign;

        public viewHolder(@NonNull View itemView) {
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
