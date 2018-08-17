package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.project.natsu_dragneel.people_tracker_android_java.AccountMainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;

public class SignupEmailActivity extends Activity {

    EditText editText_email_signup;
    final String email_exists="Email exists";
    final String email_required="Email is required";
    final String please_wait="Please wait...";
    String email;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //finish();
        setContentView(R.layout.activity_signup_email);
        interface_builder();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SignupEmailActivity.this,AccountMainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void interface_builder(){
        editText_email_signup=(EditText)findViewById(R.id.editText_email_signup);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
    }

    public void email_to_password(View v){
        if(editText_email_signup.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),email_required,Toast.LENGTH_LONG).show();
        }
        else{
            validate_email();
        }
    }

    public void validate_email(){
        dialog.setMessage(please_wait);
        dialog.show();
        auth.fetchSignInMethodsForEmail(editText_email_signup.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            boolean check_email_exists=!task.getResult().getSignInMethods().isEmpty();
                            check_email_exists(check_email_exists);
                        }
                    }
                });
    }

    public void check_email_exists(boolean check_email_exists){
        if(!check_email_exists){
            Intent intent=new Intent(SignupEmailActivity.this,SignupPasswordActivity.class);
            intent.putExtra("Email",editText_email_signup.getText().toString());
            startActivity(intent);
            finish();
        }
        else{
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),email_exists,Toast.LENGTH_LONG).show();
            Intent intent=new Intent(SignupEmailActivity.this,SigninActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
