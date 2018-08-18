package com.project.natsu_dragneel.people_tracker_android_java.classes;

public class AddCircle {
    public String name;
    public String isSharing;
    public String lat;
    public String lng;

    public AddCircle(String name, String isSharing, String lat, String lng, String imageURL) {
        this.name = name;
        this.isSharing = isSharing;
        this.lat = lat;
        this.lng = lng;
        this.imageURL = imageURL;
    }

    public AddCircle(){}

    public String imageURL;
}
