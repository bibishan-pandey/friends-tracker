package com.project.natsu_dragneel.people_tracker_android_java.adapters.alert_adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities.LiveLocationActivity;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HelpAlertsAdapter extends RecyclerView.Adapter<HelpAlertsAdapter.HelpAlertViewHolder> {

    private static final String alert_remove = "Alert removed.";
    private static final String alert_remove_error = "Could not remove it";
    private static final String location_error = "Could not get the location. Try again";

    private final ArrayList<CreateUser> nameList;
    private final Context c;

    public HelpAlertsAdapter(ArrayList<CreateUser> nameList, Context c) {
        this.nameList = nameList;
        this.c = c;
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


    @NonNull
    @Override
    public HelpAlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_card, parent, false);
        return new HelpAlertViewHolder(view, c, nameList);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpAlertViewHolder holder, int position) {
        CreateUser addCircle = nameList.get(position);
        holder.alertNameTxt.setText(addCircle.Name);
        holder.alertDateTxt.setText(addCircle.Date);
        Picasso.get().load(addCircle.profile_image).placeholder(R.drawable.defaultprofile).into(holder.alertImageView);
    }

    public static class HelpAlertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        final View v;
        final Context ctx;
        final ArrayList<CreateUser> nameArrayList;
        final CircleImageView alertImageView;
        final TextView alertNameTxt;
        final TextView alertDateTxt;
        DatabaseReference myReference;
        final FirebaseAuth auth;
        final FirebaseUser user;

        HelpAlertViewHolder(View itemView, Context ctx, ArrayList<CreateUser> nameArrayList) {
            super(itemView);
            this.v = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            this.nameArrayList = nameArrayList;
            this.ctx = ctx;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                myReference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(Objects.requireNonNull(user).getUid())
                        .child("HelpAlerts");
            }

            alertImageView = itemView.findViewById(R.id.alertImage);
            alertNameTxt = itemView.findViewById(R.id.alertName);
            alertDateTxt = itemView.findViewById(R.id.alertDate);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            CreateUser addCircle = this.nameArrayList.get(position);
            String latitude_user = addCircle.lat;
            String longitude_user = addCircle.lng;

            if (latitude_user.equals("na") && longitude_user.equals("na")) {
                Toast.makeText(ctx, location_error, Toast.LENGTH_SHORT).show();
            } else {
                Intent mYIntent = new Intent(ctx, LiveLocationActivity.class);
                mYIntent.putExtra("latitude", latitude_user);
                mYIntent.putExtra("longitude", longitude_user);
                mYIntent.putExtra("Name", addCircle.Name);
                mYIntent.putExtra("UserId", addCircle.UserId);
                mYIntent.putExtra("Date", addCircle.Date);
                mYIntent.putExtra("image", addCircle.profile_image);
                mYIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(mYIntent);

            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            final CreateUser addCircle = this.nameArrayList.get(position);

            myReference.child(addCircle.UserId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ctx, alert_remove, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, alert_remove_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add("Delete");
            myActionItem.setOnMenuItemClickListener(this);
        }
    }
}