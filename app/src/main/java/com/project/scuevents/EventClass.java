package com.project.scuevents;

import java.io.Serializable;
import java.util.Set;

public class EventClass implements Serializable {
    private String eventID;
    private String eventTitle;
    private String hostName;
    private String hostID;
    private String eventDay;
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private String eventType;
    private String department;
    private String imageUrl;
    private String videoUrl;
    private int totalSeats;
    private Set<String> attendeeSet;

    EventClass() {

    }

    EventClass(String eventId, String eventTitle, String hostName, String hostID, String eventDay, String eventDate, String eventTime,
               String eventLocation, String eventType, String department, int totalSeats) {
        this.eventID = eventId;
        this.eventTitle = eventTitle;
        this.hostName = hostName;
        this.hostID = hostID;
        this.eventDay = eventDay;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventType = eventType;
        this.department = department;
        this.totalSeats = totalSeats;
    }

    EventClass(String eventId, String eventTitle, String hostName, String hostID, String eventDay, String eventDate, String eventTime,
               String eventLocation, String eventType, String department, String imageUrl, String videoUrl,int totalSeats) {
        this.eventID = eventId;
        this.eventTitle = eventTitle;
        this.hostName = hostName;
        this.hostID = hostID;
        this.eventDay = eventDay;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventType = eventType;
        this.department = department;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.totalSeats = totalSeats;
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

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostID() {
        return hostID;
    }

    public String getEventDay() {
        return eventDay;
    }

    public void setEventDay(String day) {
        this.eventDay = day;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
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
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int TotalSeats) {
        this.totalSeats = totalSeats;
    }


    public Set<String> getAttendeeSet() {
        return attendeeSet;
    }

    public void setAttendeeSet(Set<String> attendeeSet) {
        this.attendeeSet = attendeeSet;
    }
}
