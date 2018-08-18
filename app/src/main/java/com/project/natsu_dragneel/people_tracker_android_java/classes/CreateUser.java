package com.project.natsu_dragneel.people_tracker_android_java.classes;

public class CreateUser {

    public String name;
    public String email;
    public String password;
    public String date;
    public String circlecode;
    public String userid;
    public String isSharing;
    public String lat;
    public String lng;
    public String profile_image;


    public CreateUser(){}

    public CreateUser(String name, String email, String password, String date, String circlecode, String userid, String isSharing, String lat, String lng, String profile_image) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.date = date;
        this.circlecode = circlecode;
        this.userid = userid;
        this.isSharing = isSharing;
        this.lat = lat;
        this.lng = lng;
        this.profile_image = profile_image;
    }
}
