package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.adapter.EventAdapter;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String DEBUG_TAG="SearchActivity";
    Spinner selectionSpinner;

    EditText nameText;
    EditText startDate;
    EditText endDate;

    TextView selectText;
    TextView sDateText;
    TextView eDateText;
    TextView resultText;
    RecyclerView recyclerView;
    Spinner subSelectionSpinner;
    ImageButton checkButton;
    ProgressDialog progressDialog;

    ArrayList<EventClass> eventList;
    DatabaseReference db;
    EventAdapter eventAdapter;
    ValueEventListener valueEventListener;

    String selectCriteria;
    Calendar startDateCal;
    int datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Events");

        selectionSpinner = findViewById(R.id.searchSpinner);
        subSelectionSpinner = findViewById(R.id.selectSpinner);
        checkButton=findViewById(R.id.check_button);

        nameText=findViewById(R.id.nameInput);
        selectText=findViewById(R.id.selecttv);
        resultText=findViewById(R.id.resulttv);
        recyclerView=findViewById(R.id.searchRecyclerView);

        sDateText=findViewById(R.id.sdatetext);
        eDateText=findViewById(R.id.edatetext);
        startDate = findViewById(R.id.sdateedit);
        startDate.setInputType(InputType.TYPE_NULL);
        endDate = findViewById(R.id.edateedit);
        endDate.setInputType(InputType.TYPE_NULL);

        eventList=new ArrayList<>();
    }

    public void searchSelection(View view) {
        String searchSelection=selectionSpinner.getSelectedItem().toString();
        if(searchSelection.equals("Name")){
            this.searchByNameOptions();
        }else if(searchSelection.equals("Date")){
            this.searchByDateOptions();
        }else{
            this.searchByOptions(searchSelection);
        }
    }

    private void searchByNameOptions() {
        selectText.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.VISIBLE);

        subSelectionSpinner.setVisibility(View.GONE);
        checkButton.setVisibility(View.GONE);
        sDateText.setVisibility(View.GONE);
        startDate.setVisibility(View.GONE);
        eDateText.setVisibility(View.GONE);
        endDate.setVisibility(View.GONE);
    }

    private void searchByDateOptions() {
        selectText.setVisibility(View.VISIBLE);
        sDateText.setVisibility(View.VISIBLE);
        startDate.setVisibility(View.VISIBLE);
        eDateText.setVisibility(View.VISIBLE);
        endDate.setVisibility(View.VISIBLE);

        nameText.setVisibility(View.GONE);
        subSelectionSpinner.setVisibility(View.GONE);
        checkButton.setVisibility(View.GONE);

    }

    private void searchByOptions(String string) {
        selectText.setVisibility(View.VISIBLE);
        subSelectionSpinner.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.VISIBLE);


        nameText.setVisibility(View.GONE);
        sDateText.setVisibility(View.GONE);
        startDate.setVisibility(View.GONE);
        eDateText.setVisibility(View.GONE);
        endDate.setVisibility(View.GONE);
        String[] options=null;
        switch(string){
            case "Department":
                options =getResources().getStringArray(R.array.Department);
                selectCriteria="DEPT";
                break;
            case "Location":
                options =getResources().getStringArray(R.array.Location);
                selectCriteria="LOC";
                break;
            case "Category":
                options =getResources().getStringArray(R.array.EventType);
                selectCriteria="CAT";
                break;
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.spinner_layout,R.id.spinnerText, options);
        subSelectionSpinner.setAdapter(adapter);
        subSelectionSpinner.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.GONE);
    }

    public void searchByName(View view) {
        String titleStr = nameText.getText().toString();
        if(TextUtils.isEmpty(titleStr.trim())){
            nameText.setError("Should not be empty");
            return;
        }
        resultText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        this.searchInDB("NAME",titleStr,this);
    }

    public void spinSelection(View view) {
        String spinSelection=subSelectionSpinner.getSelectedItem().toString().trim();
        if(spinSelection.contains("Select")){
            Toast.makeText(this,"You need to select one item",Toast.LENGTH_SHORT).show();
            return;
        }
        resultText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        this.searchInDB(selectCriteria,spinSelection,this);
    }

    public void setStartDate(View view) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePicker = 0;
        new DatePickerDialog(this, this, year, month, day).show();
    }

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
            resultText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            this.searchInDBByDate(startDateCal.getTimeInMillis(),c.getTimeInMillis(),this);
        }
    }

    private void searchInDBByDate(final long start, final long end,final Context context) {
        hideKeyboard();
        db = FireBaseUtilClass.getDatabaseReference().child("Events");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        if(valueEventListener != null){
            db.removeEventListener(valueEventListener);
        }
        valueEventListener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventClass eventClass = dataSnapshot1.getValue(EventClass.class);
                    if(eventClass.getStartTimestamp() >= start && eventClass.getStartTimestamp() <= end){
                        eventList.add(eventClass);
                    }
                }
                progressDialog.hide();
                eventAdapter = new EventAdapter(eventList,context);
                recyclerView.setAdapter(eventAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                //showKeybord();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Sorry Something went wrong ",Toast.LENGTH_SHORT).show();
            }
        };
        db.orderByChild("startTimestamp").addValueEventListener(valueEventListener);
    }

    private void searchInDB(final String criteria, final String searchString,final Context context) {
        if(criteria.equals("NAME")){
            hideKeyboard();
        }
        db = FireBaseUtilClass.getDatabaseReference().child("Events");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        if(valueEventListener != null){
            db.removeEventListener(valueEventListener);
        }
        valueEventListener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    EventClass eventClass = dataSnapshot1.getValue(EventClass.class);
                    if(criteria.equals("NAME")){
                        if(eventClass.getEventTitle().toLowerCase().contains(searchString.toLowerCase())){
                            eventList.add(eventClass);
                        }
                    }else if(criteria.equals("DEPT")){
                        if(eventClass.getDepartment().equals(searchString)){
                            eventList.add(eventClass);
                        }
                    }else if(criteria.equals("LOC")){
                        if(eventClass.getEventLocation().equals(searchString)){
                            eventList.add(eventClass);
                        }
                    }else if(criteria.equals("CAT")){
                        if(eventClass.getEventType().equals(searchString)){
                            eventList.add(eventClass);
                        }
                    }
                }
                progressDialog.hide();
                eventAdapter = new EventAdapter(eventList,context);
                recyclerView.setAdapter(eventAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                if(criteria.equals("NAME")){
                    showKeybord();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Sorry Something went wrong ",Toast.LENGTH_SHORT).show();
            }
        };
        db.addValueEventListener(valueEventListener);
    }

    private void showKeybord() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    showSoftInput(view , InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



}
