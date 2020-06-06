package com.project.scuevents.model;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class EventClass implements Serializable {
    private String eventID;
    private String eventTitle;
    private String eventDescription;
    private String hostName;
    private String hostID;
    private String hostToken;
    private String eventDate;
    private String eventTime;
    private String endDate;
    private String endTime;
    private String eventLocation;
    private String eventType;
    private String department;
    private String imageUrl;
    private int totalSeats;
    private int availableSeats;
    private long startTimestamp;
//    private ArrayList regusers;

    public EventClass() {
    }

    public EventClass(String eventId, String eventTitle, String eventDescription, String hostName, String hostID, String hostToken,String eventDate, String eventTime,
               String endDate, String endTime, String eventLocation, String eventType, String department, int totalSeats, int availableSeats, long startTimestamp) {
        this.eventID = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.hostName = hostName;
        this.hostID = hostID;
        this.hostToken=hostToken;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.eventLocation = eventLocation;
        this.eventType = eventType;
        this.department = department;
        this.totalSeats = totalSeats;
        this.availableSeats=availableSeats;
        this.startTimestamp=startTimestamp;
    }

//    public EventClass(String eventId, String eventTitle, String eventDescription, String hostName, String hostID, String hostToken, String eventDate, String eventTime,
//                      String endDate, String endTime, String eventLocation, String eventType, String department, int totalSeats, int availableSeats, long startTimestamp, ArrayList regusers) {
//        this.eventID = eventId;
//        this.eventTitle = eventTitle;
//        this.eventDescription = eventDescription;
//        this.hostName = hostName;
//        this.hostID = hostID;
//        this.hostToken=hostToken;
//        this.eventDate = eventDate;
//        this.eventTime = eventTime;
//        this.endDate = endDate;
//        this.endTime = endTime;
//        this.eventLocation = eventLocation;
//        this.eventType = eventType;
//        this.department = department;
//        this.totalSeats = totalSeats;
//        this.availableSeats=availableSeats;
//        this.startTimestamp=startTimestamp;
//        this.regusers=regusers;
//    }

    public EventClass(String eventId, String eventTitle, String eventDescription,String hostName, String hostID, String hostToken,String eventDate, String eventTime,
               String endDate, String endTime, String eventLocation, String eventType, String department, String imageUrl,int totalSeats, int availableSeats,long startTimestamp) {
        this.eventID = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.hostName = hostName;
        this.hostID = hostID;
        this.hostToken=hostToken;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.eventLocation = eventLocation;
        this.eventType = eventType;
        this.department = department;
        this.imageUrl = imageUrl;
        this.totalSeats = totalSeats;
        this.availableSeats=availableSeats;
        this.startTimestamp=startTimestamp;
    }
    public EventClass(String eventId, String eventTitle){
        this.eventID = eventId;
        this.eventTitle = eventTitle;
    }
    public String getEventID() {
        return eventID;
    }

    public String getEventTitle() {
        return eventTitle;
    }
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }
    public void setEventDescription(String description) {
        this.eventDescription = description;
    }

    public String getHostName() {
        return hostName;
    }
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostID() {
        return hostID;
    }
    public String getHostToken(){return hostToken;}

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats(){return availableSeats;}
    public void setAvailableSeats(int availableSeats){this.availableSeats = availableSeats;}

    public long getStartTimestamp(){return startTimestamp;}
    public void setStartTimestamp(long timestamp){this.startTimestamp = timestamp;}

    //public ArrayList getRegusers(){return regusers;}
   // public void setRegusers(ArrayList regusers){this.regusers=regusers;}

}
