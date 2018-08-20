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

    EditText e1_pass;
    Button b1_password;
    FirebaseAuth auth;
    String email;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_password);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);


        e1_pass = (EditText)findViewById(R.id.editTextPass);
        b1_password = (Button)findViewById(R.id.button);

        Intent intent = getIntent();
        if (intent!=null)
        {
            email = intent.getStringExtra("email_login");
        }


        b1_password.setEnabled(false);
        b1_password.setBackgroundColor(Color.parseColor("#faebd7"));

        e1_pass.addTextChangedListener(new TextWatcher() {
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
                    b1_password.setEnabled(true);
                    b1_password.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                else
                {
                    b1_password.setEnabled(false);
                    b1_password.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void Login(View v)
    {
        dialog.setMessage("Please wait. Logging in.");
        dialog.show();
        if(e1_pass.getText().toString().length()>=6)
        {
            auth.signInWithEmailAndPassword(email,e1_pass.getText().toString())
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
