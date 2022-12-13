package com.example.zoidonarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PE extends AppCompatActivity {

    String[] bloodtype = {"AB-", "AB+", "O+", "O-", "B+", "B-", "A-", "A+"};
    String[] hemoglogin = {"Passed", "Failed"};

    TextInputLayout[] Playouts;
    TextInputLayout BTLayout, HGLayout;

    TextInputEditText[] PeditText;

    AutoCompleteTextView Bloodtype, Hemoglobin;
    ArrayAdapter<String> adapterItems;

    Button btnPeSubmit;
    RadioGroup rg;

    private String eventDate;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnPeSubmit = findViewById(R.id.btnPeSubmit);
        rg = findViewById(R.id.Rem);
        BTLayout = (TextInputLayout) findViewById(R.id.BTLayout);
        HGLayout = (TextInputLayout) findViewById(R.id.HGLayout);
        Bloodtype = findViewById(R.id.BloodType);
        Hemoglobin = findViewById(R.id.Hemoglobin);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        adapterItems = new ArrayAdapter<String>(this, R.layout.gender_list, bloodtype);
        Bloodtype.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this, R.layout.gender_list, hemoglogin);
        Hemoglobin.setAdapter(adapterItems);




        Playouts = new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.BWLayout),
                (TextInputLayout) findViewById(R.id.BPLayout),
                (TextInputLayout) findViewById(R.id.PRLayout),
                (TextInputLayout) findViewById(R.id.TLayout),
                (TextInputLayout) findViewById(R.id.GALayout),
                (TextInputLayout) findViewById(R.id.SLayout),
                (TextInputLayout) findViewById(R.id.HLayout),
                (TextInputLayout) findViewById(R.id.HLLayout),
                (TextInputLayout) findViewById(R.id.VLayout),
                (TextInputLayout) findViewById(R.id.RSNLayout),
                (TextInputLayout) findViewById(R.id.BBOLayout)
        };

        PeditText = new TextInputEditText[]{
                (TextInputEditText) findViewById(R.id.BWeight),
                (TextInputEditText) findViewById(R.id.BPresure),
                (TextInputEditText) findViewById(R.id.Pulse),
                (TextInputEditText) findViewById(R.id.Temp),
                (TextInputEditText) findViewById(R.id.GA),
                (TextInputEditText) findViewById(R.id.Skin),
                (TextInputEditText) findViewById(R.id.H),
                (TextInputEditText) findViewById(R.id.HL),
                (TextInputEditText) findViewById(R.id.Vol),
                (TextInputEditText) findViewById(R.id.Reason),
                (TextInputEditText) findViewById(R.id.BBOName)
        };

        btnPeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty())
                {
                    return;
                } else
                {
                    if(!checkRadio())
                    {
                        return;
                    } else
                    {
                        if(!isTest())
                        {
                            return;
                        }
                    }
                }



            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MultiStep.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }

    public boolean checkRadio(){

        if (rg.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Please select a choices on remarks", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public boolean isEmpty(){
        for (int i = 0; i < 11; i++) {
            if(PeditText[i].getText().toString().trim().isEmpty())
            {
                Playouts[i].setError("Field can't be empty.");
                requestFocus(PeditText[i]);
                return false;
            } else
            {
                Playouts[i].setErrorEnabled(false);
            }
        }
        return true;
    }

    public boolean isTest(){

        String bloodtype = Bloodtype.getText().toString();
        String hemoglobin = Hemoglobin.getText().toString();

        if(bloodtype.isEmpty())
        {
            BTLayout.setError("Please select blood type");
            requestFocus(Bloodtype);
            return false;
        } else
        {
            if(hemoglobin.isEmpty())
            {
                HGLayout.setError("Please select blood type");
                requestFocus(Hemoglobin);
                return false;
            } else
            {
                HGLayout.setErrorEnabled(false);
            }
            BTLayout.setErrorEnabled(false);
        }

        return true;
    }

    public void requestFocus(View view){
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}