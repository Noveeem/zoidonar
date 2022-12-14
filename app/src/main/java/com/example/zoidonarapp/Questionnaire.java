package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Questionnaire extends AppCompatActivity {

    String[] questionList = {
            "Are you feeling well and in good health today?",
            "Is there any medicine taken today?",
            "Have you ever had problems with your heart or lungs?",
            "In the last 28 days, do you had COVID-19?",
            "Have you ever had a cancer?",
            "Have you ever had a positive test for the HIV/AIDS virus?",
            "In the last 3 months, have you had a vacinnation or tattoo?",
            "Do you have a blood disease?",
            "Have you had a relative diagnosed with Creutzfeldt-Jakob Disease? (or other types of Mad Cow's Disease)",
            "Have you had a diagnosis or had treatment for syphilis, gonorrhea or other STDs?",
            "Have you had any surgery or tooth extraction?"
    };
    TextView[] question;
    RadioGroup[] rg;
    Button btnSubmitForm, btnCancel, btnProceed;
    CheckBox checkBox;
    Dialog dialog;

    private String eventDate;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSubmitForm = findViewById(R.id.btnSubmitForm);
        checkBox = findViewById(R.id.checkBox);

        
        rg = new RadioGroup[]{
                (RadioGroup) findViewById(R.id.rg1),
                (RadioGroup) findViewById(R.id.rg2),
                (RadioGroup) findViewById(R.id.rg3),
                (RadioGroup) findViewById(R.id.rg4),
                (RadioGroup) findViewById(R.id.rg5),
                (RadioGroup) findViewById(R.id.rg6),
                (RadioGroup) findViewById(R.id.rg7),
                (RadioGroup) findViewById(R.id.rg8),
                (RadioGroup) findViewById(R.id.rg9),
                (RadioGroup) findViewById(R.id.rg10),
                (RadioGroup) findViewById(R.id.rg11)
        };

        question = new TextView[]
                {
                        findViewById(R.id.txtQ1),
                        findViewById(R.id.txtQ2),
                        findViewById(R.id.txtQ3),
                        findViewById(R.id.txtQ4),
                        findViewById(R.id.txtQ5),
                        findViewById(R.id.txtQ6),
                        findViewById(R.id.txtQ7),
                        findViewById(R.id.txtQ8),
                        findViewById(R.id.txtQ9),
                        findViewById(R.id.txtQ10),
                        findViewById(R.id.txtQ11)
                };

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("Switch")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        eventSwitchConfig eventSwitchConfig = snapshot.getValue(eventSwitchConfig.class);
                        eventDate = eventSwitchConfig.eventStatusBaseDate;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        for (int i = 0; i < 11; i++) {
            question[i].setText(questionList[i]);
        }

        //Dialog Alert
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.alertmessage);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.modal_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnProceed = dialog.findViewById(R.id.btnProceed);

        btnSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRadioChecked())
                {
                    return;
                } else
                {
                    if(!isCheckboxChecked())
                    {
                        return;
                    }
                }

                dialog.show();

            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int[] radioSelectID = {
                        rg[0].getCheckedRadioButtonId(),
                        rg[1].getCheckedRadioButtonId(),
                        rg[2].getCheckedRadioButtonId(),
                        rg[3].getCheckedRadioButtonId(),
                        rg[4].getCheckedRadioButtonId(),
                        rg[5].getCheckedRadioButtonId(),
                        rg[6].getCheckedRadioButtonId(),
                        rg[7].getCheckedRadioButtonId(),
                        rg[8].getCheckedRadioButtonId(),
                        rg[9].getCheckedRadioButtonId(),
                        rg[10].getCheckedRadioButtonId()
                };

                RadioButton[] radioButtons = {
                        findViewById(radioSelectID[0]),
                        findViewById(radioSelectID[1]),
                        findViewById(radioSelectID[2]),
                        findViewById(radioSelectID[3]),
                        findViewById(radioSelectID[4]),
                        findViewById(radioSelectID[5]),
                        findViewById(radioSelectID[6]),
                        findViewById(radioSelectID[7]),
                        findViewById(radioSelectID[8]),
                        findViewById(radioSelectID[9]),
                        findViewById(radioSelectID[10])
                };

                String[] ans = {
                        radioButtons[0].getText().toString(),
                        radioButtons[1].getText().toString(),
                        radioButtons[2].getText().toString(),
                        radioButtons[3].getText().toString(),
                        radioButtons[4].getText().toString(),
                        radioButtons[5].getText().toString(),
                        radioButtons[6].getText().toString(),
                        radioButtons[7].getText().toString(),
                        radioButtons[8].getText().toString(),
                        radioButtons[9].getText().toString(),
                        radioButtons[10].getText().toString()
                };

                for (int i = 0; i < 11; i++) {
                    modelQuestion modelQuestion = new modelQuestion(questionList[i], ans[i]);
                    
                    reference.child("users_medical_questionnaire").child(currentUser.getUid())
                            .child(eventDate)
                            .child("Question_" + i)
                            .setValue(modelQuestion)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent i = new Intent(Questionnaire.this, MultiStep.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
                                    }
                                }
                            });
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }



    public boolean isCheckboxChecked(){
        if(!checkBox.isChecked())
        {
            Toast.makeText(this, "Please click the checkbox.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean isRadioChecked(){

        for (int i = 0; i < 11; i++) {
            switch (rg[i].getCheckedRadioButtonId()){
                case -1:
                    Toast.makeText(this, "Please select a choices on question " + (i+1), Toast.LENGTH_SHORT).show();
                    return false;
            }
        }

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }
}