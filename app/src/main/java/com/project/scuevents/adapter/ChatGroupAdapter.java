package com.project.scuevents.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.scuevents.R;
import com.project.scuevents.model.GroupChatClass;
import com.project.scuevents.ui.chat.ChatActivity;

import java.util.ArrayList;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.viewHolder> {
    Context context;
    ArrayList<GroupChatClass> groupList;

    public ChatGroupAdapter(Context context, ArrayList<GroupChatClass> groupList) {
        this.context = context;
        this.groupList = groupList;
    }



    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_chatgroup_recyclerview,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final GroupChatClass groupChatClass = groupList.get(position);
        holder.eventName.setText(groupChatClass.getEventTitle());
        //assigning onClickListener to per event view card
        holder.eventID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("Object",groupChatClass);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        LinearLayout eventID;
        TextView eventName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            eventID = itemView.findViewById(R.id.eventIdRow);
            eventName = itemView.findViewById(R.id.eventTitletv);
        }
    }
}
