package com.talos.weaver.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.talos.weaver.R;


public class PlaylistDashboard extends Fragment {



    public PlaylistDashboard() {
        // Required empty public constructor
    }
    public static PlaylistDashboard newInstance() {
        PlaylistDashboard fragment = new PlaylistDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dashboard_playlist_tube, container, false);
    }
}