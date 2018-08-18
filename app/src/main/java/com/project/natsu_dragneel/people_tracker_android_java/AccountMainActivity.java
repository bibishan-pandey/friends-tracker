package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;
import com.project.natsu_dragneel.people_tracker_android_java.activities.SigninActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.SignupEmailActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.UserLocationMainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.helpers.ProgressDialogHelper;

import java.util.ArrayList;

public class AccountMainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    PermissionManager manager;
    final static String permission_enabled="Permissions enabled";
    final static String please_wait="Please wait...";
    final static String successful="Sign in successful";
    final static String email_error="Email is not verified";
    ProgressDialogHelper p_helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        sign_in_initializer();
    }

    private void initialize(){
        p_helper=new ProgressDialogHelper();
        p_helper.build_dialog(this);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }

    private void sign_in_initializer() {
        if(user==null){
            setContentView(R.layout.activity_account_main);
            //manager=new PermissionManager() {};
            //manager.checkAndRequestPermissions(this);
        }
        else{
            p_helper.show_dialog(please_wait);
            check_user_signed_or_not();
            p_helper.dismiss_dialog();
        }
    }

    private void check_user_signed_or_not() {
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
    }

    public void sign_in(View v){
        Intent myIntent=new Intent(AccountMainActivity.this,SigninActivity.class);
        startActivity(myIntent);
        finish();
    }

    public void sign_up(View v){
        Intent myIntent=new Intent(AccountMainActivity.this,SignupEmailActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode,permissions,grantResults);
        ArrayList<String> denied_permissions=manager.getStatus().get(0).denied;
        if(denied_permissions.isEmpty()){
            Toast.makeText(getApplicationContext(),permission_enabled,Toast.LENGTH_LONG).show();
        }
    }
}
