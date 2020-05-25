package com.project.scuevents;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HostEventDetailActivity extends AppCompatActivity {
    private static final String TAG = "HostEventDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_event_detail);

        Log.d(TAG, "onCreate: started.");
        getIncomingIntent();



    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for Incoming Intents");
        if(getIntent().hasExtra("eaimage") && getIntent().hasExtra("eatitle")
                && getIntent().hasExtra("eawhen") && getIntent().hasExtra("ealocation")
                && getIntent().hasExtra("eadescription") && getIntent().hasExtra("eatime")
                && getIntent().hasExtra("eahname") && getIntent().hasExtra("totalseats")
        && getIntent().hasExtra("availableseats") && getIntent().hasExtra("regusers")){
            Log.d(TAG, "getIncomingIntent: foundintentExtras");
            String edimageUrl = getIntent().getStringExtra("eaimage");
            String edeventTitle = getIntent().getStringExtra("eatitle");
            String estartdate = getIntent().getStringExtra("estartdate");
            String estarttime = getIntent().getStringExtra("estarttime");
            String elocation = getIntent().getStringExtra("ealocation");
            String edes = getIntent().getStringExtra("eadescription");
            String ehname = getIntent().getStringExtra("eahname");
            String eendtime=getIntent().getStringExtra("eendtime");
            String eenddate =getIntent().getStringExtra("eenddate");
//            ArrayList regusers=getIntent().getCharSequenceArrayListExtra("regusers");
            setImage(edimageUrl,edeventTitle,ehname,estartdate,estarttime,elocation,edes,eenddate,eendtime);

        }
    }

    private void setImage(String edimageUrl,String edeventTitle,String ehname,String estartdate, String estarttime,
                          String elocation,String edes,String eenddate,String eendtime){
        Log.d(TAG, "setImage: setting the image and name to widgets");
        EditText title = findViewById(R.id.eventTitle);
        title.setText(edeventTitle);

        EditText when = findViewById(R.id.startDateInput);
        when.setText(estartdate);

        EditText enddate = findViewById(R.id.endDateInput);
        enddate.setText(eenddate);

        TextView hname = findViewById(R.id.edhname);
        hname.setText(ehname);

        EditText time = findViewById(R.id.startTimeInput);
        time.setText(estarttime);

        EditText endtime = findViewById(R.id.endTimeInput);
        endtime.setText(eendtime);

        TextView location = findViewById(R.id.edlocation);
        location.setText(elocation);

        EditText description = findViewById(R.id.eventDescription);
        description.setText(edes);

        ImageView image = findViewById(R.id.edimage);

        Glide.with(this).asBitmap().load(edimageUrl).into(image);
    }

}
