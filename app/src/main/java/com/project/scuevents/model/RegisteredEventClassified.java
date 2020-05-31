package com.project.scuevents.model;

import java.util.ArrayList;

public class RegisteredEventClassified {
    private String classifiedTitle;
    private ArrayList<EventClass> eventClassList;

    public RegisteredEventClassified(String classifiedTitle, ArrayList<EventClass> eventClassList) {
        this.classifiedTitle = classifiedTitle;
        this.eventClassList = eventClassList;
    }

    public String getClassifiedTitle() {
        return classifiedTitle;
    }

    public void setClassifiedTitle(String classifiedTitle) {
        this.classifiedTitle = classifiedTitle;
    }

    public ArrayList<EventClass> getEventClassList() {
        return eventClassList;
    }

    public void setEventClassList(ArrayList<EventClass> eventClassList) {
        this.eventClassList = eventClassList;
    }
}
