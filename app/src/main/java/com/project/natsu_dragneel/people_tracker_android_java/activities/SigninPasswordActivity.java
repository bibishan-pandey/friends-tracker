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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.natsu_dragneel.people_tracker_android_java.AccountMainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;

public class SigninPasswordActivity extends AppCompatActivity {

    EditText signin_password_edittext;
    Button signin_password_btn;
    FirebaseAuth auth;
    FirebaseUser user;
    String email;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_password);

        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);

        signin_password_edittext=(EditText)findViewById(R.id.signin_password_edittext);
        signin_password_btn=(Button)findViewById(R.id.signin_password_btn);

        Intent intent=getIntent();
        if(intent!=null){
            email=intent.getStringExtra("email_login");
        }

        signin_password_btn.setEnabled(false);
        signin_password_btn.setBackgroundColor(Color.parseColor("#faebd7"));

        signin_password_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>7){
                    signin_password_btn.setEnabled(true);
                    signin_password_btn.setBackgroundColor(Color.parseColor("ffff4444"));
                }
                else{
                    signin_password_btn.setEnabled(false);
                    signin_password_btn.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void Signin(View v){
        dialog.setMessage("Please wait...");
        dialog.show();
        if(signin_password_edittext.getText().toString().length()>7){
            auth.signInWithEmailAndPassword(email,signin_password_edittext.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                user=auth.getCurrentUser();
                                if(user.isEmailVerified()){
                                    dialog.dismiss();
                                    finish();
                                    Intent intent=new Intent(SigninPasswordActivity.this,UserLocationMainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    dialog.dismiss();
                                    finish();
                                    auth.signOut();
                                    Toast.makeText(SigninPasswordActivity.this, "Email is not verified. Please verify your email address", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(SigninPasswordActivity.this, AccountMainActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(SigninPasswordActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
