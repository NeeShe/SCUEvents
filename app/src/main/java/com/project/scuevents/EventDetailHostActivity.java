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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.adapter.RegUsersAdapter;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.UserDetails;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EventDetailHostActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailHostActivity";
    final ArrayList regUserID =new ArrayList();
    final ArrayList regusers=new ArrayList();
    RegUsersAdapter regUsersAdapter;
    String edimageUrl ;
    String edeventTitle;
    String ewhen ;
    String etime ;
    String elocation;
    String edes ;
    String ehname;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_host);
        Log.d(TAG, "onCreate: started.");
        getIncomingIntent();
        getRegusers();


    }
    private void getRegusers(){
        FirebaseDatabase database = FireBaseUtilClass.getDatabase();
        DatabaseReference reference= database.getReference().child("Events").child(getIntent().getStringExtra("eid")).child("registeredUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    UserDetails user = dataSnapshot1.getValue(UserDetails.class);
                    regUserID.add(user.getUserID());
                    Log.d(TAG,user.getUserID());
                }
                Log.d(TAG,"in USERID ref");
                Log.d(TAG,Integer.toString(regUserID.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EventDetailHostActivity.this,"Error getting registered Users",Toast.LENGTH_LONG).show();
            }
        });

        reference=database.getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    UserDetails user = dataSnapshot1.getValue(UserDetails.class);
                    if(regUserID.contains(user.getUserID()))
                    {
                        regusers.add(user.getfName()+" "+user.getlName());
                        Log.d(TAG,user.getfName()+" "+user.getlName());

                    }


                }
                TextView numofattendees=findViewById(R.id.nattendee);
                numofattendees.setText(Integer.toString(regusers.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        RecyclerView recyclerView = findViewById(R.id.list_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG,"Befoer calling Adapter");
        regUsersAdapter = new RegUsersAdapter(this, regusers);
        recyclerView.setAdapter(regUsersAdapter);

        Log.d(TAG,"in regusersid"+Integer.toString(regUserID.size()));
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for Incoming Intents");

        Log.d(TAG,""+Integer.toString(regusers.size()));
        if(getIntent().hasExtra("eaimage") && getIntent().hasExtra("eatitle")
                && getIntent().hasExtra("estartdate") && getIntent().hasExtra("ealocation")
                && getIntent().hasExtra("eadescription") && getIntent().hasExtra("eendtime")
                && getIntent().hasExtra("eahname")){
            Log.d(TAG, "getIncomingIntent: foundintentExtras");
             edimageUrl = getIntent().getStringExtra("eaimage");
             edeventTitle = getIntent().getStringExtra("eatitle");
             ewhen = getIntent().getStringExtra("estartdate");
             etime = getIntent().getStringExtra("eendtime");
             elocation = getIntent().getStringExtra("ealocation");
             edes = getIntent().getStringExtra("eadescription");
             ehname = getIntent().getStringExtra("eahname");
            int total=getIntent().getIntExtra("totalseats",0);
            int available=getIntent().getIntExtra("availableseats",0);
            int numattendees=regUserID.size();
            setImage(edimageUrl,edeventTitle,ehname,ewhen,etime,elocation,edes,numattendees);
        }
    }
//String nattend
    private void setImage(String edimageUrl,String edeventTitle,String ehname,String ewhen, String etime,String elocation,String edes,int numa){
        Log.d(TAG, "setImage: setting the image and name to widgets");
        TextView title = findViewById(R.id.edtitle);
        title.setText(edeventTitle);

        TextView when = findViewById(R.id.edwhen);
        when.setText(ewhen);

        TextView hname = findViewById(R.id.edhname);
        hname.setText(ehname);

        TextView time = findViewById(R.id.edtime);
        time.setText(etime);

        TextView location = findViewById(R.id.edlocation);
        location.setText(elocation);

        TextView description = findViewById(R.id.eddescription);
        description.setText(edes);



        ImageView image = findViewById(R.id.edimage);

        Glide.with(this).asBitmap().load(edimageUrl).into(image);
    }

    public void edit(View view) {
        Log.d(TAG,"from eventdetail to host");
        Intent intent = new Intent(EventDetailHostActivity.this, HostEventDetailActivity.class);
        Log.d(TAG,getIntent().getStringExtra("eatitle"));
        Log.d(TAG,getIntent().getStringExtra("eatitle"));
        intent.putExtra("eaimage",getIntent().getStringExtra("eaimage"));
        intent.putExtra("eatitle",getIntent().getStringExtra("eatitle"));
        intent.putExtra("estartdate",getIntent().getStringExtra("estartdate"));
        intent.putExtra("eenddate",getIntent().getStringExtra("eenddate"));
        intent.putExtra("eendtime",getIntent().getStringExtra("eendtime"));
        intent.putExtra("estarttime",getIntent().getStringExtra("estarttime"));
        intent.putExtra("ealocation",getIntent().getStringExtra("ealocation"));
        intent.putExtra("eadescription",getIntent().getStringExtra("eadescription"));
        intent.putExtra("eahname",getIntent().getStringExtra("eahname"));
        intent.putExtra("eventtype",getIntent().getStringExtra("eventtype"));
        intent.putExtra("department",getIntent().getStringExtra("department"));
        intent.putExtra("eid",getIntent().getStringExtra("eid"));
        intent.putExtra("ehostid",getIntent().getStringExtra("ehostid"));
        intent.putExtra("ehosttoken",getIntent().getStringExtra("ehosttoken"));
        startActivity(intent);
    }
}

//comment this thing

/*public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, animalNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}*/
