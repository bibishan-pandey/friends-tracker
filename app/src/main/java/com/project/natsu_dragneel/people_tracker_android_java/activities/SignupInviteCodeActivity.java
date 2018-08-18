package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.natsu_dragneel.people_tracker_android_java.AccountMainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

public class SignupInviteCodeActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView code_textview;
    TextView button_register;
    String name,email,password,date,isSharing;
    String code=null;

    DatabaseReference reference;
//    final String please_wait="Please wait...";
//    final String email_verification="Verification email sent";
//    final String registration_failure="Registration failure";
//    final String registration_error="Registration error";

    Uri resultUri;
    ProgressDialog dialog;

    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference firebaseStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_invite_code);
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Invite Code");
        dialog = new ProgressDialog(this);

        button_register=(TextView)findViewById(R.id.button_register);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        auth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseStorageReference=FirebaseStorage.getInstance().getReference().child("Profile_images");
        code_textview=(TextView)findViewById(R.id.code_textview);

        Intent intent=getIntent();
        if(intent!=null){
            name=intent.getStringExtra("name");
            email=intent.getStringExtra("email");
            password=intent.getStringExtra("password");
            date=intent.getStringExtra("date");
            isSharing=intent.getStringExtra("isSharing");
            code=intent.getStringExtra("code");
            resultUri=intent.getParcelableExtra("imageUri");
        }
        if(code==null){
            button_register.setVisibility(View.GONE);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user=auth.getCurrentUser();
                    String user_code=dataSnapshot.child(user.getUid()).child("circlecode").getValue().toString();
                    code_textview.setText(user_code);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            code_textview.setText(code);
        }
    }

    /*
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
    }*/

    public void sendCode(View v){
        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,"Hello, my circle code for the people tracker is "+code_textview.getText().toString()+".");
        startActivity(i.createChooser(i,"Share using:"));
    }

    public void register_user(View v){
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
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
        CreateUser createUser = new CreateUser(name,email,password,date,code,user.getUid(),"false","n/a","n/a","defaultimage");
        reference
                .child(user.getUid())
                .setValue(createUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            StorageReference filePath=firebaseStorageReference.child(user.getUid()+".jpg");
                            filePath.putFile(resultUri)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                String downloadPath=task.getResult().getStorage().getDownloadUrl().toString();
                                                reference.child(user.getUid()).child("profile_image").setValue(downloadPath)
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
                                                                    Toast.makeText(getApplicationContext(),"Error registering...",Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Could not upload user image",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Could not create an account",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void send_verification_email(){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Verification email sent",Toast.LENGTH_LONG).show();
                            auth.signOut();
                        }
                        else{
                            overridePendingTransition(0,0);
                            finish();
                            overridePendingTransition(0,0);
                            startActivity(getIntent());
                        }
                    }
                });
    }
}
