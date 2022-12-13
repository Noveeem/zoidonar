package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MultiStep extends AppCompatActivity implements View.OnClickListener {

    Button[] btn;


    private String eventDate;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_step);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();



        btn = new Button[]{
            findViewById(R.id.btnQuestionnaire),
            findViewById(R.id.btnPE)
        };

        for (int i = 0; i < btn.length; i++) {
            btn[i].setOnClickListener(this);
        }

        btn[1].setEnabled(false);

        reference.child("Switch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventSwitchConfig eventSwitchConfig = snapshot.getValue(eventSwitchConfig.class);
                eventDate = eventSwitchConfig.eventStatusBaseDate;

                reference.child("users_medical_questionnaire").child(currentUser.getUid()).child(eventDate)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    btn[1].setEnabled(true);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MultiStep.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MultiStep.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnQuestionnaire:
                Questionnaire();
                break;
            case R.id.btnPE:
                Intent pe = new Intent(this, PE.class);
                startActivity(pe);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }

    public void Questionnaire(){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("Switch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventSwitchConfig eventSwitchConfig = snapshot.getValue(eventSwitchConfig.class);
                eventDate = eventSwitchConfig.eventStatusBaseDate;

                reference.child("users_medical_questionnaire").child(currentUser.getUid()).child(eventDate)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {

                                    Intent q = new Intent(MultiStep.this, HealthSummary.class);
                                    q.putExtra("eventDate", eventDate);
                                    startActivity(q);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                } else
                                {
                                    if(!snapshot.exists())
                                    {
                                        Intent q = new Intent(MultiStep.this, Questionnaire.class);
                                        startActivity(q);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MultiStep.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MultiStep.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void PhysicalExamination(){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("Switch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventSwitchConfig eventSwitchConfig = snapshot.getValue(eventSwitchConfig.class);
                eventDate = eventSwitchConfig.eventStatusBaseDate;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MultiStep.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}