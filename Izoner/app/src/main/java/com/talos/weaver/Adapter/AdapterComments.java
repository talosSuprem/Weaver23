package com.talos.weaver.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.talos.weaver.Model.ModelComment;
import com.talos.weaver.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder> {

    Context context;
    List<ModelComment> commentList;

    public AdapterComments(Context context, List<ModelComment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        String uid = commentList.get(i).getUid();
        String name = commentList.get(i).getuName();
        String email = commentList.get(i).getuEmail();
        String image = commentList.get(i).getuPd();
        String cid = commentList.get(i).getcId();
        String comment = commentList.get(i).getComment();
        String timestamp = commentList.get(i).getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        holder.timeTv.setText(pTime);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_person_24).into(holder.avatarIv);
        }
        catch (Exception e){

        }





    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
