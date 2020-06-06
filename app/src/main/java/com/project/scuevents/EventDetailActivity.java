package com.project.scuevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.MessageClass;
import com.project.scuevents.model.UserDetails;

import java.util.Calendar;

public class EventDetailActivity extends AppCompatActivity{
    private static final String TAG = "EventDetailActivity";
    private EventClass group;

    TextView edeventTitle;
    TextView when;
    TextView hname;
    TextView time;
    TextView location;
    TextView description;
    TextView dept;
    TextView type;
    TextView seats;

    ImageView image;
    Button RegButton;
    DatabaseReference db;
    long todayTimestamp;
    Calendar startDateCal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetail);
        Log.d(TAG, "onCreate: started.");
        // getIncomingIntent();
        RegButton = findViewById(R.id.edregister);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        group = (EventClass) i.getSerializableExtra("Object");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,c.get(Calendar.YEAR));
        c.set(Calendar.MONTH,c.get(Calendar.MONTH));
        c.set(Calendar.DATE,c.get(Calendar.DATE));
        startDateCal = c;
        todayTimestamp = startDateCal.getTimeInMillis();

        SharedPreferences sh = getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        final String userId = sh.getString("USER_ID", "");
        Log.d(TAG, "onCreate: "+userId);
        db = FirebaseDatabase.getInstance().getReference();
        Query query = db.child("Events").child(group.getEventID()).child("registeredUsers");

        //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query q=db.child("Events").child(group.getEventID()).child("registeredUsers").child(userId);
        Log.d(TAG, "onCreateUID: "+q);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                if (dataSnapshot.getValue()==null) {
                    RegButton.setText("Register");
                }
                else {
                    RegButton.setText("Deregister");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onCreate: "+query);

        edeventTitle = findViewById(R.id.edtitle);
        edeventTitle.setText(group.getEventTitle());

        when = findViewById(R.id.edwhen);
        when.setText(group.getEventDate());

        hname = findViewById(R.id.edhname);
        hname.setText("(event hosted by "+group.getHostName()+")");

        time = findViewById(R.id.edtime);
        time.setText(group.getEventTime()+" to "+group.getEndTime());

        location = findViewById(R.id.edlocation);
        location.setText(group.getEventLocation());

        description = findViewById(R.id.eddescription);
        description.setText(group.getEventDescription());

        image = findViewById(R.id.edimage);
        Glide.with(this).asBitmap().load(group.getImageUrl()).into(image);

        dept = findViewById(R.id.eddept);
        dept.setText(group.getDepartment());

        type = findViewById(R.id.edeventtype);
        type.setText(group.getEventType());

        seats = findViewById(R.id.edseats);
        seats.setText("Available Seats "+group.getAvailableSeats()+"/"+group.getTotalSeats());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void regButtonClick(View view){
        //check the present time stamp
        long startTimeStamp = group.getStartTimestamp();
        if(Long.signum(startTimeStamp-todayTimestamp ) != -1 && group.getAvailableSeats()>0) {
            if (RegButton.getText() == "Register") {
                registerUsers(view);
            } else if (RegButton.getText() == "Deregister") {
                deregisterUsers(view);
            } else {
                Toast.makeText(getBaseContext(), "System not responding", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(group.getAvailableSeats()==0){
                if (RegButton.getText() == "Deregister") {
                    deregisterUsers(view);
                }
                else {
                    Toast.makeText(getBaseContext(), "No seats available", Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(getBaseContext(), "Event registration expired", Toast.LENGTH_SHORT).show();
        }
    }

    private void deregisterUsers(View view) {
        db = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sh = getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        final String userId = sh.getString("USER_ID", "");
        Query delQuery=db.child("Events").child(group.getEventID()).child("registeredUsers").child(userId);
        delQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                Log.d(TAG, "onDataChange:available seats"+group.getAvailableSeats());
                updateDeregcount();
                RegButton.setText("Register");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        Query delEvent=db.child("Users").child(userId).child("registeredEvents").child(group.getEventID());
        delEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        Toast.makeText(getBaseContext(),"Deregistered Successfully",Toast.LENGTH_SHORT).show();
        //RegButton.setText("Register");
    }

    public void registerUsers(View view){
        String regID = FireBaseUtilClass.getDatabaseReference().child("Events").child("registeredUsers").push().getKey();
        SharedPreferences sh = getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        String userId = sh.getString("USER_ID", "");
        SharedPreferences pref = getSharedPreferences("MyPreferenceFileName", MODE_PRIVATE);
        String userToken=pref.getString("UserToken","");
        this.addUsersToFirebase(new UserDetails(userId,userToken));

    }
    private void addUsersToFirebase(final UserDetails item) {
        FireBaseUtilClass.getDatabaseReference().child("Events").child(group.getEventID()).child("registeredUsers").child(item.getUserID()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getBaseContext(),"registered successfully",Toast.LENGTH_SHORT).show();
                updateRegCount();
                RegButton.setText("Deregister");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),"registeration unsuccessfull",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateRegCount(){
        FireBaseUtilClass.getDatabaseReference().child("Events").child(group.getEventID()).child("availableSeats").setValue(group.getAvailableSeats()-1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(),"registered successfully",Toast.LENGTH_SHORT).show();
                group.setAvailableSeats(group.getAvailableSeats()-1);
                seats.setText("Available Seats "+group.getAvailableSeats()+"/"+group.getTotalSeats());
                RegButton.setText("Deregister");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),"registeration unsuccessfull",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateDeregcount(){
        FireBaseUtilClass.getDatabaseReference().child("Events").child(group.getEventID()).child("availableSeats").setValue(group.getAvailableSeats()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(),"Deregistered successfully",Toast.LENGTH_SHORT).show();
                group.setAvailableSeats(group.getAvailableSeats()+1);
                seats.setText("Available Seats "+group.getAvailableSeats()+"/"+group.getTotalSeats());
                RegButton.setText("Register");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),"unsuccessfull",Toast.LENGTH_SHORT).show();
            }
        });

    }



}
