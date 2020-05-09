package com.example.rideshareapp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Bericht {


    private int id;

    private String content;

    private Profiel zender;

    private Profiel ontvanger;

    private Timestamp timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Profiel getZender() {
        return zender;
    }

    public void setZender(Profiel zender) {
        this.zender = zender;
    }

    public Profiel getOntvanger() {
        return ontvanger;
    }

    public void setOntvanger(Profiel ontvanger) {
        this.ontvanger = ontvanger;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String tijdstipToString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);
    }
}
