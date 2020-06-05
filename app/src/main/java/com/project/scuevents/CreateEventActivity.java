package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;


public class CreateEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private final static String DEBUG_TAG = "CreateEventActivity";
    private final int PICK_IMAGE_REQUEST = 71;
    private final int SET_CALENDAR_REQUEST = 72;

    EditText eventTitle;
    EditText eventDescript;
    EditText startDate;
    EditText startTime;
    EditText endDate;
    EditText endTime;
    EditText totalSeats;

    Spinner locSpinner;
    Spinner catSpinner;
    Spinner deptSpinner;

    ImageView imageView;
    Uri imageFilePath;

    Calendar startDateCal;
    int startHour;
    int startMin;
    Calendar endDateCal;
    int endHour;
    int endMin;
    int datePicker; //0-start, 1-end

    ProgressDialog progressDialog;
    FirebaseStorage storage;

    StorageReference storageReference;

    EventClass event;

    String titleStr;
    String descStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().setTitle("Create Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //Initialize variables
        datePicker=0;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

        locSpinner = findViewById(R.id.locationSpinner);
        catSpinner = findViewById(R.id.typeSpinner);
        deptSpinner = findViewById(R.id.deptSpinner);

        totalSeats = findViewById(R.id.totSeatInput);

        imageView = findViewById(R.id.imageview1);

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
    // On click method of start date edit text
    public void setStartDate(View view) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePicker = 0;
        new DatePickerDialog(this, this, year, month, day).show();
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
           endDateCal = c;
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

        int tempMin;
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                int tempHour = selectedHour;
                int tempMin = selectedMinute;
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
                    startHour = tempHour;
                    startMin = tempMin;
                }else{
                    endTime.setText(time);
                    endHour = tempHour;
                    endMin = tempMin;
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


    // on activity result () after getting image and video content
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            imageFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }else  if(requestCode == SET_CALENDAR_REQUEST){
            Log.e(DEBUG_TAG, "resultCode:"+resultCode);
        }

    }

    //on click method of publish button
    public void publish(View view){
        this.validatePublish();
    }

    // validate and publish to firebase
    private void validatePublish() {

        //validate part
        titleStr = eventTitle.getText().toString();
        if(TextUtils.isEmpty(titleStr.trim())){
            eventTitle.setError("Should not be empty");
            return;
        }
        descStr = eventDescript.getText().toString();
        if(TextUtils.isEmpty(descStr.trim())){
            eventDescript.setError("Should not be empty");
            return;
        }

        String sDateStr = startDate.getText().toString();
        if(TextUtils.isEmpty(sDateStr.trim())){
            startDate.setError("Select Date");
            return;
        }
        String sTimeStr = startTime.getText().toString();
        if(TextUtils.isEmpty(sTimeStr.trim())){
            startTime.setError("Select Time");
            return;
        }

        String eDateStr = endDate.getText().toString();
        if(TextUtils.isEmpty(eDateStr.trim())){
            endDate.setError("Select Date");
            return;
        }
        String eTimeStr = endTime.getText().toString();
        if(TextUtils.isEmpty(eTimeStr.trim())){
            endTime.setError("Select Time");
            return;
        }
        String locStr=locSpinner.getSelectedItem().toString();
        if(locStr.equals("Select Location")){
            Toast.makeText(this,"Select Location",Toast.LENGTH_SHORT).show();
            return;
        }

        String catStr=catSpinner.getSelectedItem().toString();
        if(catStr.equals("Select Event Category")){
            Toast.makeText(this,"Select Event Category",Toast.LENGTH_SHORT).show();
            return;
        }

        String deptStr=deptSpinner.getSelectedItem().toString();
        if(deptStr.equals("Select Department")){
            Toast.makeText(this,"Select Department",Toast.LENGTH_SHORT).show();
            return;
        }
        String seatStr = totalSeats.getText().toString();
        if(TextUtils.isEmpty(seatStr.trim())){
            totalSeats.setError("Set total seats");
            return;
        }

        if(imageFilePath == null){
            Toast.makeText(this,"Select Cover Picture",Toast.LENGTH_SHORT).show();
            return;
        }

        startDateCal.set(Calendar.HOUR_OF_DAY,startHour);
        startDateCal.set(Calendar.MINUTE,startMin);
        long startTimestamp = startDateCal.getTimeInMillis();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,c.get(Calendar.YEAR));
        c.set(Calendar.MONTH,c.get(Calendar.MONTH));
        c.set(Calendar.DATE,c.get(Calendar.DATE));
        c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE,c.get(Calendar.MINUTE));
        long currentTime = c.getTimeInMillis();
        if(startTimestamp < currentTime){
            Toast.makeText(this,"Invalid start date/time",Toast.LENGTH_SHORT).show();
            return;
        }

        endDateCal.set(Calendar.HOUR_OF_DAY,endHour);
        endDateCal.set(Calendar.MINUTE,endMin);

        if(endDateCal.getTimeInMillis() < startTimestamp){
            Toast.makeText(this,"Invalid end date/time",Toast.LENGTH_SHORT).show();
            return;
        }

        this.publishToFireBase(titleStr,descStr, sDateStr,sTimeStr,eDateStr,eTimeStr,
                locStr,catStr,deptStr,Integer.parseInt(seatStr),imageFilePath,startTimestamp);
    }

    //publish to firebase
    private void  publishToFireBase(String title, String desc, String startDate, String startTime, String endDate,
                                       String endTime, String loc, String type, String dept, int totSeats,
                                       Uri imageUri, long startTimestamp){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Publishing");
        progressDialog.show();
        //get new id
       String pushId = FireBaseUtilClass.getDatabaseReference().child("Events").push().getKey();

        //get host id and host name
        SharedPreferences sh = getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        String hostId = sh.getString("USER_ID", "");
        String hostName = sh.getString("USER_NAME","");

        SharedPreferences pref = getSharedPreferences("MyPreferenceFileName", MODE_PRIVATE);
        String hostToken=pref.getString("UserToken","");

        //create eventclass instance
        event = new EventClass(pushId,title,desc,hostName,hostId,hostToken,startDate,startTime,endDate,
                endTime,loc,type,dept,totSeats,totSeats,startTimestamp);
        //upload image to storage
        uploadStorage(imageUri);
    }

    //upload media to storage
    private void uploadStorage(Uri uri){
        String folder="images/";
        final StorageReference ref = storageReference.child(folder + UUID.randomUUID().toString());
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(CreateEventActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        StorageMetadata sMetaData= taskSnapshot.getMetadata();
                          Task<Uri> downloadUrl = ref.getDownloadUrl();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>(){
                           @Override
                           public void onSuccess(final Uri uri) {
                                   event.setImageUrl(uri.toString());
                                   //call upload to database method
                                   uploadDatabase();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateEventActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //upload event to database
    private void uploadDatabase(){
        FireBaseUtilClass.getDatabaseReference().child("Events").child(event.getEventID()).setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(),"Published Successfully!",Toast.LENGTH_SHORT).show();
                clearFields();
                //CreateEventActivity.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateEventActivity.this, "Failed to publish"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        eventTitle.setText("");
        eventDescript.setText("");
        startDate.setText("");
        startTime.setText("");
        endDate.setText("");
        endTime.setText("");
        totalSeats.setText("");
        //TODO decide if add to calendar required
        addToCalendar();
    }

    private void addToCalendar() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create(); //Read Update
        alertDialog.setTitle("Add to Calendar");
        alertDialog.setMessage("Would you like to add the event to Calendar?");
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }});

        alertDialog.setButton( Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener()  {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateCal.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateCal.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, titleStr)
                        .putExtra(CalendarContract.Events.DESCRIPTION,descStr)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "Santa Clara University")
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivityForResult(intent,SET_CALENDAR_REQUEST);
            }});
        alertDialog.show();

    }
}
