package com.project.natsu_dragneel.people_tracker_android_java.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowedMembersAdapter extends RecyclerView.Adapter<FollowedMembersAdapter.FollowedMembersViewHolder> {

    ArrayList<CreateUser> nameList = new ArrayList<>();
    Context c;
    public FollowedMembersAdapter(ArrayList<CreateUser> nameList, Context c)
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
    public FollowedMembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.joined_card_layout,parent,false);
        FollowedMembersAdapter.FollowedMembersViewHolder membersViewHolder = new FollowedMembersAdapter.FollowedMembersViewHolder(view,c,nameList);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(FollowedMembersAdapter.FollowedMembersViewHolder holder, int position) {

        final CreateUser addCircle = nameList.get(position);
        // String name = nameList.get(position);
        Picasso.get().load(addCircle.ProfileImage).placeholder(R.drawable.icon_profile).into(holder.i1);

        holder.name_txt.setText(addCircle.Name);

    }


    public static class FollowedMembersViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener
    {
        TextView name_txt;
        View v;
        Context ctx;
        DatabaseReference reference,currentReference;
        FirebaseAuth auth;
        FirebaseUser user;
        ArrayList<CreateUser> nameArrayList;
        CircleImageView i1;
        public FollowedMembersViewHolder(View itemView,Context ctx,ArrayList<CreateUser> nameArrayList) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            this.v = itemView;
            this.ctx=ctx;
            this.nameArrayList = nameArrayList;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Followed");
            currentReference = FirebaseDatabase.getInstance().getReference().child("Users");

            name_txt = (TextView)itemView.findViewById(R.id.item_title);
            i1 =  itemView.findViewById(R.id.itemImage);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int position = getAdapterPosition();
            final CreateUser addCircle = this.nameArrayList.get(position);

            reference.child(addCircle.UserID).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                currentReference.child(addCircle.UserID).child("Followers").child(user.getUid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(ctx,"Unfollowed successfully",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });



                            }
                        }
                    });

            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add("Unfollow");
            myActionItem.setOnMenuItemClickListener(this);
        }
    }
}
