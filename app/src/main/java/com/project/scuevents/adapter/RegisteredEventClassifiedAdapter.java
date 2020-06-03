package com.project.scuevents.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.scuevents.R;
import com.project.scuevents.model.RegisteredEventClassified;

import java.util.ArrayList;

public class RegisteredEventClassifiedAdapter extends RecyclerView.Adapter<RegisteredEventClassifiedAdapter.viewHolder>{
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<RegisteredEventClassified> registeredEventClassifiedList;
    private Context context;

    public RegisteredEventClassifiedAdapter(ArrayList<RegisteredEventClassified> registeredEventClassifiedList,Context context){
        this.registeredEventClassifiedList = registeredEventClassifiedList;
        this.context = context;
    }

    @NonNull
    @Override
    public RegisteredEventClassifiedAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_registeredeventclassified_recyclerview, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegisteredEventClassifiedAdapter.viewHolder holder, int position) {
        RegisteredEventClassified registeredEventClassified = registeredEventClassifiedList.get(position);
        holder.eventClassifiedTitle.setText(registeredEventClassified.getClassifiedTitle());

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.eventRegisteredRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(registeredEventClassified.getEventClassList().size());
        // Create sub item view adapter
        RegisteredEventAdapter registeredEventAdapter = new RegisteredEventAdapter(registeredEventClassified.getEventClassList(),context);
        holder.eventRegisteredRecyclerView.setLayoutManager(layoutManager);
        holder.eventRegisteredRecyclerView.setAdapter(registeredEventAdapter);
        holder.eventRegisteredRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return registeredEventClassifiedList.size() ;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
         TextView eventClassifiedTitle;
         RecyclerView eventRegisteredRecyclerView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            eventClassifiedTitle = itemView.findViewById(R.id.registered_event_title);
            eventRegisteredRecyclerView = itemView.findViewById(R.id.eventregistered_sub_item);
        }

    }
}
