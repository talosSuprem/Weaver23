package com.talos.weaver.FragmentsCryptotubes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.talos.weaver.R;


public class SubscriptionTubeFragment extends Fragment {



    public SubscriptionTubeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscription_tube, container, false);
    }
}