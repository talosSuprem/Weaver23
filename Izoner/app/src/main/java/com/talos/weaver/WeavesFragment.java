package com.talos.weaver;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talos.weaver.Adapter.AdapterPostsSocial;
import com.talos.weaver.Model.ModelPost;

import java.util.ArrayList;
import java.util.List;


public class WeavesFragment extends Fragment {



    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPostsSocial adapterPostsSocial;


    public WeavesFragment() {


        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        //Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_weaves, container, false);



      firebaseAuth = FirebaseAuth.getInstance();

      recyclerView = view.findViewById(R.id.postsRecyclerview);

      LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

      layoutManager.setStackFromEnd(true);
      layoutManager.setReverseLayout(true);

      recyclerView.setLayoutManager(layoutManager);


        postList = new ArrayList<>();

        loadPosts();



      return view;
    }

    private void loadPosts() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts1");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    adapterPostsSocial = new AdapterPostsSocial(getActivity(), postList);

                    recyclerView.setAdapter(adapterPostsSocial);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void searchPosts(String searchQuery){



    }

    private void checkUserStatus() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

           // myUid = user.getUid();

        }else{
            //startActivity(new Intent(this, MainActivity.class));
            //finish();
        }
    }



}