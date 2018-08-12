package com.project.natsu_dragneel.people_tracker_android_java;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignupActivity extends Activity {

    EditText e4;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        e4=(EditText)findViewById(R.id.editText_email_signup);
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
    }

    public void goToPasswordActivity(View v){
        //check if email is already registered
        dialog.setMessage("Checking email address");
        dialog.show();
        auth.fetchSignInMethodsForEmail(e4.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            boolean check_email_exists=!task.getResult().getSignInMethods().isEmpty();
                            if(!check_email_exists){
                                //email doesnot exists
                                Intent myIntent=new Intent(SignupActivity.this,PasswordActivity.class);
                                myIntent.putExtra("Email",e4.getText().toString());
                                startActivity(myIntent);
                                finish();
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"This Email is already regstered",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

}
