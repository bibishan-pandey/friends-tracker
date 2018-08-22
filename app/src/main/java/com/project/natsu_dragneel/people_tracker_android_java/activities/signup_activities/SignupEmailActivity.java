package com.project.natsu_dragneel.people_tracker_android_java.activities.signup_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.signin_activities.SigninEmailActivity;

public class SignupEmailActivity extends AppCompatActivity {

    EditText signup_email_edittext;
    Button signup_email_next_button;
    ProgressDialog dialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email);
        signup_email_edittext = (EditText)findViewById(R.id.signup_profile_edittext);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        signup_email_next_button = (Button)findViewById(R.id.signin_nav_click);
        signup_email_next_button.setEnabled(false);
        signup_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        signup_email_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(signup_email_edittext.getText().toString().matches(emailPattern) && s.length() > 0)
                {
                    signup_email_next_button.setEnabled(true);
                    signup_email_next_button.setBackgroundColor(Color.parseColor("#f05545"));
                }
                else
                {
                    signup_email_next_button.setEnabled(false);
                    signup_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void email_exist_check(View v)
    {
        dialog.setMessage("Please wait");
        dialog.show();

        auth.fetchProvidersForEmail(signup_email_edittext.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        dialog.dismiss();
                        boolean check = !task.getResult().getProviders().isEmpty();
                        if(!check)
                        {
                            Intent myIntent = new Intent(SignupEmailActivity.this,SignupPasswordActivity.class);
                            myIntent.putExtra("email",signup_email_edittext.getText().toString());
                            startActivity(myIntent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"You already have an account. Please signin.",Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(SignupEmailActivity.this,SigninEmailActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent=new Intent(SignupEmailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void back_image_button(View v){
        finish();
        Intent intent=new Intent(SignupEmailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
