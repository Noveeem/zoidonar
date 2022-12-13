package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btnCreate, btnLogin;
    TextInputEditText txtEmail, txtPassword;
    TextInputLayout ELayout, PLayout;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnCreate = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);

        ELayout = (TextInputLayout) findViewById(R.id.ELayout);
        PLayout = (TextInputLayout) findViewById(R.id.PLayout);

        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() !=null){
            Intent main = new Intent(Login.this, MainActivity.class);
            startActivity(main);
            finish();
            return;
        }

        btnLogin.setOnClickListener(this);
        btnCreate.setOnClickListener(this);


    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnSignup:
                Intent register = new Intent(this, Register.class);
                startActivity(register);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.btnLogin:
                if(!validateForm())
                {
                    return;
                }
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                disabled();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            reload();
                                        }
                                    }, 2000);
                                    alertSuccess();
                                } else {
                                    alertFail();
                                    enabled();
                                }
                            }
                        });
                break;
        }
    }

    public void reload(){
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void disabled(){
        ELayout.setEnabled(false);
        PLayout.setEnabled(false);
        btnLogin.setEnabled(false);
        btnCreate.setEnabled(false);
    }

    public void enabled(){
        ELayout.setEnabled(true);
        PLayout.setEnabled(true);
        btnLogin.setEnabled(true);
        btnCreate.setEnabled(true);
    }

    public void alertSuccess(){
        View alertCustomDialog = LayoutInflater.from(Login.this).inflate(R.layout.success, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setView(alertCustomDialog);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void alertFail(){
        View alertCustomDialog = LayoutInflater.from(Login.this).inflate(R.layout.error, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        alertDialog.setView(alertCustomDialog);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public boolean validateForm(){
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (email.isEmpty()){
            ELayout.setError("Field can't be empty.");
            requestFocus(txtEmail);
            return false;
        } else {

            if(password.isEmpty()){
                PLayout.setError("Field can't be empty.");
                requestFocus(txtPassword);
                return false;
            } else {
                PLayout.setErrorEnabled(false);
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                ELayout.setError("Please provide valid email address.");
                requestFocus(txtEmail);
                return false;
            } else {
                ELayout.setErrorEnabled(false);
            }
        }
        return true;
    }

    public void requestFocus(View view){
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}