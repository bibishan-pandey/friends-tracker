package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

public class FollowActivity extends AppCompatActivity {

    Toolbar toolbar;
    Pinview follow_code_pinview;
    DatabaseReference reference, currentReference;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference circleReference, joinedReference;
    String joinUserId;
    String current_userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Follow a user");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        follow_code_pinview = (Pinview) findViewById(R.id.follow_code_pinview);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        currentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_userid = dataSnapshot.child("UserID").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void get_code_to_follow(View v) {
        current_userid = user.getUid();
        Query query = reference.orderByChild("FollowCode").equalTo(follow_code_pinview.getValue());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CreateUser createUser = null;
                    for (DataSnapshot childDss : dataSnapshot.getChildren()) {
                        createUser = childDss.getValue(CreateUser.class);
                    }
                    joinUserId = createUser.UserID;

                    circleReference = FirebaseDatabase.getInstance().getReference().child("Users").child(joinUserId).child("Followers");
                    joinedReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Followed");

                    CircleJoin circleJoin = new CircleJoin(current_userid);
                    final CircleJoin circleJoin1 = new CircleJoin(joinUserId);

                    circleReference.child(user.getUid()).setValue(circleJoin)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        joinedReference.child(joinUserId).setValue(circleJoin1)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(), "You have followed this user successfully.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        Intent myIntent = new Intent(FollowActivity.this, UserLocationMainActivity.class);
                                                        startActivity(myIntent);
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Could not follow, try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Code entered is invalid. Please enter valid code.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
