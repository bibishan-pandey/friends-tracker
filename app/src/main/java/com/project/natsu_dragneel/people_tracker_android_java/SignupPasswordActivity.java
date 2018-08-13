package com.project.natsu_dragneel.people_tracker_android_java;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignupPasswordActivity extends Activity {

    String email;
    final String password_length="Password must be at least 8 characters long";
    final String please_wait="Please wait...";
    EditText editText_password_signup;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_password);
        interface_builder();
    }

    public void interface_builder(){
        editText_password_signup=(EditText)findViewById(R.id.editText_password_signup);
        dialog = new ProgressDialog(this);
        Intent intent=getIntent();
        if(intent!=null){
            email=intent.getStringExtra("Email");
        }
    }

    public void password_to_name(View v){
        if(editText_password_signup.getText().toString().length()>7){
            dialog.setMessage(please_wait);
            dialog.show();
            Intent intent=new Intent(SignupPasswordActivity.this,SignupNameActivity.class);
            intent.putExtra("Email",email);
            intent.putExtra("Password",editText_password_signup.getText().toString());
            dialog.dismiss();
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(),password_length,Toast.LENGTH_LONG).show();
        }
    }

}
