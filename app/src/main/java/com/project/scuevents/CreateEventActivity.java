package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

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
    private final int PICK_VIDEO_REQUEST = 72;

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
    VideoView videoView;


    Calendar startDateCal;
    int datePicker; //0-start, 1-end

    Uri imageFilePath;
    Uri videoFilePath;
    MediaController mediaControls;

    FirebaseStorage storage;
    StorageReference storageReference;

    EventClass event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

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
        videoView = findViewById(R.id.videoView);

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

    //on click method of set video button
    public void setVideo(View v){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
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
        }
        if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            videoFilePath = data.getData();
            mediaControls= new MediaController(CreateEventActivity.this);
            mediaControls.setAnchorView(videoView);
            videoView.setMediaController(mediaControls);
            videoView.setVideoURI(Uri.parse(data.getDataString()));
            videoView.setVisibility(View.VISIBLE);
            videoView.start();
        }
    }

    //on click method of remove video button
    public void clearVideo(View view) {
        videoView.setVisibility(View.GONE);
    }

    //on click method of publish button
    public void publish(View view){
        this.validatePublish();
    }

    // validate and publish to firebase
    private void validatePublish() {

        //validate part
        String titleStr = eventTitle.getText().toString();
        if(TextUtils.isEmpty(titleStr.trim())){
            eventTitle.setError("Should not be empty");
            return;
        }
        String descStr = eventDescript.getText().toString();
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

        this.publishToFireBase(titleStr,descStr, sDateStr,sTimeStr,eDateStr,eTimeStr,
                locStr,catStr,deptStr,Integer.parseInt(seatStr),imageFilePath,videoFilePath);
    }

    //publish to firebase
    private void  publishToFireBase(String title, String desc, String startDate, String startTime, String endDate,
                                       String endTime, String loc, String type, String dept, int totSeats,
                                       Uri imageUri, Uri videoUri){
        //get new id
       String pushId = FireBaseUtilClass.getDatabaseReference().child("Events").push().getKey();
        //String pushId = "abc";

        //get host id and host name
        SharedPreferences sh = getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        String hostId = sh.getString("USER_ID", "");
        String hostName = sh.getString("USER_NAME","");

        //create eventclass instance
        event = new EventClass(pushId,title,desc,hostName,hostId,startDate,startTime,endDate,
                endTime,loc,type,dept,totSeats);
        //upload image to storage
        uploadStorage(imageUri,true);
    }

    //upload media to storage
    private void uploadStorage(Uri uri, final boolean isImage){
        String folder="images/";
        if(!isImage){
            folder = "videos/";
        }
        final StorageReference ref = storageReference.child(folder + UUID.randomUUID().toString());
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CreateEventActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        StorageMetadata sMetaData= taskSnapshot.getMetadata();
                          Task<Uri> downloadUrl = ref.getDownloadUrl();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>(){
                           @Override
                           public void onSuccess(final Uri uri) {
                               if(isImage){
                                   event.setImageUrl(uri.toString());
                                   //call upload to database method
                                   uploadDatabase();
                               }
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
                Toast.makeText(getBaseContext(),"Published Successfully!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateEventActivity.this, "Failed to publish"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
