package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountMainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interface_builder();
    }

    public void interface_builder(){
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null){
            setContentView(R.layout.activity_account_main);
        }
        else{
            Intent myIntent=new Intent(AccountMainActivity.this,NavigationActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    public void sign_in(View v){
        Intent myIntent=new Intent(AccountMainActivity.this,SigninActivity.class);
        startActivity(myIntent);
    }

    public void sign_up(View v){
        Intent myIntent=new Intent(AccountMainActivity.this,SignupEmailActivity.class);
        startActivity(myIntent);
    }
}
