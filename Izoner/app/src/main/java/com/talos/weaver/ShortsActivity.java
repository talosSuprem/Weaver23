package com.talos.weaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class ShortsActivity extends AppCompatActivity {

    FloatingActionButton addVideosBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorts);

        addVideosBtn = findViewById(R.id.addVideoFab);

        addVideosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShortsActivity.this, AddVideosActivity.class));
            }
        });
    }
}