package com.project.scuevents.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.annotations.Nullable;
import com.project.scuevents.R;
import com.project.scuevents.adapter.MessageAdapter;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.GroupChatClass;
import com.project.scuevents.model.MessageClass;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private GroupChatClass group;
    private static final String DEBUG_TAG ="ChatActivity";

    private RecyclerView recyclerView;
    private EditText msgEditText;
    private Toolbar toolbar;
    private LinearLayout allOKLayout;

    private MessageAdapter mAdapter;
    private List<MessageClass> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent i = getIntent();
        group = (GroupChatClass) i.getSerializableExtra("Object");
        toolbar = findViewById(R.id.chattoolbar);
        toolbar.setTitle(group.getEventTitle());


        recyclerView = findViewById(R.id.msgRecyclerView);
        msgEditText = findViewById(R.id.msgEditText);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new MessageAdapter(itemList, this, group.getEventID());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        itemList.clear();
        this.retrieveMessages();
    }

    private void retrieveMessages() {
        FireBaseUtilClass.getDatabaseReference().child("Chat").child(group.getEventID()).child("messages").orderByChild("msgTime").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageClass temp = dataSnapshot.getValue(MessageClass.class);
                itemList.add(temp);
                mAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(itemList.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                MessageClass temp = dataSnapshot.getValue(MessageClass.class);
                itemList.remove(temp);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(View view) {
        String chatID = FireBaseUtilClass.getDatabaseReference().child("Chat").child(group.getEventID()).child("messages").push().getKey();
        writeMsgToFirebase(new MessageClass(chatID, FirebaseAuth.getInstance().getCurrentUser().getEmail(), msgEditText.getText().toString()));

    }

    private void writeMsgToFirebase(final MessageClass item) {
        FireBaseUtilClass.getDatabaseReference().child("Chat").child(group.getEventID()).child("messages").child(item.getChatID()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                msgEditText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                msgEditText.setError(e.getMessage());
            }
        });
    }
}

