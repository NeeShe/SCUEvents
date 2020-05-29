package com.project.scuevents;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HostEventDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "HostEventDetailActivity";
    private final static String DEBUG_TAG = "CreateEventActivity";
    private final int PICK_IMAGE_REQUEST = 71;
    int datePicker;
    EditText eventTitle;
    EditText eventDescript;
    EditText startDate;
    EditText startTime;
    EditText endDate;
    EditText endTime;
    EditText totalSeats;
    ImageView imageView;
    Uri imageFilePath;

    Calendar startDateCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_event_detail);

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
            String edimageUrl = getIntent().getStringExtra("eaimage");
            String edeventTitle = getIntent().getStringExtra("eatitle");
            String estartdate = getIntent().getStringExtra("estartdate");
            String estarttime = getIntent().getStringExtra("estarttime");
            String elocation = getIntent().getStringExtra("ealocation");
            String edes = getIntent().getStringExtra("eadescription");
            //String ehname = getIntent().getStringExtra("eahname");
            String eendtime=getIntent().getStringExtra("eendtime");
            String eenddate =getIntent().getStringExtra("eenddate");
            String category =getIntent().getStringExtra("eventtype");
            String department =getIntent().getStringExtra("department");
//            ArrayList regusers=getIntent().getCharSequenceArrayListExtra("regusers");
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

    private void setImage(String edimageUrl,String edeventTitle,String estartdate, String estarttime,
                          String elocation,String edes,String eenddate,String eendtime,String category,String department){
        Log.d(TAG, "setImage: setting the image and name to widgets");
        EditText title = findViewById(R.id.eventTitle);
        title.setText(edeventTitle);

        EditText when = findViewById(R.id.startDateInput);
        when.setText(estartdate);

        EditText enddate = findViewById(R.id.endDateInput);
        enddate.setText(eenddate);

//        TextView hname = findViewById(R.id.edhname);
//        hname.setText(ehname);

        EditText time = findViewById(R.id.startTimeInput);
        time.setText(estarttime);

        EditText endtime = findViewById(R.id.endTimeInput);
        endtime.setText(eendtime);

        Spinner locSpinner=findViewById(R.id.locationSpinner);
        locSpinner.setSelection(getIndex(locSpinner,elocation));

        Spinner depatspinner=findViewById(R.id.deptSpinner);
        depatspinner.setSelection(getIndex(depatspinner,department));

        Spinner catspinner=findViewById(R.id.typeSpinner);
        catspinner.setSelection(getIndex(catspinner,category));

        EditText description = findViewById(R.id.eventDescription);
        description.setText(edes);

        ImageView image = findViewById(R.id.imageview1);

        Glide.with(this).asBitmap().load(edimageUrl).into(image);
    }

}
