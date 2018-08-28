package com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.receiver.GeofenceTransitionReceiver;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("unused")
public class LiveLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    private static final String TAG=LiveLocationActivity.class.getSimpleName();

    private GoogleMap mMap;
    private LatLng friendLatLng;
    private LatLng geoMark;
    private PendingIntent geofencePendingIntent;

    private List<Geofence> fences=new ArrayList<>();
    private Location location;

    GeoFire geoFire;
    private Button geofence_click;
    private Double geofence_radius=500.0;
    private Circle mapcircle;
    private VerticalSeekBar mVerticalSeekBar;

    private GoogleApiClient mGoogleApiClient;

    private String latitude;
    private String longitude;
    private String name;
    private String userid;
    private String prevdate;
    private String prevImage;
    private TextView user_name_textview;
    private Marker marker;
    private DatabaseReference reference;
    private String myImage;

    private String myName;
    private String myLat;
    private String myLng;
    private String myDate;
    private ArrayList<String> mKeys;
    private MarkerOptions myOptions;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_location);

        geofence_click=(Button)findViewById(R.id.geofence_click);

        mVerticalSeekBar= (VerticalSeekBar) findViewById(R.id.vertical_seekbar);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(i), 1500, null);
                /*)
                if(seekBar.getProgress()>6){
                    geofence_radius+=50;
                }
                else{
                    geofence_radius-=50;
                }*/
                remove_circle();
                draw_circle();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: seekbar touching");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: stopped");
            }
        });

        user_name_textview = (TextView) findViewById(R.id.user_name_textview);
        myOptions = new MarkerOptions();
        Intent intent = getIntent();
        mKeys = new ArrayList<>();
        if (intent != null) {
            latitude = intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
            name = intent.getStringExtra("Name");
            userid = intent.getStringExtra("UserId");
            prevdate = intent.getStringExtra("Date");
            prevImage = intent.getStringExtra("image");
        }
        user_name_textview.setText(name + "'s location");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: added");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myName = dataSnapshot.child("name").getValue(String.class);
                        myLat = dataSnapshot.child("lat").getValue(String.class);
                        myLng = dataSnapshot.child("lng").getValue(String.class);
                        myDate = dataSnapshot.child("date").getValue(String.class);
                        myImage = dataSnapshot.child("profile_image").getValue(String.class);
                        friendLatLng = new LatLng(Double.parseDouble(myLat), Double.parseDouble(myLng));

                        myOptions.position(friendLatLng);
                        myOptions.snippet("Last seen: " + myDate);
                        myOptions.title(myName);

                        if (marker == null) {
                            marker = mMap.addMarker(myOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng, 15));
                        } else {
                            marker.setPosition(friendLatLng);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: cancelled");
                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: cancelled");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //geoMap=googleMap;
        LatLng kathmandu = new LatLng(27.7172, 85.3240);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmandu, 12));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public View getInfoContents(Marker marker) {
                @SuppressLint("InflateParams") View row = getLayoutInflater().inflate(R.layout.custom_snippet, null);
                TextView nameTxt = row.findViewById(R.id.snippetName);
                TextView dateTxt = row.findViewById(R.id.snippetDate);
                CircleImageView imageTxt = row.findViewById(R.id.snippetImage);
                if (myName == null && myDate == null) {
                    nameTxt.setText(name);
                    dateTxt.setText(dateTxt.getText().toString() + prevdate);
                    Picasso.get().load(prevImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                } else {
                    nameTxt.setText(myName);
                    dateTxt.setText(dateTxt.getText().toString() + myDate);
                    Picasso.get().load(myImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                }
                return row;
            }
        });

        friendLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        MarkerOptions optionsnew = new MarkerOptions();
        optionsnew.position(friendLatLng);
        optionsnew.title(name);
        optionsnew.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        if (marker == null) {
            marker = mMap.addMarker(optionsnew);
        } else {
            marker.setPosition(friendLatLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng, 15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //geoMap.clear();
                geoMark=new LatLng(latLng.latitude,latLng.longitude);
                mMap.addMarker(new MarkerOptions().position(geoMark).title("Geofence").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                remove_circle();
                draw_circle();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void draw_circle(){
        geofence_click.setText("Stop Geofence");
        mapcircle=mMap.addCircle(new CircleOptions()
                .center(geoMark)
                .radius(geofence_radius)
                .strokeColor(Color.argb(0xaa, 0x00, 0x00, 0xff))
                .fillColor(Color.argb(0x55, 0x00, 0x00, 0xff))
                .strokeWidth(5f));
    }

    @SuppressLint("SetTextI18n")
    private void remove_circle(){
        geofence_click.setText("Start Geofence");
        if(mapcircle!=null){
            mapcircle.remove();
        }
    }

    public void start_geofence(View v) {
        if(geofence_click.getText().toString()=="Start Geofence"){
            if(geoMark!=null){
                draw_circle();
            }
            else{
                Toast.makeText(this, "Please click somewhere on the map", Toast.LENGTH_SHORT).show();
            }

        }else{
            remove_circle();
        }

    }

    public void back_image_button(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        geofencePendingIntent = getRequestPendingIntent();
    }

    public PendingIntent getRequestPendingIntent() {
        return createRequestPendingIntent();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location){
    }

    @Override
    public void onResult(@NonNull Status status) {
    }

    public void fetch_location(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LatLng kathmandu = new LatLng(27.7172, 85.3240);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmandu, 12));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private PendingIntent createRequestPendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        } else {
            Intent intent = new Intent(this, GeofenceTransitionReceiver.class);
            intent.setAction("geofence_transition_action");
            return PendingIntent.getBroadcast(this, R.id.geofence_transition_intent, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
