package com.example.rideshareapp;

public class Profiel {

    private int id;

    private int driverscore;
    private int passagierscore;

    private String voornaam;
    private String achternaam;
    private String login;
    private String password;
    private String paypalemail;
    private String group;

    public int getId() {
        return id;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverscore() {
        return driverscore;
    }

    public void setDriverscore(int driverscore) {
        this.driverscore = driverscore;
    }

    public int getPassagierscore() {
        return passagierscore;
    }

    public void setPassagierscore(int passagierscore) {
        this.passagierscore = passagierscore;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPaypalemail() {
        return paypalemail;
    }

    public void setPaypalemail(String paypalemail) {
        this.paypalemail = paypalemail;
    }

    public Profiel() {}

    public Profiel(String login, String password,String achternaam, String voornaam, String paypalemail) {
        this.achternaam=achternaam;
        this.login=login;
        this.password=password;
        this.voornaam=voornaam;
        this.paypalemail=paypalemail;
        this.group="Driver";
    }
}
