package com.project.natsu_dragneel.people_tracker_android_java.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        /*
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout,viewGroup,false);

        MembersViewHolder membersViewHolder=new MembersViewHolder(v,c,name_list);
        return membersViewHolder;
        */
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder membersViewHolder, int i) {
        /*
        CreateUser current_user_obj=name_list.get(i);
        membersViewHolder.name_text.setText(current_user_obj.name);
        Picasso.get().load(current_user_obj.imageURL).placeholder(R.drawable.icon_profile).into(membersViewHolder.circle_image_view);
        */
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name_text;
        CircleImageView circle_image_view;

        View v;
        Context c;

        ArrayList<CreateUser> name_array_list;

        FirebaseAuth auth;
        FirebaseUser user;

        public MembersViewHolder(@NonNull View itemView,  Context c, ArrayList<CreateUser> name_array_list) {

            super(itemView);
            this.c = c;
            this.name_array_list = name_array_list;

            itemView.setOnClickListener(this);

            /*
            auth=FirebaseAuth.getInstance();
            user=auth.getCurrentUser();

            name_text=itemView.findViewById(R.id.item_title);
            circle_image_view=itemView.findViewById(R.id.circleImageView);*/
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(c,"You have clicked this user",Toast.LENGTH_LONG).show();
        }
    }
}
