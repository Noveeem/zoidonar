package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

public class Archived extends AppCompatActivity {


    RecyclerView aView;
    ArrayList<modelHistory> list;
    HistoryAdapter adapter;


    TextView txtNORecord;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtNORecord = findViewById(R.id.txtNORecord);

        aView = findViewById(R.id.aView);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        list = new ArrayList<>();
        aView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, list);
        aView.setAdapter(adapter);

        reference.child("users_history").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(!snapshot.exists())
              {
                  txtNORecord.setText("NO RECORD");
              } else
              {
                  if(snapshot.exists())
                  {
                      for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                          modelHistory modelHistory = dataSnapshot.getValue(modelHistory.class);
                          list.add(modelHistory);
                      }
                      adapter.notifyDataSetChanged();
                  }
              }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, History.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }
}