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
    private ArrayList regusers;
    private LayoutInflater mInflater;

    public RegUsersAdapter(Context context, ArrayList data) {
        this.mInflater = LayoutInflater.from(context);
        this.regusers = data;
    }


    @NonNull
    @Override
    public RegUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.regusersrecyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegUsersAdapter.ViewHolder holder, int position) {
        String username = (String) regusers.get(position);
        holder.myTextView.setText(username);
        Log.d(TAG,username);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.username);

        }


    }
}