package com.talos.weaver.Adapter;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.talos.weaver.R;

import java.util.Collection;
import java.util.Collections;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageViewProfile, postIv;
    public TextView titleTv, descTv, likesTv,dislikesTv,scoreTv, commentTv, timesTv, nameProfileTv;
    public ImageButton likeBtn, dislikesBtn, menuOptions, commentsBtn;
    public DatabaseReference likesref;




    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int likescount;
    int dislikescount;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void SetPost(FragmentActivity activity,String name, String url, String postUri, String time,
                        String uid, String type, String desc ,String  title ,  int pScore, int pLikes,
                        int pDislikes, int pComments){

        imageViewProfile = itemView.findViewById(R.id.uPictureIv);
        commentTv = itemView.findViewById(R.id.commentTv);
        commentsBtn = itemView.findViewById(R.id.commentBtn);
        descTv= itemView.findViewById(R.id.pDescriptionTv);
        likeBtn = itemView.findViewById(R.id.likeBtn);
        dislikesBtn = itemView.findViewById(R.id.dislikesBtn);
        nameProfileTv= itemView.findViewById(R.id.uNameTv);
        titleTv = itemView.findViewById(R.id.pTitleTv);
        timesTv = itemView.findViewById(R.id.uTimeTv);
        menuOptions = itemView.findViewById(R.id.moreBtn);
        scoreTv = itemView.findViewById(R.id.pScore);
        postIv = itemView.findViewById(R.id.pImageIv);

        SimpleExoPlayer exoPlayer;



        PlayerView playerView = itemView.findViewById(R.id.exoplayer_item_post);

        if(type.equals("iv")){

            Picasso.get().load(url).into(imageViewProfile);
            Picasso.get().load(postUri).into(postIv);
            descTv.setText(desc);
            titleTv.setText(title);
            nameProfileTv.setText(name);
            timesTv.setText(time);
            playerView.setVisibility(View.INVISIBLE);
        }else if(type.equals("vv")){
            Picasso.get().load(url).into(imageViewProfile);
            postIv.setVisibility(View.INVISIBLE);
            descTv.setText(desc);
            titleTv.setText(title);
            timesTv.setText(time);
            nameProfileTv.setText(name);


            try {
               /**BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(activity);
                Uri video = Uri.parse(postUri);
                DefaultHttpDataSourceFactory df = new DefaultHttpDataSourceFactory("video");
                ExtractorsFactory ef = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(video, df,ef, null,null);
                playerView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(false);**/

               SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(activity).build();
               playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(postUri);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);



            }catch (Exception e){
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();

            }


        }

    }
    public void likesChecker(final String postKey){
        likeBtn = itemView.findViewById(R.id.likeBtn);

        likesref = database.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(uid)){
                    likeBtn.setImageResource(R.drawable.arrowup);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
//                    likesTv.setText(Integer.toString(likescount)+"likes");
                }else{
                    likeBtn.setImageResource(R.drawable.ic_arrow_down);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
//                    likesTv.setText(Integer.toString(likescount)+"likes");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
