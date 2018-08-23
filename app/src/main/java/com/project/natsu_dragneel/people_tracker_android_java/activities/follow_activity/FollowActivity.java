package com.project.natsu_dragneel.people_tracker_android_java.activities.follow_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities.CurrentLocationActivity;
import com.project.natsu_dragneel.people_tracker_android_java.classes.FollowClass;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

public class FollowActivity extends AppCompatActivity {
    
    Pinview code_pin_view;
    DatabaseReference reference,currentReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String currentUserId;
    DatabaseReference followersReference,followingReference;
    String followUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        code_pin_view = (Pinview)findViewById(R.id.code_pin_view);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        currentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserId = dataSnapshot.child("UserId").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void get_users_code_with_uid(View v)
    {
        currentUserId = user.getUid();
        Query query = reference.orderByChild("FollowCode").equalTo(code_pin_view.getValue());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    CreateUser createUser = null;
                    for(DataSnapshot childDss : dataSnapshot.getChildren())
                    {
                        createUser = childDss.getValue(CreateUser.class);
                    }
                    followUserId = createUser.UserId;
                    followersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(followUserId).child("FollowerMembers");
                    followingReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("FollowingMembers");
                    // get the correct values from the user
                    FollowClass currentFollowUser = new FollowClass(currentUserId);
                    final FollowClass followUser = new FollowClass(followUserId);
                    followersReference.child(user.getUid()).setValue(currentFollowUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        followingReference.child(followUserId).setValue(followUser)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(),"You started following this user successfully",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        Intent myIntent = new Intent(FollowActivity.this,CurrentLocationActivity.class);
                                                        startActivity(myIntent);
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Could not follow, try again",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invitation code entered is invalid",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void back_image_button(View v){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
