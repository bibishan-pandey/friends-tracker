package com.project.natsu_dragneel.people_tracker_android_java;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninActivity extends Activity {

    FirebaseAuth auth;
    EditText e1,e2;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        e1=(EditText)findViewById(R.id.editText_email_signin);
        e2=(EditText)findViewById(R.id.editText_password_signin);
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
    }

    public void signIn(View v){
        dialog.setMessage("Please wait while we are signing in");
        dialog.show();
        auth.signInWithEmailAndPassword(e1.getText().toString(),e2.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"User logged in successfully",Toast.LENGTH_LONG).show();
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Wrong email and password",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
