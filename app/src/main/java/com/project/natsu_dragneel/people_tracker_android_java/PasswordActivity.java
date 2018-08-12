package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends Activity {

    String email;
    EditText e5_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        e5_password=(EditText)findViewById(R.id.editText_password_signup);
        Intent myIntent=getIntent();
        if(myIntent!=null){
            email=myIntent.getStringExtra("Email");
        }
    }

    public void goToNamePickActivity(View v){
        if(e5_password.getText().toString().length()>8){
            Intent myIntent=new Intent(PasswordActivity.this,NameActivity.class);
            myIntent.putExtra("Email",email);
            myIntent.putExtra("Password",e5_password.getText().toString());
            startActivity(myIntent);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(),"Password must be more than 8 characters long",Toast.LENGTH_LONG).show();
        }
    }

}
