package com.example.rideshareapp;

import androidx.annotation.NonNull;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Route {
    private int id;

    private String beginpunt;

    private String eindpunt;

    private int maxPersonen;

    private Timestamp eindtijd;

    private Timestamp vertrektijd;

    private Profiel bestuurder;

    private Date eindtijdDate;
    private Date vertrektijdDate;
    private String tussenstops;

    public int getId() {
        return id;
    }

    public void setProfiel(){
        bestuurder=null;
    };

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getEindtijd() {
        return eindtijd;
    }

    public void setEindtijd(Timestamp eindtijd) {
        this.eindtijd = eindtijd;
    }
    public void setEindtijd(Date eindtijd) {
        this.eindtijd = new Timestamp(eindtijd.getTime());
    }
    public String eindtijdToString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(eindtijd);
    }

    public Timestamp getVertrektijd() {
        return vertrektijd;
    }

    public void setVertrektijd(Timestamp vertrektijd) {
        this.vertrektijd = vertrektijd;
    }
    public void setVertrektijd(Date vertrektijd) {
        this.vertrektijd = new Timestamp(vertrektijd.getTime());
    }
    public String vertrektijdToString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(vertrektijd);
    }

    public Profiel getBestuurder() {
        return bestuurder;
    }

    public void setBestuurder(Profiel bestuurder) {
        this.bestuurder = bestuurder;
    }

    public String getBeginpunt() {
        return beginpunt;
    }

    public void setBeginpunt(String beginpunt) {
        this.beginpunt = beginpunt;
    }

    public String getEindpunt() {
        return eindpunt;
    }

    public void setEindpunt(String eindpunt) {
        this.eindpunt = eindpunt;
    }

    public int getMaxPersonen() {
        return maxPersonen;
    }

    public void setMaxPersonen(int maxPersonen) {
        this.maxPersonen = maxPersonen;
    }

    public Route() {
    }

    @NonNull
    @Override
    public String toString(){
        return "beginpunt: "+beginpunt +"/n " +
                "eindpunt: "+eindpunt+"/n" +
                "personen: "+maxPersonen+"/n" +
                "vertrektijd: "+vertrektijdToString()+"/n" +
                "eindtijd: "+eindtijdToString();

    }

    public Route(Timestamp eindtijd, Timestamp vertrektijd, Profiel bestuurder) {
        this.eindtijd=eindtijd;
        this.vertrektijd=vertrektijd;
        this.bestuurder=bestuurder;
    }

    public Date getEindtijdDate() {
        return eindtijdDate;
    }

    public void setEindtijdDate(Date eindtijdDate) {
        this.eindtijdDate = eindtijdDate;
        this.eindtijd= new Timestamp(eindtijdDate.getTime());
    }

    public Date getVertrektijdDate() {
        return vertrektijdDate;
    }

    public void setVertrektijdDate(Date vertrektijdDate) {
        this.vertrektijdDate = vertrektijdDate;
        this.vertrektijd= new Timestamp(vertrektijdDate.getTime());
    }

    public String getTussenstops() {
        return tussenstops;
    }

    public void setTussenstops(String tussenstops) {
        this.tussenstops = tussenstops;
    }
}
