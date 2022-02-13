package com.talos.weaver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talos.weaver.Adapter.AdapterNotification;
import com.talos.weaver.Model.ModelNotification;

import java.util.ArrayList;


public class NotificationSocialFragment extends Fragment {
    RecyclerView notificationRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelNotification> notificationsList;

    private AdapterNotification adapterNotification;

    public NotificationSocialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_social, container, false);

        notificationRv = view.findViewById(R.id.notificationsRv);

        firebaseAuth = FirebaseAuth.getInstance();

        getAllNotification();




        return view;
    }

    private void getAllNotification() {
        notificationsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationsList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelNotification model = ds.getValue(ModelNotification.class);

                            notificationsList.add(model);
                        }

                        adapterNotification = new AdapterNotification(getActivity(), notificationsList);

                        notificationRv.setAdapter(adapterNotification);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




    }
}