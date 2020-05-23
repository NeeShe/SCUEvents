package com.project.scuevents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.project.scuevents.model.EventClass;
import com.project.scuevents.model.FireBaseUtilClass;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static final String DEBUG_TAG="SearchActivity";
    Spinner selectionSpinner;
    EditText nameText;
    TextView resultText;
    RecyclerView recyclerView;
    ArrayList<EventClass> eventList;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Events");
        selectionSpinner = findViewById(R.id.searchSpinner);
        nameText=findViewById(R.id.nameInput);
        resultText=findViewById(R.id.resulttv);
        recyclerView=findViewById(R.id.searchRecyclerView);
    }

    public void searchSelection(View view) {
        String searchSelection=selectionSpinner.getSelectedItem().toString();
        Log.e(DEBUG_TAG,"Selected:"+searchSelection);
        if(searchSelection.equals("Name")){
            this.searchByNameOptions();
        }else if(searchSelection.equals("Date")){
            this.searchByDateOptions();
        }else if(searchSelection.equals("Location")){
            this.searchByLocationOptions();
        }else if(searchSelection.equals("Category")){
            this.searchByCategoryOptions();
        }else{
            this.searchByDepartmentOptions();
        }
    }

    private void searchByNameOptions() {
        nameText.setVisibility(View.VISIBLE);
    }

    private void searchByDepartmentOptions() {
    }

    private void searchByCategoryOptions() {
    }

    private void searchByLocationOptions() {
    }

    private void searchByDateOptions() {
    }


    public void searchByName(View view) {
        String titleStr = nameText.getText().toString();
        if(TextUtils.isEmpty(titleStr.trim())){
            nameText.setError("Should not be empty");
            return;
        }
        resultText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        this.searchByNameInDB(titleStr);
    }

    private void searchByNameInDB(String titleStr) {
        db = FireBaseUtilClass.getDatabaseReference().child("Events");
    }
}
