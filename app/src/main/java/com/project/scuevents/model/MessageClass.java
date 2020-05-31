package com.project.scuevents.model;

import java.io.Serializable;
import java.util.Date;

public class MessageClass implements Serializable {
    private String chatID;
    private String email;
    private String msg;
    private long msgTime;


    public MessageClass() {
    }

    public MessageClass(String chatID,String email, String msg, long msgTime) {
        this.chatID=chatID;
        this.email = email;
        this.msg = msg;
        if (msgTime > 0) {
            msgTime *= -1;
        }
        this.msgTime = msgTime;
    }

    public MessageClass(String chatID,String email, String msg) {
        this.email = email;
        this.msg = msg;
        msgTime = -new Date().getTime();
        this.chatID=chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getMsgTime() {
        return -msgTime;
    }

    public void setMsgTime(long msgTime) {
        if (msgTime > 0) {
            msgTime *= -1;
        }
        this.msgTime = msgTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof MessageClass))
            return false;
        MessageClass other = (MessageClass) obj;
        return chatID != null && chatID.equals(other.chatID);//Compare Id if null falseF
    }


}
