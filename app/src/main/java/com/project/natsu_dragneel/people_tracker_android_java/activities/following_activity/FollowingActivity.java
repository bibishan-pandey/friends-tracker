package com.project.natsu_dragneel.people_tracker_android_java.activities.following_activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.project.natsu_dragneel.people_tracker_android_java.adapters.following_adapter.FollowingAdapter;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class FollowingActivity extends AppCompatActivity {

    private static final String show_members = "Showing the members you are following";
    private static final String no_members = "Sorry, no following members";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;

    private ArrayList<CreateUser> usersList;
    private CreateUser createUser;

    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        usersList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewFollowing);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference followingReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(user.getUid())
                .child("FollowingMembers");
        usersReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        followingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {

                        String memberUserId = dss.child("MemberId").getValue(String.class);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            usersReference.child(Objects.requireNonNull(memberUserId))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    createUser = dataSnapshot.getValue(CreateUser.class);
                                    //Toast.makeText(getApplicationContext(),createUser.name,Toast.LENGTH_SHORT).show();
                                    usersList.add(createUser);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    Toast.makeText(getApplicationContext(), show_members, Toast.LENGTH_SHORT).show();
                    recyclerAdapter = new FollowingAdapter(usersList, getApplicationContext());
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), no_members, Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

    public void back_image_button(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}