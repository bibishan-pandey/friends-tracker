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

    ArrayList<CreateUser> name_list;
    Context c;
    public MembersAdapter(ArrayList<CreateUser> name_list, Context c){
        this.name_list=name_list;
        this.c=c;
    }

    @Override
    public int getItemCount() {
        return name_list.size();
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout,viewGroup,false);

        MembersViewHolder membersViewHolder=new MembersViewHolder(v,c,name_list);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder membersViewHolder, int i) {
        CreateUser current_user_obj=name_list.get(i);
        membersViewHolder.name_text.setText(current_user_obj.name);
        Picasso.get().load(current_user_obj.imageURL).placeholder(R.drawable.icon_profile).into(membersViewHolder.circle_image_view);
        if(current_user_obj.isSharing.equals("false")){
            membersViewHolder.online_indicator.setImageResource(R.drawable.icon_location_off);
        }
        else if(current_user_obj.isSharing.equals("true")){
            membersViewHolder.online_indicator.setImageResource(R.drawable.icon_location_on);
        }
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        TextView name_text;
        ImageView online_indicator;
        CircleImageView circle_image_view;

        View v;
        Context c;

        ArrayList<CreateUser> name_array_list;

        FirebaseAuth auth;
        FirebaseUser user;
        DatabaseReference mReference,mJoinedRef;

        public MembersViewHolder(@NonNull View itemView,  Context c, ArrayList<CreateUser> name_array_list) {

            super(itemView);
            this.c = c;
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener((View.OnCreateContextMenuListener) this);
            this.name_array_list=name_array_list;
            this.c=c;
            auth=FirebaseAuth.getInstance();
            user=auth.getCurrentUser();
            mReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
            mJoinedRef=FirebaseDatabase.getInstance().getReference().child("Users");
            name_text=itemView.findViewById(R.id.item_title);
            online_indicator=itemView.findViewById(R.id.online_indicator);
            circle_image_view=itemView.findViewById(R.id.image_profile);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            CreateUser addCircle = this.name_array_list.get(position);
            String latitude_user = addCircle.lat;
            String longitude_user = addCircle.lng;
            if(latitude_user.equals("n/a") && longitude_user.equals("n/a")){
                Toast.makeText(c,"User is not sharing location",Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent=new Intent(c,LiveMapActivity.class);
                intent.putExtra("latitude",latitude_user);
                intent.putExtra("longitude",longitude_user);
                intent.putExtra("name", addCircle.name);
                intent.putExtra("userid",addCircle.userID);
                intent.putExtra("date",addCircle.date);
                intent.putExtra("image",addCircle.imageURL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int position=getAdapterPosition();
            final CreateUser addCircle=this.name_array_list.get(position);
            mReference.child(addCircle.userID).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mJoinedRef.child(addCircle.userID).child("JoinedCircles").child(user.getUid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(c,"User Removed from circle",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem my_action_item=contextMenu.add("Remove");
            my_action_item.setOnMenuItemClickListener(this);
        }
    }
}
