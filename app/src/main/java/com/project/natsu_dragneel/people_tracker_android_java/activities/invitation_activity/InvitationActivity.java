package com.project.natsu_dragneel.people_tracker_android_java.activities.invitation_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

import java.util.Objects;

@SuppressWarnings("unused")
public class InvitationActivity extends AppCompatActivity {

    private TextView show_code_textView;
    private String name;
    private String email;
    private String password;
    private String date;
    private String code = null;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ProgressDialog dialog;
    private StorageReference firebaseStorageReference;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        dialog = new ProgressDialog(this);
        TextView register_done_textView = findViewById(R.id.register_done_textview);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        firebaseStorageReference = FirebaseStorage.getInstance()
                .getReference()
                .child("Profile_images");
        show_code_textView = findViewById(R.id.show_code_textview);

        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("Name");
            email = intent.getStringExtra("Email");
            password = intent.getStringExtra("Password");
            date = intent.getStringExtra("Date");
            String isSharing = intent.getStringExtra("isSharing");
            code = intent.getStringExtra("Code");
            resultUri = intent.getParcelableExtra("imageUri");
        }

        if (code == null) {
            // check for code in firebase
            register_done_textView.setVisibility(View.GONE);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = auth.getCurrentUser();
                    String user_code = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        user_code = Objects.requireNonNull(dataSnapshot.child(user.getUid())
                                .child("FollowCode")
                                .getValue())
                                .toString();
                    }
                    show_code_textView.setText(user_code);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            show_code_textView.setText(code);
        }
    }

    public void share_code_button(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "People Tracker invitation code is '" + show_code_textView.getText().toString() + "'. Please follow me to connect.");
        startActivity(Intent.createChooser(i, "Share using:"));
    }

    public void register_done_button(View v) {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Creating a new account. Please wait");
        dialog.setCancelable(false);
        dialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            CreateUser createUser = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                createUser = new CreateUser(name, email, password, date, code, Objects.requireNonNull(user).getUid(), "false", "na", "na", "defaultimage");
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                reference.child(Objects.requireNonNull(user).getUid()).setValue(createUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    StorageReference filePath = firebaseStorageReference.child(user.getUid() + ".jpg");
                                                    filePath.putFile(resultUri)
                                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        String downloadPath = task.getResult().getStorage().getDownloadUrl().toString();
                                                                        reference.child(user.getUid()).child("profile_image").setValue(downloadPath)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            dialog.dismiss();
                                                                                            sendVerificationEmail();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "Could not upload profile picture", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not create account. Try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email sent for verification. Please check email.", Toast.LENGTH_SHORT).show();
                            finish();
                            auth.signOut();
                            Intent myIntent = new Intent(InvitationActivity.this, MainActivity.class);
                            startActivity(myIntent);
                        } else {
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
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