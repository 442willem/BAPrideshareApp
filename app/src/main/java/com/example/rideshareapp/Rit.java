package com.example.rideshareapp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rit {

    private int id;

    private String beginpunt;

    private String eindpunt;

    private int prijs;

    private Timestamp vertrektijd;

    private int aantalPersonen;

    private boolean betaald;

    private boolean goedgekeurd;

    private Route route;

    private Profiel passagier;

    private Date vertrektijdDate;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }


    public Profiel getPassagier() {
        return passagier;
    }

    public void setPassagier(Profiel passagier) {
        this.passagier = passagier;
    }


    public int getPrijs() {
        return prijs;
    }

    public void setPrijs(int prijs) {
        this.prijs = prijs;
    }


    public Timestamp getVertrektijd() {
        return vertrektijd;
    }

    public void setVertrektijd(Timestamp vertrektijd) {
        System.out.println("SET VERTREKTIJD (timestamp)");
        this.vertrektijd = vertrektijd;
    }
    public void setVertrektijd(Date vertrektijd) {
        System.out.println("SET VERTREKTIJD (date)");
        this.vertrektijd = new Timestamp(vertrektijd.getTime());
    }

    public Date getVertrektijdDate() {
        return vertrektijdDate;
    }

    public void setVertrektijdDate(Date vertrektijdDate) {
        System.out.println("SET VERTREKTIJDDATE");
        this.vertrektijdDate = vertrektijdDate;
        this.vertrektijd= new Timestamp(vertrektijdDate.getTime());
    }

    public String vertrektijdToString() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(vertrektijd);
    }



    public int getAantalPersonen() {
        return aantalPersonen;
    }

    public void setAantalPersonen(int aantalPersonen) {
        this.aantalPersonen = aantalPersonen;
    }


    public boolean isBetaald() {
        return betaald;
    }

    public void setBetaald(boolean betaald) {
        this.betaald = betaald;
    }


    public boolean isGoedgekeurd() {
        return goedgekeurd;
    }

    public void setGoedgekeurd(boolean goedgekeurd) {
        this.goedgekeurd = goedgekeurd;
    }

    public Rit() {}

}
