
package com.project.scuevents.adapter;

import android.content.Context;
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

import com.project.scuevents.model.EventClass;
import com.project.scuevents.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.viewHolder> {
    ArrayList<EventClass> eventList;
    Context context;
    //Set<String> viewedEventNames;
    //SharedPreferences prefs;
    Set<String> viewedEventNames;

    //initializing the eventAdapter constructor
    public EventAdapter(ArrayList<EventClass> eventList, Context context, Set<String> viewedEventNames) {
        this.eventList = eventList;
        this.context = context;
        this.viewedEventNames = viewedEventNames;

        //this.prefs = prefs;

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
        final EventClass eventClass = eventList.get(position);
        Picasso.get().load(eventClass.getImageUrl()).into(holder.eventImg);
        holder.eventTimeDate.setText(eventClass.getEventDate());
        holder.eventName.setText(eventClass.getEventTitle());
        holder.eventVenue.setText(eventClass.getEventLocation());

        //checking whether the event is already present in the cache , if  yes then red dot is not displayed else displayed
       if(viewedEventNames!= null && viewedEventNames.contains(eventClass.getEventID())){
            holder.eventUpdateSign.setBackgroundColor(Color.WHITE);
        }


        //assigning onClickListener to per event view card
        holder.eventId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Item " + eventClass.getEventTitle()+ " is clicked!",Toast.LENGTH_SHORT).show();
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
