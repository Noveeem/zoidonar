package com.example.zoidonarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class Splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash_screen.this, Login.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        }, 2000);
    }
}