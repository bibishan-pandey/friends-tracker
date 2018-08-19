package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.project.natsu_dragneel.people_tracker_android_java.R;

public class SigninEmailActivity extends AppCompatActivity {

    EditText signin_email_edittext;
    Toolbar toolbar;
    Button signin_email_button;
    ProgressDialog dialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_email);
        signin_email_edittext = (EditText)findViewById(R.id.signup_name_edittext);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Sign In");
        setSupportActionBar(toolbar);
        dialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        signin_email_button = (Button)findViewById(R.id.signin_password_button);
        signin_email_button.setEnabled(false);
        signin_email_button.setBackgroundColor(Color.parseColor("#faebd7"));
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        signin_email_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(signin_email_edittext.getText().toString().matches(emailPattern) && s.length() > 0)
                {
                    signin_email_button.setEnabled(true);
                    signin_email_button.setBackground(getResources().getDrawable(R.drawable.button_shape_normal));
                }
                else
                {
                    signin_email_button.setEnabled(false);
                    signin_email_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void checkEmail(View v)
    {
        dialog.setMessage("Please wait!");
        dialog.show();
        auth.fetchProvidersForEmail(signin_email_edittext.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        boolean check = !task.getResult().getProviders().isEmpty();
                        if(!check)
                        {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"This email does not exist. Please create an account first",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // go to password login
                            dialog.dismiss();
                            Intent myIntent = new Intent(SigninEmailActivity.this,SigninPasswordActivity.class);
                            myIntent.putExtra("EmailSignin",signin_email_edittext.getText().toString());
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
    }
}
