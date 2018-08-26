package com.project.natsu_dragneel.people_tracker_android_java.activities.followers_activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.adapters.followers_adapter.FollowersAdapter;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

import java.util.ArrayList;

public class FollowersActivity extends AppCompatActivity {

    private static final String no_followers = "Sorry, no followers";
    private static final String show_followers = "Showing followers";

    private static final String TAG = FollowersActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private CreateUser createUser;
    private ArrayList<CreateUser> nameList;

    private DatabaseReference reference;
    private DatabaseReference usersReference;

    //private ArrayList<String> followersUserIdList;
    private String memberUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        recyclerView = findViewById(R.id.recyclerViewFollowers);
        layoutManager = new LinearLayoutManager(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        nameList = new ArrayList<>();
        //followersUserIdList = new ArrayList<>();
        usersReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(user.getUid())
                .child("FollowerMembers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        memberUserId = dss.child("MemberId").getValue(String.class);
                        usersReference.child(memberUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                createUser = dataSnapshot.getValue(CreateUser.class);
                                nameList.add(createUser);
                                recyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(), show_followers, Toast.LENGTH_SHORT).show();
                    recyclerAdapter = new FollowersAdapter(nameList, getApplicationContext());
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), no_followers, Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Cancelled");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void reload_followers_view(View v) {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void back_image_button(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
