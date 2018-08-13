package com.project.natsu_dragneel.people_tracker_android_java;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

public class AccountMainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    PermissionManager manager;
    final String permission_enabled="Permissions enabled";
    final String please_wait="Please wait...";
    final String successful="Sign in successful";
    final String email_error="Email is not verified";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interface_builder();
    }

    public void interface_builder(){
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        dialog=new ProgressDialog(this);
        if(user==null){
            setContentView(R.layout.activity_account_main);
            manager=new PermissionManager() {
            };
            manager.checkAndRequestPermissions(this);
        }
        else{
            dialog.setMessage(please_wait);
            dialog.show();
            user=auth.getCurrentUser();
            if(user.isEmailVerified()) {
                Toast.makeText(getApplicationContext(), successful, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AccountMainActivity.this, UserLocationMainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),email_error,Toast.LENGTH_LONG).show();
                setContentView(R.layout.activity_account_main);
            }
            dialog.dismiss();
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
