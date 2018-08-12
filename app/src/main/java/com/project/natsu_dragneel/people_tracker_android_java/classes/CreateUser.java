package com.project.natsu_dragneel.people_tracker_android_java.classes;

public class CreateUser {

    public CreateUser(){

    }
    public String name;
    public String email;

    public CreateUser(String name, String email, String password, String code, String isSharing, String lat, String lng, String imageURL) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.code = code;
        this.isSharing = isSharing;
        this.lat = lat;
        this.lng = lng;
        this.imageURL = imageURL;
    }

    public String password;
    public String code;
    public String isSharing;
    public String lat;
    public String lng;
    public String imageURL;
}
