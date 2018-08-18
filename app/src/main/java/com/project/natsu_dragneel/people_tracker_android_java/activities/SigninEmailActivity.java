package com.project.natsu_dragneel.people_tracker_android_java.activities;

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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.project.natsu_dragneel.people_tracker_android_java.R;

public class SigninEmailActivity extends AppCompatActivity {

    EditText signin_email_edittext;
    Button sign_email_btn;
    ProgressDialog dialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_email);

        signin_email_edittext=(EditText)findViewById(R.id.signin_email_edittext);
        dialog=new ProgressDialog(this);

        auth=FirebaseAuth.getInstance();
        sign_email_btn=(Button)findViewById(R.id.signin_email_btn);
        sign_email_btn.setEnabled(false);
        sign_email_btn.setBackgroundColor(Color.parseColor("#faebd7"));
        final String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        signin_email_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(signin_email_edittext.getText().toString().matches(emailPattern)&& editable.length()>0){
                    sign_email_btn.setEnabled(true);
                    sign_email_btn.setBackgroundColor(Color.parseColor("#ffff4444"));
                }
                else{
                    sign_email_btn.setEnabled(false);
                    sign_email_btn.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void checkEmail(View v){
        dialog.setMessage("Please wait...");
        dialog.show();
        auth.fetchSignInMethodsForEmail(signin_email_edittext.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check=!task.getResult().getSignInMethods().isEmpty();
                        if(!check){
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"This email doesnot exist. Please create an account to sign in.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            dialog.dismiss();
                            Intent intent=new Intent(SigninEmailActivity.this,SigninPasswordActivity.class);
                            intent.putExtra("email_login",signin_email_edittext.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
