package com.project.scuevents.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.scuevents.EventDetailActivity;
import com.project.scuevents.R;
import com.project.scuevents.model.EventClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RegisteredEventAdapter extends RecyclerView.Adapter<RegisteredEventAdapter.viewHolder> {
    ArrayList<EventClass> eventList;
    Context context;

    RegisteredEventAdapter(ArrayList<EventClass> eventList,Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_registeredevents_recyclerview, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final EventClass eventClass = eventList.get(position);
        Picasso.get().load(eventClass.getImageUrl()).into(holder.eventImg);
        holder.eventTimeDate.setText(eventClass.getEventDate());
        holder.eventName.setText(eventClass.getEventTitle());
        holder.eventVenue.setText(eventClass.getEventLocation());

        //assigning onClickListener to per event view card
        holder.eventId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Item " + eventClass.getEventTitle()+ " is clicked!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("Object", eventClass);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView eventImg;
        TextView eventTimeDate;
        TextView eventName;
        TextView eventVenue;
        LinearLayout eventId;
        //TextView eventUpdateSign;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            eventImg = itemView.findViewById(R.id.eventImg);
            eventTimeDate = itemView.findViewById(R.id.eventDate);
            eventName = itemView.findViewById(R.id.eventName);
            eventVenue = itemView.findViewById(R.id.eventVenue);
            eventId = itemView.findViewById(R.id.eventIdRegistered);
           // eventUpdateSign = itemView.findViewById(R.id.eventSign);
        }
    }
}
