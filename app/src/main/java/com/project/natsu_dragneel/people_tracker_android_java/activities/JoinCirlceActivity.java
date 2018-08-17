package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CircleJoin;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

public class JoinCirlceActivity extends AppCompatActivity {

    Pinview pinview;
    DatabaseReference reference;
    DatabaseReference current_reference;
    FirebaseUser user;
    FirebaseAuth auth;

    DatabaseReference circle_reference;

    String current_user_id, join_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        pinview=(Pinview)findViewById(R.id.pinview);

        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        current_reference=FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        current_user_id=user.getUid();
    }

    public void button_join_circle(View v){
        Query query=reference.orderByChild("code").equalTo(pinview.getValue());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    CreateUser createUser=null;
                    for(DataSnapshot childDss:dataSnapshot.getChildren()){
                        createUser=childDss.getValue(CreateUser.class);
                        join_user_id=createUser.userID;
                        circle_reference=FirebaseDatabase.getInstance().getReference().child("Users").child(join_user_id).child("CircleMembers");

                        CircleJoin circleJoinCurrentID=new CircleJoin(current_user_id);
                        CircleJoin circleJoinJoinID=new CircleJoin(join_user_id);

                        circle_reference.child(user.getUid()).setValue(circleJoinCurrentID)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"User joined circle successfully",Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Circle code is invalid",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Operation cancelled",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
