package com.project.natsu_dragneel.people_tracker_android_java.activities.signup_activities;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;

public class SignupPasswordActivity extends AppCompatActivity {

    EditText signup_password_edittext;
    Button signup_password_next_signup;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_password);
        signup_password_edittext = (EditText)findViewById(R.id.signup_password_edittext);
        signup_password_next_signup = (Button)findViewById(R.id.signup_password_next_signup);

        Intent intent = getIntent();
        if (intent!=null) {
            email = intent.getStringExtra("email");
        }
        signup_password_next_signup.setEnabled(false);
        signup_password_next_signup.setBackgroundColor(Color.parseColor("#faebd7"));
        signup_password_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>=6)
                {
                    signup_password_next_signup.setEnabled(true);
                    signup_password_next_signup.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                else
                {
                    signup_password_next_signup.setEnabled(false);
                    signup_password_next_signup.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void go_to_name_activity(View v)
    {
        if(signup_password_edittext.getText().toString().length()>=6)
        {
            // go to Name Activity
            Intent myIntent = new Intent(SignupPasswordActivity.this,SignupProfileActivity.class);
            myIntent.putExtra("email",email);
            myIntent.putExtra("password",signup_password_edittext.getText().toString());
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent=new Intent(SignupPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void back_image_button(View v){
        finish();
        Intent intent=new Intent(SignupPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
