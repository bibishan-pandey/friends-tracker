package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null){
            setContentView(R.layout.activity_main);
        }
        else{
            Intent myIntent=new Intent(MainActivity.this,NavigationActivity.class);
            startActivity(myIntent);
            finish();
        }


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
