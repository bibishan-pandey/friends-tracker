package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.natsu_dragneel.people_tracker_android_java.AccountMainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

public class SignupInviteCodeActivity extends AppCompatActivity {

    String name,email,password,date,isSharing,code,userId;
    final String please_wait="Please wait...";
    final String email_verification="Verification email sent";
    final String registration_failure="Registration failure";
    final String registration_error="Registration error";

    Uri imageURI;
    ProgressDialog dialog;

    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference reference;

    TextView textView_circle_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_invite_code);
        interface_builder();
    }

    public void interface_builder(){
        textView_circle_code = (TextView)findViewById(R.id.textView_circle_code);
        auth = FirebaseAuth
                .getInstance();
        dialog = new ProgressDialog(this);

        Intent intent = getIntent();

        reference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users");

        storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("User_Images");

        if(intent != null){
            name = intent.getStringExtra("Name");
            email = intent.getStringExtra("Email");
            password = intent.getStringExtra("Password");
            date = intent.getStringExtra("Date");
            isSharing = intent.getStringExtra("isSharing");
            code = intent.getStringExtra("Code");
            imageURI = intent.getParcelableExtra("ImageURI");
        }
        //left
        if(code==null){

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
        user = auth.getCurrentUser();
        CreateUser createUser = new CreateUser(name,email,password,date,code,"false","n/a","n/a","n/a",user.getUid());
        userId = user.getUid();
        reference
                .child(userId)
                .setValue(createUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            StorageReference sr=storageReference.child(userId+".jpg");
                            sr.putFile(imageURI)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                String download_image_path=task.getResult().getStorage().getDownloadUrl().toString();
                                                reference.child(userId).child("imageURL").setValue(download_image_path)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    dialog.dismiss();
                                                                    send_verification_email();
                                                                    Intent intent=new Intent(SignupInviteCodeActivity.this,AccountMainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else{
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(),registration_error,Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),registration_failure,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void send_verification_email(){
        user.sendEmailVerification();
        Toast.makeText(getApplicationContext(),email_verification,Toast.LENGTH_LONG).show();
        if (user != null) {
            auth.signOut();
        }
    }
}
