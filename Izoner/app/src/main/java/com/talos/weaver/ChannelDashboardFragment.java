package com.talos.weaver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.talos.weaver.AdapterTube.ViewPagerAdapter;
import com.talos.weaver.Dashboard.AboutDashboard;
import com.talos.weaver.Dashboard.HomeDashboard;
import com.talos.weaver.Dashboard.PlaylistDashboard;
import com.talos.weaver.Dashboard.SubscriptionsDashboard;
import com.talos.weaver.Dashboard.VideosDashboard;

import com.talos.weaver.R;


public class ChannelDashboardFragment extends Fragment {


    TextView user_channel_name;
    ViewPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;


    public ChannelDashboardFragment() {
        // Required empty public constructor
    }

    public static ChannelDashboardFragment newInstance() {
        ChannelDashboardFragment fragment = new ChannelDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_dashboard, container, false);

       user_channel_name = view.findViewById(R.id.user_channel_name);
       tabLayout = view.findViewById(R.id.tabLayout);
       viewPager = view.findViewById(R.id.viewPager);

       initAdapter();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Channels");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("channel_name").getValue().toString();
                    user_channel_name.setText(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }

    private void initAdapter() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.add(new HomeDashboard(),"Home");
        adapter.add(new VideosDashboard(), "Videos");
        adapter.add(new PlaylistDashboard(), "Playlists");
        adapter.add(new SubscriptionsDashboard(), "Subscription");
        adapter.add(new AboutDashboard(), "About");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}