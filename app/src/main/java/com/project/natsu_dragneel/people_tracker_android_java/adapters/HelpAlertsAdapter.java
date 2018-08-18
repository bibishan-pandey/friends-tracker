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

public class HelpAlertsAdapter extends RecyclerView.Adapter<HelpAlertsAdapter.HelpAlertViewHolder> {

    ArrayList<CreateUser> name_list=new ArrayList<>();
    Context c;

    public HelpAlertsAdapter(ArrayList<CreateUser> name_list,Context c){
        this.name_list=name_list;
        this.c=c;
    }

    @Override
    public int getItemCount() {
        return name_list.size();
    }

    @NonNull
    @Override
    public HelpAlertViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alert_layout,viewGroup,false);
        HelpAlertViewHolder alertViewHolder=new HelpAlertViewHolder(view,c,name_list);
        return alertViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HelpAlertViewHolder helpAlertViewHolder, int i) {
        CreateUser addCircle=name_list.get(i);
        helpAlertViewHolder.alertNameTxt.setText(addCircle.name);
        helpAlertViewHolder.alertDateTxt.setText(addCircle.date);
        Picasso.get().load(addCircle.profile_image).placeholder(R.drawable.icon_profile).into(helpAlertViewHolder.alertImageView);
    }

    public static class HelpAlertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener{
        View v;
        Context ctx;
        ArrayList<CreateUser> name_array_list;
        CircleImageView alertImageView;
        TextView alertNameTxt,alertDateTxt;
        DatabaseReference myReference;
        FirebaseAuth auth;
        FirebaseUser user;

        public HelpAlertViewHolder(@NonNull View itemView, Context ctx, ArrayList<CreateUser> name_array_list) {
            super(itemView);
            this.v = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            this.ctx = ctx;
            this.name_array_list = name_array_list;

            auth=FirebaseAuth.getInstance();
            user=auth.getCurrentUser();
            myReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("HelpAlerts");
            alertImageView=itemView.findViewById(R.id.alertImage);
            alertNameTxt=itemView.findViewById(R.id.alertName);
            alertDateTxt=itemView.findViewById(R.id.alertDate);
        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            CreateUser addCircle=this.name_array_list.get(position);
            String latitude_user=addCircle.lat;
            String longitude_user=addCircle.lng;

            if(latitude_user.equals("n/a")&& longitude_user.equals("n/a")){
                Toast.makeText(ctx,"Could not get location",Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent=new Intent(ctx, LiveMapActivity.class);
                intent.putExtra("latitude",latitude_user);
                intent.putExtra("longitude",longitude_user);
                intent.putExtra("name",addCircle.name);
                intent.putExtra("userid",addCircle.userid);
                intent.putExtra("date",addCircle.date);
                intent.putExtra("image",addCircle.profile_image);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int position=getAdapterPosition();
            final CreateUser addCircle=this.name_array_list.get(position);
            myReference.child(addCircle.userid).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ctx,"Alert removed", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(ctx,"Could not remove it",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem myActionItem=contextMenu.add("REMOVE");
            myActionItem.setOnMenuItemClickListener(this);
        }
    }
}
