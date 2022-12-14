package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Deferred extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    TextView defDonor, deftext, lasttext;
    Button btnDeferred;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deferred);

        btnDeferred = findViewById(R.id.btnDeferred);
        defDonor = findViewById(R.id.defDonor);
        deftext = findViewById(R.id.deftext);
        lasttext = findViewById(R.id.lasttext);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        Intent i = getIntent();
        String reason = i.getStringExtra("Reason");
        String status = i.getStringExtra("Status");
        reference.child("users")
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        defDonor.setText("Sorry, "+ user.firstName+",");
                        if(status.equals("Temporary Deferred"))
                        {
                            deftext.setText(status);
                            decisonMaking(reason);
                        } else
                        {
                           if(status.equals("Permanently Deferred"))
                           {
                               deftext.setText(status);
                               decisonMaking(reason);
                           }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btnDeferred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Deferred.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
                finish();
                return;
            }
        });
    }

    public void decisonMaking(String reason){

        if(reason.contains("Tattoo"))
        {
            lasttext.setText("The reason you been temporary deffered is : " + reason + "\n Which mean you can't donate blood right now. " + "You will wait 365 days before you can donate blood.");
        }

        if(reason.contains("Root Canal") | reason.contains("Tooth Surgery"))
        {
            lasttext.setText("The reason you been temporary deffered is : " + reason + "\n Which mean you can't donate blood right now.." + "You will wait 3 days before you can donate blood.");
        }

        if(reason.contains("Hemoglobin failed") | reason.contains("failed test in hemoglobin"))
        {
            lasttext.setText("The reason you been temporary deffered is : " + reason + "\n Which mean you can't donate blood right now." + "You will wait 3 Months before you can donate blood.");
        }

        if(reason.contains("Below 50kgs") | reason.contains("Failed Body Weight"))
        {
            lasttext.setText("The reason you been temporary deffered is : " + reason + "\n Which mean you can't donate blood right now." + "You will wait 3 Months and gain 50ks up before you can donate blood.");
        }

        if(reason.contains("High Blood") | reason.contains("Blood Pressure"))
        {
            lasttext.setText("The reason you been temporary deffered is : " + reason + "\n Which mean you can't donate blood right now." + "You will wait 3 Months and gain normal blood pressure before you can donate blood.");
        }

        if(reason.contains("Below 50 bpm") | reason.contains("Low bpm"))
        {
            lasttext.setText("The reason you been temporary deffered is : " + reason + "\n Which mean you can't donate blood right now." + "You will wait 3 Months and BPM before you can donate blood.");
        }

        if(reason.contains("Gonorrhea Positive") | reason.contains("Syphilis"))
        {
            lasttext.setText("The reason you been temporary deffered is : " + reason + "\n Which mean you can't donate blood right now." + "You will wait 3 Months and treat your Syphilis/Gonorrhea/other-STD before you can donate blood.");
        }

        if(reason.contains("Cancer") | reason.contains("Blood Diseases") | reason.contains("HIV Positive") | reason.contains("Heart Problems"))
        {
            lasttext.setText("The reason you been permanently deffered is : " + reason + "\n Which mean you can't donate blood permanently.");
        }
    }
}