package com.talos.weaver.qalambook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.talos.weaver.AdaptaterBook.AdaptaterCategory;
import com.talos.weaver.MainActivity;
import com.talos.weaver.ModelBook.ModelCategory;
import com.talos.weaver.PdfAddActivity;
import com.talos.weaver.R;


import java.util.ArrayList;

public class QalamDashboardActivity extends AppCompatActivity {




    //private ActivityQalamDashboardBinding binding;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelCategory> categoryArrayList;
    private AdaptaterCategory adaptaterCategory;
    ImageButton back;
    Button addCategory;
    TextView subtitle;
    RecyclerView categories;
    EditText searshEt;
    FloatingActionButton pdfAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qalam_dashboard);
        //binding = ActivityQalamDashboardBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategories();
        back = findViewById(R.id.backBtn);
        addCategory = findViewById(R.id.addCategorybtn);
        subtitle = findViewById(R.id.subtitleTv);
        categories = findViewById(R.id.categoriesRv);
        searshEt = findViewById(R.id.search);
        pdfAdd = findViewById(R.id.addPdfFab);








      back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QalamDashboardActivity.this, CategoriesAddActivity.class);
                startActivity(intent);
            }
        });

       pdfAdd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(QalamDashboardActivity.this, PdfAddActivity.class));
           }
       });

       searshEt.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }



           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

               try{
                   adaptaterCategory.getFilter().filter(s);

               }
               catch (Exception e){

               }


           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

    }

    private void loadCategories() {

        categoryArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    categoryArrayList.add(model);
                }

               adaptaterCategory = new AdaptaterCategory(QalamDashboardActivity.this, categoryArrayList);

               categories.setAdapter(adaptaterCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else{
            String name = firebaseUser.getEmail();

           // subtitle.setText(name);
        }


    }


}