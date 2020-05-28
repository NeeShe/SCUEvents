package com.project.scuevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.MessageClass;
import com.project.scuevents.model.UserDetails;

public class EventDetailActivity extends AppCompatActivity{
    private static final String TAG = "EventDetailActivity";
    private EventClass group;

    TextView edeventTitle;
    TextView when;
    TextView hname;
    TextView time;
    TextView location;
    TextView description;
    ImageView image;
    Context context;
    Button RegButton;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetail);
        Log.d(TAG, "onCreate: started.");
        // getIncomingIntent();
        RegButton = (Button) findViewById(R.id.edregister);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        group = (EventClass) i.getSerializableExtra("Object");

        edeventTitle = findViewById(R.id.edtitle);
        edeventTitle.setText(group.getEventTitle());

        when = findViewById(R.id.edwhen);
        when.setText(group.getEventDate());

        hname = findViewById(R.id.edhname);
        hname.setText("(event hosted by "+group.getHostName()+")");

        time = findViewById(R.id.edtime);
        time.setText(group.getEventTime());

        location = findViewById(R.id.edlocation);
        location.setText(group.getEventLocation());

        description = findViewById(R.id.eddescription);
        description.setText(group.getEventDescription());

        image = findViewById(R.id.edimage);
        Glide.with(this).asBitmap().load(group.getImageUrl()).into(image);
    }



    public void registerUsers(View view){
        String regID = FireBaseUtilClass.getDatabaseReference().child("Events").child("registeredUsers").push().getKey();
        SharedPreferences sh = getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        String userId = sh.getString("USER_ID", "");
       // String dName = sh.getString("USER_NAME","");


        SharedPreferences pref = getSharedPreferences("MyPreferenceFileName", MODE_PRIVATE);
        String uToken=pref.getString("UserToken","");
        this.addUsersToFirebase(new UserDetails(userId,uToken));
    }
    private void addUsersToFirebase(final UserDetails item) {
        FireBaseUtilClass.getDatabaseReference().child("Events").child(group.getEventID()).child("registeredUsers").child(item.getUserID()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(),"registered successfully",Toast.LENGTH_SHORT).show();
                RegButton.setText("Deregister");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),"registeration unsuccessfull",Toast.LENGTH_SHORT).show();
            }
        });
    }






}
