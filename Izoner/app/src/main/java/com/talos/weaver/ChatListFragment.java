package com.talos.weaver;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talos.weaver.Adapter.AdapterChatlist;
import com.talos.weaver.Model.ModelChat;
import com.talos.weaver.Model.ModelChatlist;
import com.talos.weaver.Model.User;

import java.util.ArrayList;
import java.util.List;


public class ChatListFragment extends Fragment {


    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatlist> chatlistList;
    List<User>  userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;


    public ChatListFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.recyclerView);

        chatlistList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlistList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistList.add(chatlist);
                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    for(ModelChatlist chatlist: chatlistList){
                        if(user.getId() != null && user.getId().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }

                    adapterChatlist = new AdapterChatlist(getContext(), userList);

                    recyclerView.setAdapter(adapterChatlist);

                    for(int i=0; i<userList.size(); i++){
                        lastMassage(userList.get(i).getId());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void lastMassage(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChat  chat = ds.getValue(ModelChat.class);
                    if (chat==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender == null || receiver == null){
                        continue;
                    }
                    if(chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(userId) ||
                    chat.getReceiver().equals(userId)  &&
                    chat.getSender().equals(currentUser.getUid())){
                        theLastMessage = chat.getMessage();

                    }
                }
                adapterChatlist.setLastMessageMap(userId, theLastMessage);
                adapterChatlist.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

        }else{
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }




}