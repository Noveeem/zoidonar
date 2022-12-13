package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HealthSummary extends AppCompatActivity {

    RecyclerView rView;
    ArrayList<modelQuestion> list;
    HealthSummaryAdapter adapter;
    private String eventDate;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HealthSummary.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        rView = findViewById(R.id.rView);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users_medical_questionnaire");

        Intent i = getIntent();
        eventDate = i.getStringExtra("eventDate");

        list = new ArrayList<>();
        rView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HealthSummaryAdapter(this, list);
        rView.setAdapter(adapter);


            reference.child(currentUser.getUid()).child(eventDate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        modelQuestion modelQuestion = dataSnapshot  .getValue(modelQuestion.class);
                        list.add(modelQuestion);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MultiStep.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }
}