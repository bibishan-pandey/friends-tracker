package com.project.natsu_dragneel.people_tracker_android_java.classes;

public class CreateUser
{
    public String Name;
    public String Email;
    public String Password;
    public String Date;
    public String FollowCode;
    public String UserId;
    public String isSharing;
    public String lat;
    public String lng;
    public String profile_image;

    public CreateUser()
    {}

    public CreateUser(String Name, String Email, String Password, String Date, String FollowCode, String UserId, String isSharing, String lat, String lng,String profile_image) {
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
        this.Date = Date;
        this.FollowCode = FollowCode;
        this.UserId = UserId;
        this.isSharing = isSharing;
        this.lat = lat;
        this.lng = lng;
        this.profile_image = profile_image;
    }
}
