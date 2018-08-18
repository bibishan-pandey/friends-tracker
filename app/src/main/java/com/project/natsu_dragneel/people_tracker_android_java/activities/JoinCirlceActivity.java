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

public class JoinCirlceActivity extends AppCompatActivity {

    Toolbar toolbar;
    Pinview pinView;
    DatabaseReference reference;
    DatabaseReference current_reference;
    FirebaseUser user;
    FirebaseAuth auth;

    DatabaseReference circle_reference, joined_reference;

    String current_user_id, join_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);

        toolbar=(Toolbar)findViewById(R.id.joincircle_toolbar);
        toolbar.setTitle("Join a circle");

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        pinView=(Pinview)findViewById(R.id.pinview);

        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        current_reference=FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        current_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user_id=dataSnapshot.child("userid").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getCode(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void button_join_circle(View v){
        current_user_id=user.getUid();
        Query query=reference.orderByChild("circlecode").equalTo(pinView.getValue());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    CreateUser createUser = null;
                    for (DataSnapshot childDss : dataSnapshot.getChildren()) {
                        createUser = childDss.getValue(CreateUser.class);
                    }
                    join_user_id = createUser.userid;
                    circle_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(join_user_id).child("CircleMembers");
                    joined_reference=FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("JoinedCircles");

                    CircleJoin circleJoinCurrentID = new CircleJoin(current_user_id);
                    final CircleJoin circleJoinJoinID = new CircleJoin(join_user_id);

                    circle_reference.child(user.getUid()).setValue(circleJoinCurrentID)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        joined_reference.child(join_user_id).setValue(circleJoinJoinID)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(), "User joined circle successfully", Toast.LENGTH_LONG).show();
                                                        finish();
                                                        Intent intent= new Intent(JoinCirlceActivity.this,UserLocationMainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });

                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Couldnot join the circle", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
