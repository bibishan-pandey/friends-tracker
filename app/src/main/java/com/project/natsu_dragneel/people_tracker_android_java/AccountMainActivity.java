package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

public class AccountMainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    PermissionManager manager;
    final String permission_enabled="Permissions enabled";

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
            manager=new PermissionManager() {
            };
            manager.checkAndRequestPermissions(this);
        }
        else{
            Intent myIntent=new Intent(AccountMainActivity.this,UserLocationMainActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode,permissions,grantResults);
        ArrayList<String> denied_permissions=manager.getStatus().get(0).denied;
        if(denied_permissions.isEmpty()){
            Toast.makeText(getApplicationContext(),permission_enabled,Toast.LENGTH_LONG).show();
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
