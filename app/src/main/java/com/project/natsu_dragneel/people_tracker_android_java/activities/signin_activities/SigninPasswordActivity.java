package com.project.natsu_dragneel.people_tracker_android_java.activities.signin_activities;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities.CurrentLocationActivity;

public class SigninPasswordActivity extends AppCompatActivity {

    EditText signin_password_edittext;
    Button signin_password_next_button;
    FirebaseAuth auth;
    String email;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_password);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        signin_password_edittext = (EditText)findViewById(R.id.signup_profile_edittext);
        signin_password_next_button = (Button)findViewById(R.id.signin_nav_click);

        Intent intent = getIntent();
        if (intent!=null)
        {
            email = intent.getStringExtra("email_login");
        }

        signin_password_next_button.setEnabled(false);
        signin_password_next_button.setBackgroundColor(Color.parseColor("#faebd7"));

        signin_password_edittext.addTextChangedListener(new TextWatcher() {
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
                    signin_password_next_button.setEnabled(true);
                    signin_password_next_button.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                else
                {
                    signin_password_next_button.setEnabled(false);
                    signin_password_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void Login(View v)
    {
        dialog.setMessage("Please wait. Logging in.");
        dialog.show();
        if(signin_password_edittext.getText().toString().length()>=6)
        {
            auth.signInWithEmailAndPassword(email,signin_password_edittext.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseUser user = auth.getCurrentUser();
                                if(user.isEmailVerified())
                                {
                                    dialog.dismiss();
                                    finish();
                                    Intent myIntent = new Intent(SigninPasswordActivity.this,CurrentLocationActivity.class);
                                    startActivity(myIntent);
                                }
                                else
                                {
                                    dialog.dismiss();
                                    finish();
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(getApplicationContext(),"This email is not verified yet. Please check your email",Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(SigninPasswordActivity.this,MainActivity.class);
                                    startActivity(myIntent);
                                }
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Wrong username/password",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
