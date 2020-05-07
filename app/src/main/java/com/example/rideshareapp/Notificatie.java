package com.example.rideshareapp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notificatie {


    private int id;

    private String type;


    private Profiel profiel;


    private Rit rit;


    private Route route;


    private Timestamp tijdstip;


    private Date tijdstipDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Profiel getProfiel() {
        return profiel;
    }

    public void setProfiel(Profiel profiel) {
        this.profiel = profiel;
    }

    public Rit getRit() {
        return rit;
    }

    public void setRit(Rit rit) {
        this.rit = rit;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Timestamp getTijdstip() {
        return tijdstip;
    }

    public void setTijdstip(Timestamp timestamp) {
        this.tijdstip = timestamp;
    }
    public void setTijdstipDat(Date tijdstipDate) {
        this.tijdstipDate = tijdstipDate;
        this.tijdstip= new Timestamp(tijdstipDate.getTime());
    }

    public String tijdstipToString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(tijdstip);
    }
    public boolean heeftRoute() {
        if(route!=null)return true;
        else return false;
    }
    public boolean heeftRit() {
        if(rit!=null)return true;
        else return false;
    }
    public boolean isRouteHerrinering() {
        if(type.equals("routeHerinnering"))return true;
        else return false;
    }
    public boolean isRitHerrinering() {
        if(type.equals("ritHerinnering"))return true;
        else return false;
    }

    public String getMessage() {
        String message;
        switch(type) {
            case "betaling":
                message="You have been payed for a ride";
                break;
            case "review":
                message="Someone has left a review on your profile";
                break;
            case "routeHerinnering":
                message="You have an upcoming ride as driver";
                break;
            case "ritHerinnering":
                message="You have an upcoming ride as passenger";
                break;
            case "ritAccepted":
                message="A ride you have booked has been accepted";
                break;
            case "ritChange":
                message="A ride you have requested has been changed";
                break;
            default:
                message=null;
        }
        return message;
    }

    public Notificatie(String type) {
        this.type=type;
        Date date = new Date();
        tijdstip=new Timestamp(date.getTime());
    }
    public Notificatie() {

    }

}
