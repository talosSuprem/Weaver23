package com.talos.weaver.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.talos.weaver.ChatSocialActivity;
import com.talos.weaver.Model.User;
import com.talos.weaver.R;
import com.talos.weaver.ThereProfileActivity;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<User> userList;

    public AdapterUsers(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {


        String hisUID = userList.get(i).getId();
        String userImage = userList.get(i).getImageurl();
        String  userName = userList.get(i).getUsername();
        String userEmail = userList.get(i).getFullname();

        //String levelUser = String.valueOf(userList.get(i).getLevelUser());

        myHolder.mNameTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);
        //myHolder.mLevelTv.setText(levelUser);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_person_24)
                    .into(myHolder.mAvatarTv);
        }
        catch (Exception e){

        }


        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ThereProfileActivity.class);
                intent1.putExtra("uid", hisUID);
                context.startActivity(intent1);

/**
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Intent intent1 = new Intent(context, ThereProfileActivity.class);
                            intent1.putExtra("uid", hisUID);
                            context.startActivity(intent1);

                        }
                        if (which==1){
                            Intent intent = new Intent(context, ChatSocialActivity.class);
                            intent.putExtra("hisUid", hisUID);
                            context.startActivity(intent);


                        }
                    }
                });
                builder.create().show();**/
            }
        });



    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarTv;
        TextView mNameTv, mEmailTv , mLevelTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);


            mAvatarTv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            //mLevelTv = itemView.findViewById(R.id.levelTv);
        }
    }
}
