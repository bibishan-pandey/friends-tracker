package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.adapters.FollowedMembersAdapter;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

import java.util.ArrayList;

public class FollowedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter recycleradapter;
    RecyclerView.LayoutManager layoutManager;

    FirebaseAuth auth;
    DatabaseReference joinedReference;
    ArrayList<CreateUser> myList;
    FirebaseUser user;
    DatabaseReference usersReference;
    CreateUser createUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewFollowed);
        layoutManager = new LinearLayoutManager(this);
        myList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        joinedReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Followed");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        joinedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        String memberUserid = dss.child("FollowMemberID").getValue(String.class);
                        usersReference.child(memberUserid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                createUser = dataSnapshot.getValue(CreateUser.class);
                                //  Toast.makeText(getApplicationContext(),createUser.name,Toast.LENGTH_SHORT).show();
                                myList.add(createUser);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    Toast.makeText(getApplicationContext(), "Showing followed users", Toast.LENGTH_SHORT).show();
                    recycleradapter = new FollowedMembersAdapter(myList, getApplicationContext());
                    recyclerView.setAdapter(recycleradapter);
                    recycleradapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "You have not followed any user yet!", Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
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