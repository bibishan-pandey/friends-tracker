package com.project.natsu_dragneel.people_tracker_android_java.adapters.following_adapter;

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

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.JoinedMembersViewHolder>
{
    ArrayList<CreateUser> nameList = new ArrayList<>();
    Context c;
    public FollowingAdapter(ArrayList<CreateUser> nameList,Context c)
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
    public FollowingAdapter.JoinedMembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_card_layout,parent,false);
        FollowingAdapter.JoinedMembersViewHolder membersViewHolder = new FollowingAdapter.JoinedMembersViewHolder(view,c,nameList);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(FollowingAdapter.JoinedMembersViewHolder holder, int position) {

        final CreateUser addCircle = nameList.get(position);
        // String name = nameList.get(position);
        Picasso.get().load(addCircle.profile_image).placeholder(R.drawable.defaultprofile).into(holder.i1);

        holder.name_txt.setText(addCircle.name);

    }


    public static class JoinedMembersViewHolder extends RecyclerView.ViewHolder
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
        public JoinedMembersViewHolder(View itemView,Context ctx,ArrayList<CreateUser> nameArrayList) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            this.v = itemView;
            this.ctx=ctx;
            this.nameArrayList = nameArrayList;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("JoinedCircles");
            currentReference = FirebaseDatabase.getInstance().getReference().child("Users");

            name_txt = (TextView)itemView.findViewById(R.id.item_title);
            i1 =  itemView.findViewById(R.id.itemImage);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int position = getAdapterPosition();
            final CreateUser addCircle = this.nameArrayList.get(position);

            reference.child(addCircle.userid).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                currentReference.child(addCircle.userid).child("CircleMembers").child(user.getUid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(ctx,"Unjoined successfully",Toast.LENGTH_SHORT).show();
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
            MenuItem myActionItem = menu.add("UNJOIN");
            myActionItem.setOnMenuItemClickListener(this);
        }
    }
}
