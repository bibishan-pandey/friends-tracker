package com.project.natsu_dragneel.people_tracker_android_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.LiveMapActivity;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {
    ArrayList<CreateUser> nameList = new ArrayList<>();
    Context c;
    public MembersAdapter(ArrayList<CreateUser> nameList,Context c)
    {
        this.nameList = nameList;
        this.c=c;

    }

    @Override
    public int getItemCount()
    {
        return nameList.size();
    }

    @Override
    public MembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        MembersViewHolder membersViewHolder = new MembersViewHolder(view,c,nameList);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(MembersViewHolder holder, int position) {

        CreateUser addCircle = nameList.get(position);
        holder.name_txt.setText(addCircle.Name);
        Picasso.get().load(addCircle.ProfileImage).placeholder(R.drawable.icon_profile).into(holder.circleImageView);

        if(addCircle.isSharing.equals("false"))
        {
            holder.i1.setImageResource(R.drawable.icon_location_off);
        }
        else if(addCircle.isSharing.equals("true"))
        {
            holder.i1.setImageResource(R.drawable.icon_location_on);
        }


    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            ,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener

    {
        TextView name_txt;
        ImageView i1;
        CircleImageView circleImageView;
        View v;
        Context ctx;
        ArrayList<CreateUser> nameArrayList;

        FirebaseAuth mAuth;
        FirebaseUser mUser;
        DatabaseReference mReference,mJoinedRef;

        public MembersViewHolder(View itemView,Context ctx,ArrayList<CreateUser> nameArrayList) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            this.nameArrayList = nameArrayList;
            this.v = itemView;
            this.ctx = ctx;
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Followers");
            mJoinedRef = FirebaseDatabase.getInstance().getReference().child("Users");
            name_txt = itemView.findViewById(R.id.item_title);
            i1  = itemView.findViewById(R.id.item_image);
            circleImageView = itemView.findViewById(R.id.item_imageprofile);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            CreateUser addCircle = this.nameArrayList.get(position);
            String latitude_user = addCircle.Lat;
            String longitude_user = addCircle.Lng;


            if(latitude_user.equals("na") && longitude_user.equals("na"))
            {

                Toast.makeText(ctx,"This circle member is not sharing location.",Toast.LENGTH_SHORT).show();


            }
            else
            {
                Intent mYIntent = new Intent(ctx,LiveMapActivity.class);
                // mYIntent.putExtra("createuserobject",addCircle);
                mYIntent.putExtra("Latitude",latitude_user);
                mYIntent.putExtra("Longitude",longitude_user);
                mYIntent.putExtra("Name",addCircle.Name);
                mYIntent.putExtra("UserID",addCircle.UserID);
                mYIntent.putExtra("Date",addCircle.Date);
                mYIntent.putExtra("Image",addCircle.ProfileImage);
                mYIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(mYIntent);
            }

        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            final CreateUser addCircle = this.nameArrayList.get(position);

            mReference.child(addCircle.UserID).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mJoinedRef.child(addCircle.UserID).child("Followed").child(mUser.getUid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(ctx,"User removed from circle.",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });


            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add("Remove");
            myActionItem.setOnMenuItemClickListener(this);
        }
    }
}
