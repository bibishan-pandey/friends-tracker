package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.AccountMainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.squareup.picasso.Picasso;

public class UserLocationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback {

    String current_user_name;
    String current_user_email;
    //Uri current_user_imageURL;
    String current_user_imageURL;

    GoogleMap mMap;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    View header;
    TextView textView_title_name;
    TextView textView_title_email;

    double latitude,longitude;
    ImageView imageView_user_image;

    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "UserLocationMainActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);
        textView_title_name = header.findViewById(R.id.textView_title_name);
        textView_title_email = header.findViewById(R.id.textView_title_email);
        imageView_user_image = header.findViewById(R.id.imageView_user_image);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user_name = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                current_user_email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                //current_user_imageURL=user.getPhotoUrl();

                current_user_imageURL = dataSnapshot.child(user.getUid()).child("imageURL").getValue(String.class);
                String url= String.valueOf(current_user_imageURL);
                Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();
                Picasso.get().load(url).into(imageView_user_image);

                textView_title_name.setText(current_user_name);
                textView_title_email.setText(current_user_email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_location_main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_joinCircle) {
            Intent intent = new Intent(UserLocationMainActivity.this, JoinCirlceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_myCircle) {
            Intent intent=new Intent(UserLocationMainActivity.this,MyCircleActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_joinedCircle) {

        } else if (id == R.id.nav_inviteMembers) {

        } else if (id == R.id.nav_shareLocation) {
            String uri = "http://maps.google.com/maps?saddr="+latitude+","+longitude;
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String ShareSub = "Here is my location";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_signOut) {
            if (user != null) {
                auth.signOut();
                Intent intent = new Intent(UserLocationMainActivity.this, AccountMainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /*
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            DEFAULT_ZOOM);
                    latitude=currentLocation.getLatitude();
                    longitude=currentLocation.getLongitude();
                    Log.d(TAG, "onComplete: successful");
                } else {
                    Toast.makeText(UserLocationMainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        final Task location=mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Location loc=(Location)task.getResult();
                latitude=loc.getLatitude();
                longitude=loc.getLongitude();
                moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM);
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}
