package com.project.scuevents;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HostEventDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "HostEventDetailActivity";
    private final static String DEBUG_TAG = "CreateEventActivity";
    private final int PICK_IMAGE_REQUEST = 71;
    int datePicker;
    TextView eventTitle;
    TextView eventDescript;
    EditText startDate;
    EditText startTime;
    EditText endDate;
    EditText endTime;
    EditText totalSeats;
    ImageView imageView;
    Uri imageFilePath;
    EventClass event;
    String edimageUrl ;
    String edeventTitle;
    String estartdate ;
    String estarttime;
    String elocation ;
    String edes ;
    String ehname;
    String eendtime;
    String eenddate ;
    String category ;
    String department;
    String eventid;
    int TotalSeats;
    TextView title ;

    EditText when;
    EditText enddate ;
//        TextView hname = findViewById(R.id.edhname);
//        hname.setText(ehname);

    EditText time ;

    EditText endtime ;

    Spinner locSpinner;

    Spinner depatspinner;

    Spinner catspinner;

    TextView description ;
    ImageView image;

    //ImageView image = findViewById(R.id.imageview1);




    Calendar startDateCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_event_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        datePicker=0;
        eventTitle = findViewById(R.id.eventTitle);
        eventDescript = findViewById(R.id.eventDescription);

        startDate = findViewById(R.id.startDateInput);
        startDate.setInputType(InputType.TYPE_NULL);

        startTime = findViewById(R.id.startTimeInput);
        startTime.setInputType(InputType.TYPE_NULL);

        endDate = findViewById(R.id.endDateInput);
        endDate.setInputType(InputType.TYPE_NULL);

        endTime = findViewById(R.id.endTimeInput);
        endTime.setInputType(InputType.TYPE_NULL);

        imageView = findViewById(R.id.imageview1);
        Log.d(TAG, "onCreate: started.");
        getIncomingIntent();



    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for Incoming Intents");
        Intent i =getIntent();
        Log.e("DEBUG","Event ID:"+i.hasExtra("eaimage"));
        Log.e("DEBUG","Event Title:"+i.hasExtra("eatitle"));
        Log.e("DEBUG","Event Description:"+i.hasExtra("estartdate"));
        Log.e("DEBUG","Host Name:"+i.hasExtra("ealocation"));

        if(getIntent().hasExtra("eaimage") && getIntent().hasExtra("eatitle")
                && getIntent().hasExtra("estartdate") && getIntent().hasExtra("ealocation")
                && getIntent().hasExtra("eadescription") && getIntent().hasExtra("eendtime")
                && getIntent().hasExtra("eahname")){
            Log.d(TAG, "getIncomingIntent: foundintentExtras");
            Log.d(TAG, getIntent().getStringExtra("eatitle"));
            edimageUrl = getIntent().getStringExtra("eaimage");
             edeventTitle = getIntent().getStringExtra("eatitle");
             estartdate = getIntent().getStringExtra("estartdate");
             estarttime = getIntent().getStringExtra("estarttime");
             elocation = getIntent().getStringExtra("ealocation");
             edes = getIntent().getStringExtra("eadescription");
             ehname = getIntent().getStringExtra("eahname");
             eendtime=getIntent().getStringExtra("eendtime");
             eenddate =getIntent().getStringExtra("eenddate");
             category =getIntent().getStringExtra("eventtype");
             department =getIntent().getStringExtra("department");
             eventid=getIntent().getStringExtra("eventid");
             TotalSeats=getIntent().getIntExtra("totalseats",0);


            //String eventId, String eventTitle, String eventDescription, String hostName, String hostID, String hostToken,
              //      String eventDate, String eventTime,
                //    String endDate, String endTime, String eventLocation, String eventType, String department

            setImage(edimageUrl,edeventTitle,estartdate,estarttime,elocation,edes,eenddate,eendtime,category,department);

        }
    }
//    // On click method of start date edit text
    public void setStartDate(View view) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePicker = 0;
        new DatePickerDialog(this, this, year, month, day).show();
    }

    @Override
    public void onBackPressed() {
        Log.e(DEBUG_TAG,"On back pressed");
        super.onBackPressed();
    }

    //on date picked
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DATE,dayOfMonth);
        String dateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        if(datePicker == 0){
            startDate.setText(dateString);
            startDateCal = c;
        }else{
            endDate.setText(dateString);
        }

    }

    //on click method of start time edit text
    public void setStartTime(View view) {
        this.setTime(true);
    }

    //on click method of end date edit text
    public void setEndDate(View view) {
        Calendar c;
        if(startDateCal != null){
            c=startDateCal;
        }else{
            c = Calendar.getInstance();
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePicker = 1;
        new DatePickerDialog(    this, this, year, month, day).show();
    }

    //on click method of end time edit text
    public void setEndTime(View view) {
        this.setTime(false);
    }

    //set time function
    private void setTime(final boolean isStart){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String pmam = "AM";
                if (selectedHour >= 12) {
                    pmam = "PM";
                    selectedHour -= 12;
                }
                if (selectedHour == 0) {
                    selectedHour = 12;
                }
                String time = selectedHour + ":" + String.format("%02d", selectedMinute) + " " + pmam;
                if(isStart){
                    startTime.setText(time);
                }else{
                    endTime.setText(time);
                }
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    //on click method of set image button
    public void setImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    //private method of your class
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setImage(String edimageUrl,String edeventTitle,String estartdate, String estarttime,
                          String elocation,String edes,String eenddate,String eendtime,String category,String department){
        Log.d(TAG, "setImage: setting the image and name to widgets");
         title = findViewById(R.id.eventTitle);
        title.setText(edeventTitle);

        when = findViewById(R.id.startDateInput);
        when.setText(estartdate);

         enddate = findViewById(R.id.endDateInput);
        enddate.setText(eenddate);

        totalSeats = findViewById(R.id.totalSeatInput);
        totalSeats.setText(TotalSeats+"");

         time = findViewById(R.id.startTimeInput);
        time.setText(estarttime);

         endtime = findViewById(R.id.endTimeInput);
        endtime.setText(eendtime);

         locSpinner=findViewById(R.id.locationSpinner);
        locSpinner.setSelection(getIndex(locSpinner,elocation));

         depatspinner=findViewById(R.id.deptSpinner);
        depatspinner.setSelection(getIndex(depatspinner,department));

         catspinner=findViewById(R.id.typeSpinner);
        catspinner.setSelection(getIndex(catspinner,category));

         description = findViewById(R.id.eventDescription);
        description.setText(edes);

        image = findViewById(R.id.imageview1);

        Glide.with(this).asBitmap().load(edimageUrl).into(image);
    }
public void  updatedatabase(){
    event=new EventClass();
    event.setEventTitle(title.getText().toString());
    event.setEventTime(startTime.getText().toString());
    event.setDepartment(depatspinner.getSelectedItem().toString());
    event.setEndDate(endDate.getText().toString());
    event.setEventDate(startDate.getText().toString());
    event.setEventDescription(eventDescript.getText().toString());
    event.setEventType(catspinner.getSelectedItem().toString());

    FirebaseDatabase database = FireBaseUtilClass.getDatabase();
    Log.d(TAG, eventid+ "event id" );
//                                        database = FirebaseDatabase.getInstance();

    DatabaseReference reference= database.getReference().child("Events");
    reference.child(eventid).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            dataSnapshot.getRef().child("eventType").setValue(catspinner.getSelectedItem().toString());
            dataSnapshot.getRef().child("endDate").setValue(endDate.getText().toString());
            dataSnapshot.getRef().child("department").setValue(depatspinner.getSelectedItem().toString());
            dataSnapshot.getRef().child("eventDate").setValue(startDate.getText().toString());
            dataSnapshot.getRef().child("endTime").setValue(endTime.getText().toString());
            dataSnapshot.getRef().child("eventLocation").setValue(locSpinner.getSelectedItem().toString());
            dataSnapshot.getRef().child("eventTime").setValue(startTime.getText().toString());
            dataSnapshot.getRef().child("eventTitle").setValue(eventTitle.getText().toString());
            dataSnapshot.getRef().child("eventDescription").setValue(eventDescript.getText().toString());
            dataSnapshot.getRef().child("totalSeats").setValue(Integer.parseInt(totalSeats.getText().toString()));

        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("User", databaseError.getMessage());
        }
    });



    Toast.makeText(HostEventDetailActivity.this,"Event details saved",Toast.LENGTH_LONG).show();

}
    public void update(View view) {



            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Do you want to save the information?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                    updatedatabase();
                                }
                            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }



//        event=new EventClass();
//        event.setEventTitle(title.getText().toString());
//        event.setEventTime(startTime.getText().toString());
//        event.setDepartment(depatspinner.getSelectedItem().toString());
//        event.setEndDate(endDate.getText().toString());
//        event.setEventDate(startDate.getText().toString());
//        event.setEventDescription(eventDescript.getText().toString());
//        event.setEventType(catspinner.getSelectedItem().toString());
//        FirebaseDatabase database = FireBaseUtilClass.getDatabase();
//            //database = FirebaseDatabase.getInstance();
//
//        DatabaseReference reference= database.getReference().child("Events");
//            reference.child(eventid).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    dataSnapshot.getRef().child("eventType").setValue(catspinner.getSelectedItem().toString());
//                    dataSnapshot.getRef().child("endDate").setValue(endDate.getText().toString());
//                    dataSnapshot.getRef().child("department").setValue(depatspinner.getSelectedItem().toString());
//                    dataSnapshot.getRef().child("eventDate").setValue(startDate.getText().toString());
//                    dataSnapshot.getRef().child("endTime").setValue(endTime.getText().toString());
//                    dataSnapshot.getRef().child("eventLocation").setValue(locSpinner.getSelectedItem().toString());
//                    dataSnapshot.getRef().child("eventTime").setValue(startTime.getText().toString());
//                    dataSnapshot.getRef().child("eventTitle").setValue(eventTitle.getText().toString());
//                    dataSnapshot.getRef().child("eventDescription").setValue(eventDescript.getText().toString());
//
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("User", databaseError.getMessage());
//                }
//            });


    }
//}
