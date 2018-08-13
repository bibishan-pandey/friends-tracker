package com.project.natsu_dragneel.people_tracker_android_java;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

public class SignupInviteCodeActivity extends AppCompatActivity {

    String name,email,password,date,isSharing,code,userId;
    final String please_wait="Please wait...";
    final String registration_success="Registration successful";
    final String registration_failure="Registration failure";
    Uri imageURI;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    TextView textView_circle_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_invite_code);
        interface_builder();
    }

    public void interface_builder(){
        textView_circle_code=(TextView)findViewById(R.id.textView_circle_code);
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);

        Intent intent=getIntent();

        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        if(intent!=null){
            name=intent.getStringExtra("Name");
            email=intent.getStringExtra("Email");
            password=intent.getStringExtra("Password");
            date=intent.getStringExtra("Date");
            isSharing=intent.getStringExtra("isSharing");
            code=intent.getStringExtra("Code");
            imageURI=intent.getParcelableExtra("ImageURI");
        }

        textView_circle_code.setText(code);
    }

    public void register_user(View v){
        dialog.setMessage(please_wait);
        dialog.show();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            register_method();
                        }
                    }
                });
    }

    public void register_method(){
        CreateUser createUser=new CreateUser(name,email,password,code,"false","n/a","n/a","n/a");
        user=auth.getCurrentUser();
        userId=user.getUid();
        reference.child(userId).setValue(createUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),registration_success,Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(SignupInviteCodeActivity.this,UserLocationMainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),registration_failure,Toast.LENGTH_LONG).show();
                        }
                    }
                });
        finish();
    }
}
