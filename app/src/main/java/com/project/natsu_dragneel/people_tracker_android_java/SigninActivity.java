package com.project.natsu_dragneel.people_tracker_android_java;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends Activity {

    final String please_wait="Please wait...";
    final String successful="Sign in successful";
    final String invalid_input="Invalid input";
    final String field_required="Fields are empty";
    final String email_error="Email is not verified";

    FirebaseAuth auth;
    FirebaseUser user;
    EditText editText_email_signin;
    EditText editText_password_signin;
    ProgressDialog dialog;

    //private static final String TAG = "UserLocationMainActivity";
    //private  static final int ERROR_DIALOG_REQUEST=9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        interface_builder();
    }

    /*
    @SuppressLint("LongLogTag")
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SigninActivity.this);
        if(available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(SigninActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getApplicationContext(),"We cannot make map requests",Toast.LENGTH_LONG).show();
        }
        return false;
    }
    */


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SigninActivity.this,AccountMainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void interface_builder(){
        editText_email_signin=(EditText)findViewById(R.id.editText_email_signin);
        editText_password_signin=(EditText)findViewById(R.id.editText_password_signin);
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
    }

    public void sign_in_method(View v){
        if(editText_email_signin.getText().toString().isEmpty() || editText_password_signin.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),field_required,Toast.LENGTH_LONG).show();
        }
        else{
            //if(isServicesOK()){
                validate_email_password();
            //}
        }
    }

    public void validate_email_password(){
        dialog.setMessage(please_wait);
        dialog.show();
        auth.signInWithEmailAndPassword(editText_email_signin.getText().toString(),editText_password_signin.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();

                            user=auth.getCurrentUser();
                            if(user.isEmailVerified()){
                                Toast.makeText(getApplicationContext(),successful,Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(SigninActivity.this,UserLocationMainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),email_error,Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),invalid_input,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
