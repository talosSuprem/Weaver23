package com.talos.weaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.talos.weaver.Adapter.AdapterChat;
import com.talos.weaver.Model.ModelChat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.sankai.crypters.Notifications.Data;

public class ChatSocialActivity extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv,back;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;




    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String hisUid;
    String myUid;
    String hisImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_social);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recyclerView);
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatSocialActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");


        Query userQuery = usersDbRef.orderByChild("id").equalTo(hisUid);

        userQuery.addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    String name =""+ ds.child("username").getValue();
                    hisImage = ""+ds.child("imageurl").getValue();
                    String typingStatus = ""+ ds.child("typingTo").getValue();

                    if(typingStatus.equals(myUid)){
                        userStatusTv.setText("typing...");
                    }else{
                        String onlineStatus = ""+ds.child("onlineStatus").getValue();
                        if (onlineStatus.equals("online")){
                            userStatusTv.setText(onlineStatus);
                        }else{
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);

                            cal.setTimeInMillis(Long.parseLong(onlineStatus));

                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            userStatusTv.setText("Last seen at:"+dateTime);
                        }

                    }






                    nameTv.setText(name);

                    try{
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_person_24).into(profileIv);


                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_person_24).into(profileIv);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEt.getText().toString().trim();

                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatSocialActivity.this, "Connot send the empty message...", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
            }
        });

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().trim().length() ==0){
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readMessage();

        seenMessage();

    }

    private void seenMessage() {

        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatSocialActivity.this, chatList, hisImage);
                    adapterChat.notifyDataSetChanged();

                    recyclerView.setAdapter(adapterChat);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hashMap);


        messageEt.setText("");
    }


    private void checkUserStatus() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

            myUid = user.getUid();

        }else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);

        dbRef.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);

        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");

        super.onStart();
    }

    @Override
    protected void onPause() {
        String timestamp = String.valueOf(System.currentTimeMillis());

        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");

        super.onResume();
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
}