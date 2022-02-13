package com.talos.weaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.inappmessaging.MessagesProto;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.talos.weaver.AdapterTube.PlaylistAdapter;
import com.talos.weaver.ModelsTube.PlaylistModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PublishContentActivity extends AppCompatActivity {

    EditText input_video_title, input_video_description;
    LinearLayout progressLyt;
    ProgressBar progressBar;
    TextView progress_text;

    VideoView videoView;
    String type;
    Uri videoUri;

    MediaController mediaController;

    NachoTextView nachoTextView;

    TextView txt_upload;

    TextView txt_choose_playlist;

    Dialog dialog;

    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;

    String selectedPlaylist;
    int videosCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_content);

        nachoTextView = findViewById(R.id.input_video_tag);

        txt_upload = findViewById(R.id.txt_upload_video);

        txt_choose_playlist = findViewById(R.id.choose_playlist);

        videoView = findViewById(R.id.videoView);

        input_video_title = findViewById(R.id.input_video_title);
        input_video_description = findViewById(R.id.input_video_description);
        progressLyt = findViewById(R.id.progressLyt);
        progress_text = findViewById(R.id.progress_text);





        mediaController = new MediaController(PublishContentActivity.this);

        Intent intent = getIntent();

        if(intent != null){
            videoUri = intent.getData();
            videoView.setVideoURI(videoUri);
            videoView.setMediaController(mediaController);
            videoView.start();

        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Videos");
        storageReference = FirebaseStorage.getInstance().getReference().child("Video");

        nachoTextView.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PublishContentActivity.this, "Tags: " +nachoTextView.getAllChips().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        txt_choose_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaylistDialog();
            }
        });


        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = input_video_title.getText().toString();
                String description = input_video_description.getText().toString();
                String tags = nachoTextView.getAllChips().toString().replace(",","");

                if (title.isEmpty() || description.isEmpty() || tags.isEmpty()){
                    Toast.makeText(PublishContentActivity.this, "fill all fields...", Toast.LENGTH_SHORT).show();

                }else if (txt_choose_playlist.getText().toString().equals("Choose Playlist")){
                    Toast.makeText(PublishContentActivity.this, "Please select playlist", Toast.LENGTH_SHORT).show();
                }else{
                    uploadVideoToStorage(title,description,tags);
                }
            }
        });
    }

    private void uploadVideoToStorage(String title, String description, String tags) {
        progressLyt.setVisibility(View.VISIBLE);
        final StorageReference sRef = storageReference.child(user.getUid())
                .child(System.currentTimeMillis()+","+getFileExtension(videoUri));
        sRef.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String videoUrl = uri.toString();

                        saveDataToFirebase(title,description,tags,videoUrl);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                progress_text.setText("Uploading "+(int)progress+"%");
            }
        });

    }

    private void saveDataToFirebase(String title, String description, String tags, String videoUrl) {
        String currentDate = DateFormat.getDateInstance().format(new Date());
        String videoId = reference.push().getKey();

        HashMap<String,Object> map = new HashMap<>();
        map.put("videoid", videoId);
        map.put("video_title", title);
        map.put("description", description);
        map.put("video_tag", tags);
        map.put("playlist", selectedPlaylist);
        map.put("video_url", videoUrl);
        map.put("publisher", user.getUid());
        map.put("date", currentDate);

        reference.child(videoId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressLyt.setVisibility(View.GONE);
                    Toast.makeText(PublishContentActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PublishContentActivity.this, CryptubeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();

                    updateVideoCount();


                }
                else {
                    progressLyt.setVisibility(View.GONE);
                    Toast.makeText(PublishContentActivity.this, "Failed to upload" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateVideoCount() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlist");

        int update = videosCount + 1;

        HashMap<String, Object> map = new HashMap<>();
        map.put("videos", update);

        reference.child(user.getUid()).child(selectedPlaylist).updateChildren(map);


    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void showPlaylistDialog() {

        dialog = new Dialog(PublishContentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.play_list_dialog);
        dialog.setCancelable(true);


        EditText input_playlist_name = dialog.findViewById(R.id.input_playlist);
        TextView txt_add = dialog.findViewById(R.id.txt_add);

        ArrayList<PlaylistModel> list = new ArrayList<>();
        PlaylistAdapter adapter;

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        adapter = new PlaylistAdapter(PublishContentActivity.this, list, new PlaylistAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(PlaylistModel model) {
                dialog.dismiss();
                selectedPlaylist = model.getPlaylist_name();
                videosCount = model.getVideos();
                txt_choose_playlist.setText("playlist: " + model.getPlaylist_name());

            }
        });

        recyclerView.setAdapter(adapter);


        checkUserAlreadyhavePlaylist(recyclerView);

        showAllPlaylist(adapter, list);


        txt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = input_playlist_name.getText().toString();
                if (value.isEmpty()){
                    Toast.makeText(PublishContentActivity.this, "Entrer Playlist Name", Toast.LENGTH_SHORT).show();
                }else{
                    createNewPlaylist(value);
                }
            }
        });

        dialog.show();

    }

    private void showAllPlaylist(PlaylistAdapter adapter, ArrayList<PlaylistModel> list) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlist");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        PlaylistModel model = dataSnapshot.getValue(PlaylistModel.class);
                        list.add(model);

                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(PublishContentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });


    }

    private void createNewPlaylist(String value) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
        HashMap<String,Object> map = new HashMap<>();
        map.put("Playlist", value);
        map.put("videos", 0);
        map.put("uid", user.getUid());

        reference.child(user.getUid()).child(value).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PublishContentActivity.this, "New playlist created", Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(PublishContentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void checkUserAlreadyhavePlaylist(RecyclerView recyclerView) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Playlists");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Toast.makeText(PublishContentActivity.this, "User have playlist", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.VISIBLE);


                }else{
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PublishContentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }
}