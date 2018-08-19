package com.project.natsu_dragneel.people_tracker_android_java.classes;

public class CreateUser
{
    public String Name;
    public String Email;
    public String Password;
    public String Date;
    public String Code;
    public String UserID;
    public String isSharing;
    public String Lat;
    public String Lng;
    public String ProfileImage;


    public CreateUser()
    {}

    public CreateUser(String Name, String Email, String Password, String Date, String Code, String UserID, String isSharing, String Lat, String Lng,String ProfileImage) {
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
        this.Date = Date;
        this.Code = Code;
        this.UserID = UserID;
        this.isSharing = isSharing;
        this.Lat = Lat;
        this.Lng = Lng;
        this.ProfileImage = ProfileImage;
    }
}
