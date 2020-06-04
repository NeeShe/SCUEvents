package com.project.scuevents.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.scuevents.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RegUsersAdapter extends RecyclerView.Adapter<RegUsersAdapter.ViewHolder> {
    final String TAG="From Adapter";
    private ArrayList<String> regusers;
    private LayoutInflater mInflater;

    public RegUsersAdapter(Context context, ArrayList data) {
        Log.d(TAG,"in Constructor");
        this.mInflater = LayoutInflater.from(context);
        this.regusers = data;
    }


    @NonNull
    @Override
    public RegUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG,"in onCreate View Holder");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.regusersrecyclerview, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RegUsersAdapter.ViewHolder holder, int position) {
//        Log.d(TAG,"in onBind View Holder");
        String username = (String) regusers.get(position);
        holder.myTextView.setText(username);
        Log.d(TAG,username);
    }

    @Override
    public int getItemCount() {
        return regusers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
//            Log.d(TAG,"in onCreate View Holder");
            myTextView = itemView.findViewById(R.id.username);

        }
    }
}