package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PE extends AppCompatActivity {

    String[] bloodtype = {"AB-", "AB+", "O+", "O-", "B+", "B-", "A-", "A+"};
    String[] hemoglobin = {"Passed", "Failed"};
    String[] remarks = {"Accepted", "Temporary Deferred", "Permanently Deferred"};

    TextInputLayout[] Playouts;
    TextInputLayout BTLayout, HGLayout, RMLayout;

    TextInputEditText[] PeditText;

    AutoCompleteTextView Bloodtype, Hemoglobin, Remarks;
    ArrayAdapter<String> adapterItems;

    Dialog dialog;

    Button btnPEContinue, btnPEModify, btnPESubmit, btnKey;
    EditText etKey;

    SharedPreferences sharedPreferences;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_BODY_WEIGTH = "BodyWeight";
    public static final String KEY_BLOOD_PRESSURE = "BloodPressure";
    public static final String KEY_PULSE_RATE = "Pulse";
    public static final String KEY_TEMP = "Temp";
    public static final String KEY_GA = "GA";
    public static final String KEY_HEENT = "HEE=ENT";
    public static final String KEY_HL = "HL";

    public static final String KEY_REMARKS = "Remarks";

    public static final String KEY_BLOOD_TYPE = "BloodType";
    public static final String KEY_HEMOGLOBIN = "Hemoglobin";

    public static final String KEY_VOLUME = "Vol";
    public static final String KEY_REASON = "Reason";
    public static final String KEY_BBO = "BBO";

    private String BWeight, BPressure, PRate, Temp, Ga, Heent, HLungs, Rem, BType, Haemoglobin, Volume, Reason, BBO;
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

        btnPEContinue = findViewById(R.id.btnPEContinue);
        btnPEModify = findViewById(R.id.btnPEModify);
        btnPESubmit = findViewById(R.id.btnPESubmit);

        RMLayout = (TextInputLayout) findViewById(R.id.RMLayout);
        BTLayout = (TextInputLayout) findViewById(R.id.BTLayout);
        HGLayout = (TextInputLayout) findViewById(R.id.HGLayout);
        Bloodtype = findViewById(R.id.BloodType);
        Hemoglobin = findViewById(R.id.Hemoglobin);
        Remarks = findViewById(R.id.Remarks);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        adapterItems = new ArrayAdapter<String>(this, R.layout.gender_list, bloodtype);
        Bloodtype.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this, R.layout.gender_list, hemoglobin);
        Hemoglobin.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this, R.layout.gender_list, remarks);
        Remarks.setAdapter(adapterItems);

        btnPEModify.setVisibility(View.GONE);
        btnPESubmit.setVisibility(View.GONE);

        Playouts = new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.BWLayout),
                (TextInputLayout) findViewById(R.id.BPLayout),
                (TextInputLayout) findViewById(R.id.PRLayout),
                (TextInputLayout) findViewById(R.id.TLayout),
                (TextInputLayout) findViewById(R.id.GALayout),
                (TextInputLayout) findViewById(R.id.HELayout),
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
                (TextInputEditText) findViewById(R.id.HEENT),
                (TextInputEditText) findViewById(R.id.HL),
                (TextInputEditText) findViewById(R.id.Vol),
                (TextInputEditText) findViewById(R.id.Reason),
                (TextInputEditText) findViewById(R.id.BBOName)
        };


        //Dialog Alert
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.verify);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.modal_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        btnKey = dialog.findViewById(R.id.btnKey);
        etKey = dialog.findViewById(R.id.etKey);



        btnPEContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isTest())
                {
                    return;
                } else
                {
                    if(!isReason())
                    {
                        return;
                    }
                }


                disabled();
                btnPEContinue.setVisibility(View.GONE);
                btnPEModify.setVisibility(View.VISIBLE);
                btnPESubmit.setVisibility(View.VISIBLE);
                Toast.makeText(PE.this, "Please double check the form before to submit.", Toast.LENGTH_SHORT).show();
            }
        });

        btnPEModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enabled();
                btnPEContinue.setVisibility(View.VISIBLE);
                btnPEModify.setVisibility(View.GONE);
                btnPESubmit.setVisibility(View.GONE);
            }
        });

        btnPESubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        btnKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = etKey.getText().toString();
                etKey.getText().clear();
                reference.child("adminAccount").child("admin")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                adminKey susi = snapshot.getValue(adminKey.class);
                                String susiValue = susi.password;
                                if(susiValue.equals(key)){
                                    String result = Remarks.getText().toString().trim();
                                    switch (result){
                                        case "Accepted":
                                            showAccepted();
                                            Intent accepted = new Intent(PE.this, Accepted.class);
                                            startActivity(accepted);
                                            overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
                                            break;
                                        case "Temporary Deferred":
                                            showTempDeffered();
                                            break;
                                        case "Permanently Deferred":
                                            showPermaDeffered();
                                            break;
                                    }
                                } else
                                {
                                    if(!susiValue.equals(key))
                                    {
                                        Toast.makeText(PE.this, "Invalid Key, try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MultiStep.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }

    public void showAccepted(){

        String bw = PeditText[0].getText().toString().trim();
        String bp = PeditText[1].getText().toString().trim();
        String pr = PeditText[2].getText().toString().trim();
        String temp = PeditText[3].getText().toString().trim();
        String ga = PeditText[4].getText().toString().trim();
        String hnt = PeditText[5].getText().toString().trim();
        String heartlungs = PeditText[6].getText().toString().trim();
        String remarks = Remarks.getText().toString().trim();
        String bt = Bloodtype.getText().toString().trim();
        String hemoglobin = Hemoglobin.getText().toString().trim();
        String volume = PeditText[7].getText().toString().trim();
        String bbo = PeditText[9].getText().toString().trim();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("Switch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventSwitchConfig eventSwitchConfig = snapshot.getValue(eventSwitchConfig.class);
                eventDate = eventSwitchConfig.eventStatusBaseDate;

                PhysicalExamination physicalExamination = new PhysicalExamination(bw, bp, pr, temp, ga, hnt, heartlungs, remarks, bt, hemoglobin);
                reference.child("users_physical_examination").child(currentUser.getUid()).child(eventDate)
                        .setValue(physicalExamination);
                int vol = Integer.parseInt(volume);

                reference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        String usertype = user.user_type;
                        HashMap<String, Object> toMap = new HashMap<>();
                        toMap.put("user_type", user.user_type);
                        toMap.put("firstName", user.firstName);
                        toMap.put("lastName", user.lastName);
                        toMap.put("middleName", user.middleName);
                        toMap.put("Bloodtype", bt);
                        toMap.put("Volume", volume);
                        toMap.put("type", "STUDENT");
                        reference.child("archive").child(eventDate).child(currentUser.getUid()).setValue(toMap);
                        reference.child("Events").child(eventDate).child(usertype).child(currentUser.getUid()).setValue(toMap);

                        HashMap bloodmap = new HashMap();
                        bloodmap.put("Donor_ID", currentUser.getUid());
                        reference.child("Report").child(eventDate).child("Qualified").child("BloodType").child(bt).push().setValue(bloodmap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                HashMap<String, Object> bloodtypeHash = new HashMap<>();
                bloodtypeHash.put("Blood_Type", bt);
                reference.child("users_blood_type").child(currentUser.getUid()).setValue(bloodtypeHash);

                modelHistory modelHistory = new modelHistory(bbo, eventDate, volume, remarks);

                reference.child("users_history").child(currentUser.getUid()).child(eventDate).setValue(modelHistory);

                HashMap<String, Object> userStatus = new HashMap<>();
                userStatus.put("Status", remarks);
                reference.child("users_status").child(currentUser.getUid()).setValue(userStatus);

                reference.child("users_donated").child(currentUser.getUid()).child(eventDate)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {

                                } else
                                {
                                    if(!snapshot.exists())
                                    {
                                        DonorDonate donorDonate = new DonorDonate(1, vol);
                                        reference.child("users_donated").child(currentUser.getUid()).child(eventDate).setValue(donorDonate);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showTempDeffered(){
        String bw = PeditText[0].getText().toString().trim();
        String bp = PeditText[1].getText().toString().trim();
        String pr = PeditText[2].getText().toString().trim();
        String temp = PeditText[3].getText().toString().trim();
        String ga = PeditText[4].getText().toString().trim();
        String hnt = PeditText[5].getText().toString().trim();
        String heartlungs = PeditText[6].getText().toString().trim();
        String remarks = Remarks.getText().toString().trim();
        String bt = Bloodtype.getText().toString().trim();
        String hemoglobin = Hemoglobin.getText().toString().trim();
        String volume = PeditText[7].getText().toString().trim();
        String bbo = PeditText[9].getText().toString().trim();
        String reason = PeditText[8].getText().toString();


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("Switch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventSwitchConfig eventSwitchConfig = snapshot.getValue(eventSwitchConfig.class);
                eventDate = eventSwitchConfig.eventStatusBaseDate;

                PhysicalExamination physicalExamination = new PhysicalExamination(bw, bp, pr, temp, ga, hnt, heartlungs, remarks, bt, hemoglobin);
                reference.child("users_physical_examination").child(currentUser.getUid()).child(eventDate)
                        .setValue(physicalExamination);
                int vol = Integer.parseInt(volume);
                HashMap<String, Object> toMap = new HashMap<>();
                toMap.put("Bloodtype", bt);
                toMap.put("Volume", volume);
                toMap.put("type", "STUDENT");
                reference.child("archive").child(eventDate).child(currentUser.getUid()).setValue(toMap);

                HashMap<String, Object> bloodtypeHash = new HashMap<>();
                bloodtypeHash.put("Blood_Type", bt);
                reference.child("users_blood_type").child(currentUser.getUid()).setValue(bloodtypeHash);

                modelHistory modelHistory = new modelHistory(bbo, eventDate, volume, remarks);

                reference.child("users_history").child(currentUser.getUid()).child(eventDate).setValue(modelHistory);

                HashMap<String, Object> userStatus = new HashMap<>();
                userStatus.put("Status", remarks);
                reference.child("users_status").child(currentUser.getUid()).setValue(userStatus);

                Intent def = new Intent(PE.this, Deferred.class);
                def.putExtra("Reason", reason);
                def.putExtra("Status", remarks);
                startActivity(def);
                overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showPermaDeffered(){
        String bw = PeditText[0].getText().toString().trim();
        String bp = PeditText[1].getText().toString().trim();
        String pr = PeditText[2].getText().toString().trim();
        String temp = PeditText[3].getText().toString().trim();
        String ga = PeditText[4].getText().toString().trim();
        String hnt = PeditText[5].getText().toString().trim();
        String heartlungs = PeditText[6].getText().toString().trim();
        String remarks = Remarks.getText().toString().trim();
        String bt = Bloodtype.getText().toString().trim();
        String hemoglobin = Hemoglobin.getText().toString().trim();
        String volume = PeditText[7].getText().toString().trim();
        String bbo = PeditText[9].getText().toString().trim();
        String reason = PeditText[8].getText().toString();


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("Switch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventSwitchConfig eventSwitchConfig = snapshot.getValue(eventSwitchConfig.class);
                eventDate = eventSwitchConfig.eventStatusBaseDate;

                PhysicalExamination physicalExamination = new PhysicalExamination(bw, bp, pr, temp, ga, hnt, heartlungs, remarks, bt, hemoglobin);
                reference.child("users_physical_examination").child(currentUser.getUid()).child(eventDate)
                        .setValue(physicalExamination);
                int vol;
                if(volume.isEmpty())
                {
                     vol = 0;
                } else
                {
                        vol = Integer.parseInt(volume);
                }

                HashMap<String, Object> userStatus = new HashMap<>();
                userStatus.put("Status", remarks);


                reference.child("users_status").child(currentUser.getUid()).setValue(userStatus);

                Intent def = new Intent(PE.this, Deferred.class);
                def.putExtra("Reason", reason);
                def.putExtra("Status", remarks);
                startActivity(def);
                overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void saveSharedPrefs(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_BODY_WEIGTH, PeditText[0].getText().toString().trim());
        editor.putString(KEY_BLOOD_PRESSURE, PeditText[1].getText().toString().trim());
        editor.putString(KEY_PULSE_RATE, PeditText[2].getText().toString().trim());
        editor.putString(KEY_TEMP, PeditText[3].getText().toString().trim());
        editor.putString(KEY_GA, PeditText[4].getText().toString().trim());
        editor.putString(KEY_HEENT, PeditText[5].getText().toString().trim());
        editor.putString(KEY_HL, PeditText[6].getText().toString().trim());
        editor.putString(KEY_REMARKS, Remarks.getText().toString().trim());
        editor.putString(KEY_BLOOD_TYPE, Bloodtype.getText().toString().trim());
        editor.putString(KEY_HEMOGLOBIN, Hemoglobin.getText().toString().trim());
        editor.putString(KEY_VOLUME, PeditText[7].getText().toString().trim());
        editor.putString(KEY_REASON, PeditText[8].getText().toString().trim());
        editor.putString(KEY_BBO, PeditText[9].getText().toString().trim());

        editor.apply();

        Intent i = new Intent(this, PESummary.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void loadSharedPrefs(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        BWeight = sharedPreferences.getString(KEY_BODY_WEIGTH, "");
        BPressure = sharedPreferences.getString(KEY_BLOOD_PRESSURE, "");
        PRate = sharedPreferences.getString(KEY_PULSE_RATE, "");
        Temp = sharedPreferences.getString(KEY_TEMP, "");
        Ga = sharedPreferences.getString(KEY_GA, "");
        Heent = sharedPreferences.getString(KEY_HEENT, "");
        HLungs = sharedPreferences.getString(KEY_HL, "");
        Rem = sharedPreferences.getString(KEY_REMARKS, "");
        BType = sharedPreferences.getString(KEY_BLOOD_TYPE, "");
        Haemoglobin = sharedPreferences.getString(KEY_HEMOGLOBIN, "");
        Volume = sharedPreferences.getString(KEY_VOLUME, "");
        Reason = sharedPreferences.getString(KEY_REASON, "");
        BBO = sharedPreferences.getString(KEY_BBO, "");
    }

    public void updateViewSharePrefs(){
        PeditText[0].setText(BWeight);
        PeditText[1].setText(BPressure);
        PeditText[2].setText(PRate);
        PeditText[3].setText(Temp);
        PeditText[4].setText(Ga);
        PeditText[5].setText(HLungs);
        Remarks.setText(Rem);
        Bloodtype.setText(BType);
        Hemoglobin.setText(Haemoglobin);
        PeditText[6].setText(Volume);
        PeditText[7].setText(Reason);
        PeditText[8].setText(BBO);
    }

    public void disabled(){
        for (int i = 0; i < 10; i++) {
            Playouts[i].setEnabled(false);
        }
        RMLayout.setEnabled(false);
        BTLayout.setEnabled(false);
        HGLayout.setEnabled(false);
    }

    public void enabled(){
        for (int i = 0; i < 10; i++) {
            Playouts[i].setEnabled(true);
        }
        RMLayout.setEnabled(true);
        BTLayout.setEnabled(true);
        HGLayout.setEnabled(true);
    }


    public boolean isReason(){

        if(PeditText[8].getText().toString().trim().isEmpty())
        {
            Playouts[8].setError("Field can't be empty.");
            requestFocus(PeditText[8]);
            return false;
        }
        else
        {
            Playouts[8].setErrorEnabled(false);
        }
        return true;
    }

    public boolean isTest(){

        String remarks = Remarks.getText().toString();
        if(remarks.isEmpty())
        {
            RMLayout.setError("Please select blood type");
            requestFocus(Remarks);
            return false;
        } else
        {
            RMLayout.setErrorEnabled(false);
        }

        return true;
    }


    public void requestFocus(View view){
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}