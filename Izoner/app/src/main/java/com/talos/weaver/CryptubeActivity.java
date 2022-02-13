package com.talos.weaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;


import com.talos.weaver.FragmentsCryptotubes.ExploreTubeFragment;
import com.talos.weaver.FragmentsCryptotubes.HomeTubeFragment;
import com.talos.weaver.FragmentsCryptotubes.LibraryTubeFragment;

import com.talos.weaver.FragmentsCryptotubes.SubscriptionTubeFragment;



public class CryptubeActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    ImageView user_profile_image,returnh;
    AppBarLayout appBarLayout;


    Fragment fragment;

    GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 100;
    private static final int PERMISSION = 101;
    private static final int PICK_VIDEO = 102;




    FirebaseAuth auth;
    FirebaseUser user;

    Uri videoUri;
    MediaController mediaController;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptube);

        toolbar = findViewById(R.id.toolbartube);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");




        bottomNavigationView = findViewById(R.id.btm_cr);
        frameLayout = findViewById(R.id.frame_layouttube);
        appBarLayout = findViewById(R.id.appBar);

        //auth = FirebaseAuth.getInstance();
        //user = auth.getCurrentUser();

        user_profile_image = findViewById(R.id.user_profile_image);
        //getProfileImage();


        returnh = findViewById(R.id.returnhomeicon);
        returnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CryptubeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();




        checkPermission();


        GoogleSignInOptions gsc = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("77585142121-6v4b30hr2ehubugmo66ls6bn4rjhii40.apps.googleusercontent.com")
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(CryptubeActivity.this, gsc);

        showFragment();





        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.hometube:
                        HomeTubeFragment homeTubeFragment = new HomeTubeFragment();
                        selectedFragment(homeTubeFragment);
                        break;

                    case R.id.explore:
                        ExploreTubeFragment exploreTubeFragment = new ExploreTubeFragment();
                        selectedFragment(exploreTubeFragment);
                        break;
                    case R.id.publish:
                        showPublishContentDialogue();
                        break;
                    case R.id.subscription:
                        SubscriptionTubeFragment subscriptionTubeFragment =new SubscriptionTubeFragment();
                        selectedFragment(subscriptionTubeFragment);
                        break;

                    case R.id.library:
                        LibraryTubeFragment libraryTubeFragment = new LibraryTubeFragment();
                        selectedFragment(libraryTubeFragment);
                        break;


                }
                return false;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.hometube);



        user_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (user != null){
                    startActivity(new Intent(CryptubeActivity.this, AccountActivity.class));
                    //getProfileImage();

                    Toast.makeText(CryptubeActivity.this, "User Already Sign In", Toast.LENGTH_LONG).show();
                }else{
                    user_profile_image.setImageResource(R.drawable.ic_person_24);

                    showDialogue();
                }


            }
        });

    }

    private void showPublishContentDialogue() {
        Dialog dialog = new Dialog(CryptubeActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_dialogue);
        dialog.setCanceledOnTouchOutside(true);


        TextView txt_make_post = dialog.findViewById(R.id.txt_publish_post);
        TextView txt_poll = dialog.findViewById(R.id.txt_release_poll);

        TextView txt_upload_video = dialog.findViewById(R.id.txt_upload_video);
        txt_upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO);

            }
        });


        dialog.show();
    }


    private void showDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CryptubeActivity.this);
        builder.setCancelable(true);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_signin_dialog, viewGroup , false);
        builder.setView(view);

        TextView txt_google_signIn = view.findViewById(R.id.txt_google_sign);
        txt_google_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        builder.create().show();

        
    }

    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RC_SIGN_IN:
                if(resultCode == RESULT_OK && data != null){
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try{
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("username" , account.getDisplayName());
                                    map.put("email" , account.getEmail());
                                    map.put("profile" , String.valueOf(account.getPhotoUrl()));
                                    map.put("uid" , firebaseUser.getUid());
                                    map.put("search" , account.getDisplayName().toLowerCase());

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cryptubeusers");
                                    reference.child(firebaseUser.getUid()).setValue(map);


                                }else{
                                    Toast.makeText(CryptubeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                    catch (Exception e){
                        Toast.makeText(this, e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                }
            case PICK_VIDEO:
                if (resultCode == RESULT_OK && data != null){
                    videoUri = data.getData();
                    Intent intent = new Intent(CryptubeActivity.this, PublishContentActivity.class);
                    //intent.putExtra("type", "video");
                    intent.setData(videoUri);
                    startActivity(intent);
                }






        }



    }


    private void selectedFragment(Fragment fragment) {


        setStatusBarColor("#FFFFF");


        appBarLayout.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layouttube, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.notification:
                Toast.makeText(CryptubeActivity.this, "notifications", Toast.LENGTH_LONG).show();
                break;

            case R.id.search:
                Toast.makeText(CryptubeActivity.this, "search", Toast.LENGTH_LONG ).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }
   private void getProfileImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String p = snapshot.child("imageurl").getValue().toString();

                    Picasso.get().load(p).placeholder(R.drawable.ic_person_24)
                            .into(user_profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CryptubeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showFragment(){
        String type = getIntent().getStringExtra("type");
        if (type != null){
            switch (type){
                case "channel":
                    setStatusBarColor("#99FF0000");
                    appBarLayout.setVisibility(View.GONE);
                    //
                    fragment = ChannelDashboardFragment.newInstance();
                    break;

            }
            if (fragment != null ){
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_layouttube, fragment).commit();
            }else{
                Toast.makeText(this, "Somethings went wrong", Toast.LENGTH_SHORT).show();
            }





        }

    }

    @SuppressLint("NewApi")
    private void setStatusBarColor(String color){
        Window  window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));

    }

    private void checkPermission(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(CryptubeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION);
        } else{
            Log.d("tag","checkpermission: Permission granted");
        }

    }




}