package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserLocationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener {

    final String no_location="Could not get location";
    final String current_location="Current location";

    String current_user_name;
    String current_user_email;
    String current_user_imageURL;

    GoogleMap mMap;
    FirebaseAuth auth;
    FirebaseUser user;

    GoogleApiClient client;
    LocationRequest request;

    DatabaseReference databaseReference;

    LatLng latLng;

    View header;

    TextView textView_title_name;
    TextView textView_title_email;

    ImageView imageView_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header=navigationView.getHeaderView(0);
        textView_title_name=header.findViewById(R.id.textView_title_name);
        textView_title_email=header.findViewById(R.id.textView_title_email);
        imageView_user_image=header.findViewById(R.id.imageView_user_image);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user_name=dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                current_user_email=dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                current_user_imageURL=dataSnapshot.child(user.getUid()).child("imageURL").getValue(String.class);

                textView_title_name.setText(current_user_name);
                textView_title_email.setText(current_user_email);
                Picasso.get().load(current_user_imageURL).into(imageView_user_image);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_location_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_joinCircle) {
            Intent intent=new Intent(UserLocationMainActivity.this,JoinCirlceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_myCircle) {

        } else if (id == R.id.nav_joinedCircle) {

        } else if (id == R.id.nav_inviteMembers) {

        } else if (id == R.id.nav_shareLocation) {
            Intent i=new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT,"My location is :"+"https://www.google.com/maps/@"+latLng.latitude+","+latLng.longitude+",17z");
            startActivity(Intent.createChooser(i,"Share using: "));

            /*
            Double latitude = latLng.latitude;
            Double longitude = latLng.longitude;

            String uri = "http://maps.google.com/maps?saddr=" +latitude+","+longitude;

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String ShareSub = "Here is my location";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));*/
        } else if (id == R.id.nav_signOut) {
            //user = auth.getCurrentUser();
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
        mMap = googleMap;
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        request.setFastestInterval(2500);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("TAG_CONNECTION_FAILED", "Connection failed: ConnectionResult.getErrorCode() = "+ connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null){
            Toast.makeText(getApplicationContext(),no_location,Toast.LENGTH_LONG).show();
        }
        else{
            latLng=new LatLng(location.getLatitude(),location.getLongitude());

            MarkerOptions options=new MarkerOptions();
            //mMap.clear();
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
            options.position(latLng);
            options.title(current_location);
            mMap.addMarker(options);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

            //zoom into a particular position
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            mMap.moveCamera(zoom);
            mMap.animateCamera(zoom);
        }
    }
}
