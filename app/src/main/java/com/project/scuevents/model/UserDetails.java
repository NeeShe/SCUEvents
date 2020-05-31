package com.project.scuevents.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UserDetails {
    private String fName;
    private String lName;
    private String email;
    private String userID;
    private String userToken;


    private Set<String> regEventIDSet;
    private Set<String> hostedEventIDSet;
    private String imageUri;

    public UserDetails(){

    }


    public UserDetails(String fName, String lName, String email, String userID) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.userID = userID;
    }

    public UserDetails(String fName, String lName, String email, String userID, String imageUri) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.userID = userID;
        this.imageUri = imageUri;
    }

    public UserDetails(String fName, String lName, String email, String userID,
                       String imageUri, HashSet<String> regEventIDSet, HashSet<String> hostedEventIDSet) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.userID = userID;
        this.imageUri = imageUri;
        this.regEventIDSet = regEventIDSet;
        this.hostedEventIDSet = hostedEventIDSet;
    }


    public UserDetails(String userID,String userToken) {
        this.userID = userID;
        this.userToken = userToken;
    }


    public String getfName() {
        return fName;
    }
    public String getuserToken(){return userToken;}

    public void setfName(String name) {
        this.fName = name;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String name) {
        this.lName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String uri) {
        this.imageUri = uri;
    }

    public Set<String> getRegEventsID() {
        return regEventIDSet;
    }

    public void setRegEventIDSet(HashSet<String> set) {
        this.regEventIDSet = set;
    }

    public Set<String> getHostedEventIDSet() {
        return hostedEventIDSet;
    }

    public void getHostedEventIDSet(HashSet<String> set) {
        this.hostedEventIDSet = set;
    }
}
