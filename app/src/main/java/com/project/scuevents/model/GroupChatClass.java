package com.project.scuevents.model;

import android.util.Log;

import java.io.Serializable;

public class GroupChatClass implements Serializable {
    private String eventID;
    private String eventTitle;

    public GroupChatClass() {

    }
    public GroupChatClass(String eventID, String eventTitle){
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
