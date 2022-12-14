package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class final_register extends AppCompatActivity {

    TextInputLayout RLEmail, RLPassword, RLCPassword;
    TextInputEditText regEmail, regPassword, regCPassword;
    Button btnCreate;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_register);


        //TextInputLayout
        RLEmail = (TextInputLayout) findViewById(R.id.RLEmail);
        RLPassword = (TextInputLayout) findViewById(R.id.RLPassword);
        RLCPassword = (TextInputLayout) findViewById(R.id.RLCPassword);

        //TextInputEditText
        regEmail = (TextInputEditText) findViewById(R.id.regEmail);
        regPassword = (TextInputEditText) findViewById(R.id.regPassword);
        regCPassword = (TextInputEditText) findViewById(R.id.regCPassword);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateForm()){
                    return;
                } else
                {
                    if (!isPasswordMatch())
                    {
                        return;
                    }
                }
                disabled();
                Intent i = getIntent();
                String user_type = i.getStringExtra("user_type");
                String firstName = i.getStringExtra("firstName");
                String lastName = i.getStringExtra("lastName");
                String middleName = i.getStringExtra("middleName");
                String phoneNumber = i.getStringExtra("phoneNumber");
                String birthDate = i.getStringExtra("birthDate");
                String gender = i.getStringExtra("gender");
                String age = i.getStringExtra("age");

                String email = regEmail.getText().toString().trim();
                String password = regCPassword.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(final_register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(user_type, firstName, lastName, middleName, email, phoneNumber, birthDate, gender, Integer.parseInt(age));
                                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    alertSuccess();
                                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            reload();
                                                        }
                                                    }, 2000);
                                                }
                                            });
                                } else {
                                    alertFail();
                                    enabled();
                                }
                            }
                        });
            }
        });

    }

    public void reload(){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        finish();
    }

    public void disabled(){
        RLEmail.setEnabled(false);
        RLPassword.setEnabled(false);
        RLCPassword.setEnabled(false);
    }

    public void enabled(){
        RLEmail.setEnabled(true);
        RLPassword.setEnabled(true);
        RLCPassword.setEnabled(true);
    }

    public boolean validateForm(){
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String confirm_password = regCPassword.getText().toString().trim();

        if (email.isEmpty()){
            RLEmail.setError("Field can't be empty.");
            requestFocus(regEmail);
            return false;
        } else {

            if(password.isEmpty()){
                RLPassword.setError("Field can't be empty.");
                requestFocus(regPassword);
                return false;
            } else {
                if (password.length() < 8){
                    RLPassword.setError("Password must be more than 8 characters.");
                    requestFocus(regPassword);
                    return false;
                } else {
                    if (confirm_password.isEmpty())
                    {
                        RLCPassword.setError("Field can't be empty!");
                        requestFocus(regCPassword);
                        return false;
                    } else {
                        RLCPassword.setErrorEnabled(false);
                    }
                    RLPassword.setErrorEnabled(false);
                }
                RLPassword.setErrorEnabled(false);
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                RLEmail.setError("Please provide valid email address.");
                requestFocus(regEmail);
                return false;
            } else {
                RLEmail.setErrorEnabled(false);
            }
        }
        return true;
    }

    public void alertSuccess(){
        View alertCustomDialog = LayoutInflater.from(final_register.this).inflate(R.layout.success, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(final_register.this);
        alertDialog.setView(alertCustomDialog);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Successfully created a new account!");
    }

    public void alertFail(){
        View alertCustomDialog = LayoutInflater.from(final_register.this).inflate(R.layout.error, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(final_register.this);
        alertDialog.setView(alertCustomDialog);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Ooops! Something went wrong.");

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public boolean isPasswordMatch(){
        String password = regPassword.getText().toString().trim();
        String confirm_password = regCPassword.getText().toString().trim();

        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirm_password);
        if (!matcher.matches()){
            RLCPassword.setError("Password & Confirm Password are not match.");
            requestFocus(regCPassword);
            return false;
        }
        return true;
    }

    public void requestFocus(View view){
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}