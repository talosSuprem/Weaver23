package com.talos.weaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.squareup.picasso.Picasso;
import com.talos.weaver.Model.Postmember;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostingNewActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;

    FirebaseAuth firebaseAuth;
    DatabaseReference userDb;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1, db2, db3, db4;


    MediaController mediaController;

    ActionBar actionBar;


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;


    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    String[] cameraPermissions;
    String[] storagePermissions;



    EditText titleEt, descriptionEt;

    ImageView imageIv,returnI;
    FloatingActionButton uploadBtn;

    NachoTextView nachoTextView;

    String name,uid, dp;

    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    VideoView videoView;
    String url,name1;

    String type;
    Postmember postmember;

    Uri image_rui = null;

    ProgressDialog pd;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_new);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-1750096581756549/7941561472", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        mInterstitialAd = null;
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(PostingNewActivity.this);
                } else {

                }
            }
        },20);

         postmember = new Postmember();

        mediaController = new MediaController(this);




        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);



        storageReference = FirebaseStorage.getInstance().getReference("User Posts");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");
        db4 = database.getReference("All texts").child(currentuid);




        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        nachoTextView = findViewById(R.id.input_publication_tag);
        nachoTextView.addChipTerminator('.', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        userDb = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDb.orderByChild("id").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    name = ""+ds.child("username").getValue();
                    dp = ""+ds.child("imageurl").getValue();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        titleEt = findViewById(R.id.pTitleEt);
        descriptionEt = findViewById(R.id.pDescriptionEt);
        imageIv = findViewById(R.id.pImageIv);
        uploadBtn = findViewById(R.id.pUploadBtn);
        videoView = findViewById(R.id.pVideoV);

        /**Intent intent = getIntent();

         String action = intent.getAction();
         String type = intent.getType();
         if(Intent.ACTION_SEND.equals(action) && type!=null){
         if("text/plain".equals(type)){
         handleSendText(intent);
         }
         else if(type.startsWith("image")){
         //handleSendImage(intent);
         }
         }**/

        //final String isUbdateKey = ""+intent.getStringExtra("key");
        //final String editPostId = ""+intent.getStringExtra("editPostId");


        returnI = findViewById(R.id.backToHome);
        returnI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();

            }
        });




        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dopost();
                /** String title = titleEt.getText().toString().trim();
                 String description = descriptionEt.getText().toString().trim();

                 if(TextUtils.isEmpty(title)){
                 Toast.makeText(AddPostSocialActivity.this, "Enter title...", Toast.LENGTH_SHORT).show();
                 return;
                 }
                 //if(TextUtils.isEmpty(description)){
                 // Toast.makeText(AddPostSocialActivity.this, "Enter description...", Toast.LENGTH_SHORT).show();
                 //  return;

                 // }

                 if(image_rui==null){
                 uploadData(title, description, "noImage");
                 }
                 else{
                 Dopost();

                 }**/
            }
        });
    }

    /** private void handleSendImage(Intent intent) {
     Uri imageURI = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
     if(imageURI != null){
     image_rui = imageURI;

     imageIv.setImageURI(image_rui);
     }


     }**/

    /** private void handleSendText(Intent intent) {
     String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
     if(sharedText!=null){
     descriptionEt.setText(sharedText);
     }

     }**/

    void Dopost(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        String desc = descriptionEt.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate+":"+ savetime;

        if(TextUtils.isEmpty(desc) || selectedUri != null){


            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> uriTask = uploadTask.continueWithTask((task) -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener((task)-> {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    if(type.equals("iv")){
                        postmember.setDesc(desc);
                        postmember.setName(name1);
                        postmember.setPostUri(downloadUri.toString());
                        postmember.setTime(time);
                        postmember.setUid(currentuid);
                        postmember.setUrl(url);
                        postmember.setType("iv");
                        postmember.setpComments(0);
                        postmember.setpDislikes(0);
                        postmember.setpLikes(0);
                        postmember.setpScore(0);

                        String id = db1.push().getKey();
                        db1.child(id).setValue(postmember);

                        String id1 = db3.push().getKey();
                        db3.child(id1).setValue(postmember);
                        Toast.makeText(this, "Post uploaded", Toast.LENGTH_SHORT).show();


                    }else if(type.equals("vv")){
                        postmember.setDesc(desc);
                        postmember.setName(name1);
                        postmember.setPostUri(downloadUri.toString());
                        postmember.setTime(time);
                        postmember.setUid(currentuid);
                        postmember.setUrl(url);
                        postmember.setType("vv");
                        postmember.setpComments(0);
                        postmember.setpDislikes(0);
                        postmember.setpLikes(0);
                        postmember.setpScore(0);

                        String id3 = db2.push().getKey();
                        db1.child(id3).setValue(postmember);

                        String id4 = db3.push().getKey();
                        db3.child(id4).setValue(postmember);
                        Toast.makeText(this, "Post uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PostingNewActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else {
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                    }

                }

            });







        }else{
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }


    }

    private void uploadData(String title, String description, String uri) {



        /** pd.setMessage("Publishing post...");
         pd.show();


         String timesStamp = String.valueOf(System.currentTimeMillis());
         String filePathAndName = "Posts1/" +"post_" +timesStamp;


         if(!uri.equals("noImage")){

         StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
         ref.putFile(Uri.parse(uri))
         .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
        while (!uriTask.isSuccessful());

        String downloadUri = uriTask.getResult().toString();

        if(uriTask.isSuccessful()){
        HashMap<Object, Object> hashMap = new HashMap<>();


        hashMap.put("uid", uid);
        hashMap.put("uName", name);
        hashMap.put("uDp", dp);
        hashMap.put("pId", timesStamp);
        hashMap.put("pTitle", title);
        hashMap.put("pDescr", description);
        hashMap.put("pImage", downloadUri);
        hashMap.put("pTime", timesStamp);
        hashMap.put("pScore", 0);
        hashMap.put("pLikes", 0);
        hashMap.put("pDislikes", 0);
        hashMap.put("pComments", 0);



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts1");


        ref.child(timesStamp).setValue(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void unused) {
        pd.dismiss();
        Toast.makeText(AddPostSocialActivity.this,"Post published", Toast.LENGTH_SHORT).show();
        titleEt.setText("");
        descriptionEt.setText("");
        imageIv.setImageURI(null);
        image_rui = null;
        Intent intent = new Intent(AddPostSocialActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        if (mInterstitialAd != null) {
        mInterstitialAd.show(AddPostSocialActivity.this);
        } else {

        }
        }
        })
        .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        Toast.makeText(AddPostSocialActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

        pd.dismiss();

        }
        });



        }

        }
        })
         .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        pd.dismiss();
        Toast.makeText(AddPostSocialActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        });


         }else{

         HashMap<Object, Object> hashMap = new HashMap<>();

         hashMap.put("uid", uid);
         hashMap.put("uName", name);
         hashMap.put("uDp", dp);
         hashMap.put("pId", timesStamp);
         hashMap.put("pTitle", title);
         hashMap.put("pDescr", description);
         hashMap.put("pImage", "noImage");
         hashMap.put("pTime", timesStamp);
         hashMap.put("pScore", 0);
         hashMap.put("pLikes", 0);
         hashMap.put("pDislikes", 0);
         hashMap.put("pComments", 0);


         DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts1");


         ref.child(timesStamp).setValue(hashMap)
         .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void unused) {
        pd.dismiss();
        Toast.makeText(AddPostSocialActivity.this,"Post published", Toast.LENGTH_SHORT).show();


        titleEt.setText("");
        descriptionEt.setText("");
        imageIv.setImageURI(null);
        image_rui = null;

        Intent intent = new Intent(AddPostSocialActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        if (mInterstitialAd != null) {
        mInterstitialAd.show(AddPostSocialActivity.this);
        } else {

        }

        }
        })
         .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        Toast.makeText(AddPostSocialActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

        pd.dismiss();

        }
        });

         }**/
    }

    private void showImagePickDialog() {



        String [] option = {"Camera","Gallery"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_post);
        builder.setIcon(R.drawable.ic_round_add_a_photo_24);


        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0){

                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }

                }
                if (which==1){

                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }

                }

            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent, PICK_FILE);
    }

    private void pickFromCamera() {

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE || requestCode == RESULT_OK ||
                data != null || data.getData() != null ){

            selectedUri = data.getData();
            if (selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageIv);
                imageIv.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            }else if(selectedUri.toString().contains("video")){
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageIv.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";

            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }




    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

            uid = user.getUid();

        }else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_social, menu);

        menu.findItem(R.id.action_searsh).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "camera & Storage both permissions are neccessary...", Toast.LENGTH_SHORT).show();

                    }
                }
                else{

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Storage permissions are neccessary...", Toast.LENGTH_SHORT).show();

                    }

                }else{

                }
            }
            break;
        }
    }

/**
 @Override
 protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
 if(resultCode == RESULT_OK ){
 if(requestCode == IMAGE_PICK_GALLERY_CODE){
 image_rui = data.getData();

 imageIv.setImageURI(image_rui);
 }
 else if(requestCode == IMAGE_PICK_CAMERA_CODE) {


 imageIv.setImageURI(image_rui);

 }

 }



 super.onActivityResult(requestCode, resultCode, data);
 }**/
}
