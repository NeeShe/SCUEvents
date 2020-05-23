package com.project.scuevents.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.project.scuevents.R;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.MessageClass;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<MessageClass> itemList;
    private Context context;
    public static int CONF_DISC = 1, MENT_DISC = 2, MENT_CHAT = 3, PANEL = 4;
    private String eventID;


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView emailTextView, timeTextView, msgTextView;
        private MessageClass item;
        private CardView parent;


        public MessageViewHolder(View view) {
            super(view);

            emailTextView = view.findViewById(R.id.emailTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            msgTextView = view.findViewById(R.id.msgTextView);
            parent = view.findViewById(R.id.messageCard);
        }

        public MessageClass getItem() {
            return item;
        }

        public void setItem(MessageClass item) {
            this.item = item;
        }
    }


    public MessageAdapter(List<MessageClass> msgList, Context context, String eventID) {
        this.itemList = msgList;
        this.context = context;
        this.eventID = eventID;
    }



    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_chat_recyclerview, parent, false);

        return new MessageViewHolder(itemView);
    }

    private int convertDpToPx(float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        final MessageClass item = itemList.get(position);
        holder.emailTextView.setText(item.getEmail());
        holder.timeTextView.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", item.getMsgTime()));
        holder.msgTextView.setText(item.getMsg());

        if (item.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            params.setMargins(convertDpToPx(60), 0, convertDpToPx(8), convertDpToPx(7));
            holder.parent.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            params.setMargins(convertDpToPx(8), 0, convertDpToPx(60), convertDpToPx(7));
            holder.parent.setLayoutParams(params);
        }


        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (item.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {


                    String[] colors = {"Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {

                                case 0:


                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                    builder.setTitle("Confirm");
                                    builder.setMessage("Are you sure to delete?");

                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {

                                            deleteMe(item, eventID, holder.parent);
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            // Do nothing
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    break;
                            }
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });
        holder.setItem(item);

    }

    private void deleteMe(MessageClass item, String eventID, final CardView cardView) {
        FireBaseUtilClass.getDatabaseReference().child("Chat").child(eventID).child("messages").child(item.getChatID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "failed to remove the message, try again!", Toast.LENGTH_SHORT).show();
                }
            });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
