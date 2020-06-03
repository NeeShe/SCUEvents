package com.project.scuevents.model;

import java.io.Serializable;

public class NotificationClass implements Serializable {
    private String body;
    private String eventId;
    private String eventName;
    private long timeStamp;
    private boolean view;

    public NotificationClass() {
    }

    public NotificationClass(String body, String eventId, String eventName, long timeStamp, boolean view) {
        this.body = body;
        this.eventId = eventId;
        this.eventName = eventName;
        this.timeStamp = timeStamp;
        this.view = view;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }
}
