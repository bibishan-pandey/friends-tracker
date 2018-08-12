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

import org.w3c.dom.Text;

public class InviteCodeActivity extends AppCompatActivity {
    String name,email,password,date,isSharing,code,userId;
    Uri imageURI;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        t1=(TextView)findViewById(R.id.textView_circle_code);
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        Intent myIntent=getIntent();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        if(myIntent!=null){
            name=myIntent.getStringExtra("Name");
            email=myIntent.getStringExtra("Email");
            password=myIntent.getStringExtra("Password");
            date=myIntent.getStringExtra("Date");
            isSharing=myIntent.getStringExtra("isSharing");
            code=myIntent.getStringExtra("Code");
            imageURI=myIntent.getParcelableExtra("ImageURI");
        }
        t1.setText(code);
    }

    public void registerUser(View v){
        dialog.setMessage("Please wait...");
        dialog.show();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            CreateUser createUser=new CreateUser(name,email,password,code,"false","n/a","n/a","n/a");
                            user=auth.getCurrentUser();
                            userId=user.getUid();
                            reference.child(userId).setValue(createUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Registration successful",Toast.LENGTH_LONG).show();
                                                finish();
                                                Intent myIntent=new Intent(InviteCodeActivity.this,NavigationActivity.class);
                                                startActivity(myIntent);
                                            }
                                            else{
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Registration failure",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
