package com.talos.weaver.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.talos.weaver.Model.ModelPost;
import com.talos.weaver.PostDetailActivity;
import com.talos.weaver.PostDislikedByActivity;
import com.talos.weaver.PostLikedByActivity;
import com.talos.weaver.R;
import com.talos.weaver.ThereProfileActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterPostsSocial extends RecyclerView.Adapter<AdapterPostsSocial.MyHolder>{


    Context context;
    List<ModelPost> postList;
    String myUid;

    private DatabaseReference likesRef;
    private DatabaseReference dislikesRef;
    private DatabaseReference postsRef;
    private DatabaseReference userRef;

    boolean mProcessLike = false;
    boolean mProcessDislike = false;


    public AdapterPostsSocial(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        dislikesRef = FirebaseDatabase.getInstance().getReference().child("Blamed");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts1");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int i) {
        String uid = postList.get(i).getUid();
        String uEmail = postList.get(i).getuEmail();
        String uName = postList.get(i).getuName();
        String uDp = postList.get(i).getuDp();
        String pId = postList.get(i).getpId();
        String pTitle = postList.get(i).getpTitle();
        String pDescr = postList.get(i).getpDescr();
        String pImage = postList.get(i).getpImage();
        String pScore = String.valueOf(postList.get(i).getpScore());
        String pTimesStamp = postList.get(i).getpTime();
        String pLikes = String.valueOf(postList.get(i).getpLikes());
        String pDislikes= String.valueOf(postList.get(i).getpDislikes());
        String pComments = String.valueOf(postList.get(i).getpComments());


        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimesStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitle.setText(pTitle);
        holder.pDescriptionTv.setText(pDescr);
        holder.pLikesTv.setText(pLikes);
        holder.pDislikesTv.setText(pDislikes);
        holder.pScore.setText(pScore);
        holder.pComments.setText(pComments);


        setLikes(holder, pId);

        setDislikes(holder, pId);

        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_person_24).into(holder.uPictureIv);
        }
        catch (Exception e){

        }
        if(pImage.equals("noImage")){

            holder.pImageIv.setVisibility(View.GONE);
            holder.pLinearIv.setVisibility(View.GONE);

        }else {
            holder.pImageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).into(holder.pImageIv);
            }
            catch (Exception e){

            }
        }



        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOption(holder.moreBtn, uid,  pId, pImage);

            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pLikes = postList.get(i).getpLikes();
                int pScores = postList.get(i).getpScore();
                mProcessLike = true;

                String postIde = postList.get(i).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if(snapshot.child(postIde).hasChild(myUid)){
                                postsRef.child(postIde).child("pLikes").setValue(pLikes-1);
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else{
                                postsRef.child(postIde).child("pLikes").setValue(pLikes+1);
                                //
                                postsRef.child(postIde).child("pScore").setValue(pScores+1);
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;

                                addToHisNotification(""+uid, ""+pId, "Liked your post");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        holder.dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int dislikes1 = postList.get(i).getpDislikes();
                int pScores = postList.get(i).getpScore();
                //Toast.makeText(context, "Dislike", Toast.LENGTH_SHORT).show();
                mProcessDislike = true;

                String postIde = postList.get(i).getpId();
                dislikesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessDislike){
                            if(snapshot.child(postIde).hasChild(myUid)){
                                postsRef.child(postIde).child("pDislikes").setValue(dislikes1-1);
                                dislikesRef.child(postIde).child(myUid).removeValue();
                                mProcessDislike = false;
                            }
                            else{
                                postsRef.child(postIde).child("pDislikes").setValue(dislikes1+2);
                                postsRef.child(postIde).child("pScore").setValue(pScores-2);
                                Toast.makeText(context, "You lose 2 Coins", Toast.LENGTH_SHORT).show();
                                dislikesRef.child(postIde).child(myUid).setValue("Blamed");
                                mProcessDislike = false;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });



        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);

            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();

            }
        });
        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });

        holder.likers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostLikedByActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });
        holder.dislikers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDislikedByActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

    }

    private void addToHisNotification (String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();

        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", myUid);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void setDislikes(MyHolder holder, String postKey) {
        dislikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(myUid)){

                   // holder.dislikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_arrow_circle_down_24, 0 , 0 ,0 );
                   // holder.dislikeBtn.setText("Disliked");
                    holder.LDLin.setVisibility(View.GONE);

                    //Toast.makeText(context, "Dislike", Toast.LENGTH_SHORT).show();
                    holder.infoLin.setVisibility(View.VISIBLE);

                }else{
                   // holder.dislikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_arrow_circle_down_24, 0 , 0 ,0 );
                    //holder.dislikeBtn.setText("Dislike");


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes(MyHolder holder,final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(myUid)){

                   // holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_arrow_back_ios_new_24, 0 , 0 ,0 );
                  //  holder.likeBtn.setText("Liked");
                    holder.LDLin.setVisibility(View.GONE);

                    //Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
                    holder.infoLin.setVisibility(View.VISIBLE);

                }else{
                  //  holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_circle_up, 0 , 0 ,0 );
                    //holder.likeBtn.setText("Like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMoreOption(ImageButton moreBtn, String uid, String pId, String pImage) {

        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);
       if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0,0,"Delete");
        }

       popupMenu.getMenu().add(Menu.NONE, 1, 0,"View Detail");



        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0){
                    beginDelete(pId, pImage);
                }
                else if (id==1){
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                }



                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {

        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);
        }else{
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts1").orderByChild("pId").equalTo(pId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds: snapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }

                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage(String pId) {

        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");



        Query fquery = FirebaseDatabase.getInstance().getReference("Posts1").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }

                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView uPictureIv, pImageIv, likers, dislikers;

        TextView uNameTv, pTimeTv, pTitle, pDescriptionTv, pLikesTv, pDislikesTv,pScore,pComments;
        ImageButton moreBtn;
        LinearLayout infoLin, LDLin,profileLayout;
        ConstraintLayout pLinearIv;
        ImageButton likeBtn, dislikeBtn, commentBtn, shareBtn,forgiveBtn;



        public MyHolder(@NonNull View itemView) {
            super(itemView);

            infoLin = itemView.findViewById(R.id.infoLike);
            LDLin = itemView.findViewById(R.id.likeAndDislike);
            profileLayout = itemView.findViewById(R.id.profileLayout);
            pLinearIv = itemView.findViewById(R.id.linerIv);

            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.uTimeTv);
            pTitle = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            pDislikesTv = itemView.findViewById(R.id.pDislikesTv);
            pComments = itemView.findViewById(R.id.pCommentsTv);
            pScore = itemView.findViewById(R.id.pScore);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            dislikeBtn = itemView.findViewById(R.id.dislikesBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            likers = itemView.findViewById(R.id.likers);
            dislikers = itemView.findViewById(R.id.dislikers);

        }
    }
}
