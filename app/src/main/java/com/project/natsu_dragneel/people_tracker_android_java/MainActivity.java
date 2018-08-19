package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            Intent myIntent = new Intent(MainActivity.this,MyNavigationTutorial.class);
            startActivity(myIntent);
            finish();

        }
    }


    public void getStarted_click(View v)
    {

        Intent myintent = new Intent(MainActivity.this,RegisterEmailActivity.class);
        startActivity(myintent);
        finish();

    }

    public void LoginUser(View v)
    {

        Intent myIntent = new Intent(MainActivity.this,LoginEmailActivity.class);
        startActivity(myIntent);
        finish();

    }




}
