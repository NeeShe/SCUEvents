package com.project.scuevents;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EventDetailActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailActivity";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetail);
        Log.d(TAG, "onCreate: started.");
        getIncomingIntent();


        final Button RegButton = (Button) findViewById(R.id.edregister);
        RegButton.setTag(1);
        RegButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final int status =(Integer) v.getTag();
                if(status == 1) {
                    RegButton.setText("Deregister");
                    v.setTag(0);
                } else {
                    RegButton.setText("Register");
                    v.setTag(1);
                }
            }
        });

    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for Incoming Intents");
        if(getIntent().hasExtra("eaimage") && getIntent().hasExtra("eatitle")
            && getIntent().hasExtra("eawhen") && getIntent().hasExtra("ealocation")
            && getIntent().hasExtra("eadescription") && getIntent().hasExtra("eatime")
                && getIntent().hasExtra("eahname")){
            Log.d(TAG, "getIncomingIntent: foundintentExtras");
            String edimageUrl = getIntent().getStringExtra("eaimage");
            String edeventTitle = getIntent().getStringExtra("eatitle");
            String ewhen = getIntent().getStringExtra("eawhen");
            String etime = getIntent().getStringExtra("eatime");
            String elocation = getIntent().getStringExtra("ealocation");
            String edes = getIntent().getStringExtra("eadescription");
            String ehname = getIntent().getStringExtra("eahname");
            setImage(edimageUrl,edeventTitle,ehname,ewhen,etime,elocation,edes);
        }
    }

    private void setImage(String edimageUrl,String edeventTitle,String ehname,String ewhen, String etime,String elocation,String edes){
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






}
