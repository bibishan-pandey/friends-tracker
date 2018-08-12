package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToSignIn(View v){
        Intent myIntent=new Intent(MainActivity.this,SigninActivity.class);
        startActivity(myIntent);
    }

    public void goToSignUp(View v){
        Intent myIntent=new Intent(MainActivity.this,SignupActivity.class);
        startActivity(myIntent);
    }
}
