package com.talos.weaver.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.talos.weaver.R;


public class SubscriptionsDashboard extends Fragment {



    public SubscriptionsDashboard() {
        // Required empty public constructor
    }
    public static SubscriptionsDashboard newInstance() {
        SubscriptionsDashboard fragment = new SubscriptionsDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dashboard_subscriptions_tube, container, false);
    }
}