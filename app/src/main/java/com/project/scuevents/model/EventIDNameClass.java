package com.project.scuevents.model;

import android.util.Log;

import java.io.Serializable;

public class EventIDNameClass implements Serializable {
    private String eventID;
    private String eventTitle;

    public EventIDNameClass() {

    }
    public EventIDNameClass(String eventID, String eventTitle){
        this.eventID = eventID;
        this.eventTitle = eventTitle;
    }
    public String getEventID() {
        return this.eventID;
    }
    public String getEventTitle() {
        return this.eventTitle;
    }
}
