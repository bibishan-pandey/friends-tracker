package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities.CurrentLocationActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.signin_activities.SigninEmailActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.signup_activities.SignupEmailActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        if(user == null)
        {
            setContentView(R.layout.activity_main);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this,CurrentLocationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void signup_click(View v)
    {
        Intent intent = new Intent(MainActivity.this,SignupEmailActivity.class);
        startActivity(intent);
        finish();
    }

    public void signin_click(View v)
    {
        Intent intent = new Intent(MainActivity.this,SigninEmailActivity.class);
        startActivity(intent);
        finish();
    }
}
