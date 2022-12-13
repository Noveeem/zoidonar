package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnDonate, btnProfile, btnHistory, btnSecurity, btnLogout;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Intent relog = new Intent(this, Register.class);
            startActivity(relog);
            finish();
            return;
        }

        btnDonate = findViewById(R.id.btnDonate);
        btnHistory = findViewById(R.id.btnCard);
        btnProfile = findViewById(R.id.btnProfile);
        btnSecurity = findViewById(R.id.btnPassword);
        btnLogout = findViewById(R.id.btnLogout);



        btnDonate.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnSecurity.setOnClickListener(this);
        btnLogout.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnDonate:
                Intent ms = new Intent(this, MultiStep.class);
                startActivity(ms);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.btnCard:
                Intent h = new Intent(this, History.class);
                startActivity(h);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.btnProfile:
                Intent p = new Intent(this, Profile.class);
                startActivity(p);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.btnPassword:
                Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLogout:
                logout();
                break;
        }

    }

    public void logout(){
        auth.signOut();
        Intent logout = new Intent(this, Login.class);
        startActivity(logout);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        finish();
    }
}