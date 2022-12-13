package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class History extends AppCompatActivity {

    TextView txtDonorName, txtViewHistory;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtDonorName = findViewById(R.id.txtDonorName);
        txtViewHistory = findViewById(R.id.txtViewHistory);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("users").child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (currentUser != null)
                        {
                            txtDonorName.setText(user.firstName + " " + user.middleName + " " + user.lastName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        txtViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                archivedView();
            }
        });
    }
    public void archivedView(){
        Intent i = new Intent(this, Archived.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }
}