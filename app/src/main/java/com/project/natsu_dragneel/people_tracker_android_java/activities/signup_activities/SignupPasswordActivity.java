package com.project.natsu_dragneel.people_tracker_android_java.activities.signup_activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.security.SHA_Conversion;

import java.security.NoSuchAlgorithmException;

@SuppressWarnings("unused")
public class SignupPasswordActivity extends AppCompatActivity {

    private static final String TAG = SignupPasswordActivity.class.getSimpleName();

    private EditText signup_password_editText;
    private String signup_password_secure;
    private TextView hashed_textView;
    private Button signup_password_next_signup;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_password);
        signup_password_editText = findViewById(R.id.signup_password_edittext);
        hashed_textView=findViewById(R.id.hashed_text);
        signup_password_next_signup = findViewById(R.id.signup_password_next_signup);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("Email");
        }
        signup_password_next_signup.setEnabled(false);
        signup_password_next_signup.setBackgroundColor(Color.parseColor("#faebd7"));
        signup_password_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 6) {
                    signup_password_next_signup.setEnabled(true);
                    signup_password_next_signup.setBackgroundColor(Color.parseColor("#f05545"));
                } else {
                    signup_password_next_signup.setEnabled(false);
                    signup_password_next_signup.setBackgroundColor(Color.parseColor("#faebd7"));
                }
                try {
                    signup_password_secure = SHA_Conversion.hashPassword(signup_password_editText.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                hashed_textView.setText(signup_password_secure);
            }
        });
    }

    public void go_to_name_activity(View v) {
        if (signup_password_editText.getText().toString().length() >= 6) {
            Intent myIntent = new Intent(SignupPasswordActivity.this, SignupProfileActivity.class);
            myIntent.putExtra("Email", email);
            myIntent.putExtra("Password", hashed_textView.getText().toString());
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(SignupPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void back_image_button(View v) {
        finish();
        Intent intent = new Intent(SignupPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }
}