package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.project.natsu_dragneel.people_tracker_android_java.R;

public class SignupPasswordActivity extends AppCompatActivity {

    EditText signup_password_edittext;
    Toolbar toolbar;
    Button signup_password_button;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_password);
        signup_password_edittext = (EditText) findViewById(R.id.signup_password_edittext);
        toolbar = (Toolbar) findViewById(R.id.toolbarPassword);
        signup_password_button = (Button) findViewById(R.id.signup_password_button);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("Email");
        }

        signup_password_button.setEnabled(false);
        signup_password_button.setBackgroundColor(Color.parseColor("#faebd7"));
        toolbar.setTitle("Password");
        setSupportActionBar(toolbar);


        signup_password_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 6) {
                    signup_password_button.setEnabled(true);
                    signup_password_button.setBackground(getResources().getDrawable(R.drawable.button_shape_normal));
                } else {
                    signup_password_button.setEnabled(false);
                    signup_password_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });

    }

    public void goToNameActivity(View v) {
        if (signup_password_edittext.getText().toString().length() >= 6) {
            // go to Name Activity
            Intent myIntent = new Intent(SignupPasswordActivity.this, SignupNameActivity.class);
            myIntent.putExtra("Email", email);
            myIntent.putExtra("Password", signup_password_edittext.getText().toString());
            startActivity(myIntent);
            finish();
        }
    }
}
