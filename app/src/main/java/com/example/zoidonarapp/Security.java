package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Security extends AppCompatActivity {

    TextInputEditText NewPassword, ConfirmPassword, CurrentPassword;
    TextInputLayout NPLayout, CPLayout, CurrentLayout;
    Button btnChangePassword;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = fAuth.getCurrentUser();

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CurrentPassword = (TextInputEditText) findViewById(R.id.CurrentPassword);
        NewPassword = (TextInputEditText) findViewById(R.id.NewPassword);
        ConfirmPassword = (TextInputEditText) findViewById(R.id.ConfirmPassword);

        CurrentLayout = (TextInputLayout) findViewById(R.id.CurrentLayout);
        NPLayout = (TextInputLayout) findViewById(R.id.NPLayout);
        CPLayout = (TextInputLayout) findViewById(R.id.CPLayout);

        btnChangePassword = findViewById(R.id.btnChangePassword);

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentPassword = CurrentPassword.getText().toString().trim();
                final String newPassword = NewPassword.getText().toString().trim();
                final String confirmPassword = ConfirmPassword.getText().toString().trim();

                if(!validateNewPassword()){
                    return;
                } else {
                    if (!isPasswordMatch(newPassword, confirmPassword))
                    {
                        return;
                    }
                }

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.emailAddress, currentPassword);
                        disabled();
                        currentUser.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            currentUser.updatePassword(confirmPassword)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                clear();
                                                                enabled();
                                                                alertSuccess();
                                                            } else {
                                                                alertFail();
                                                                enabled();
                                                            }
                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            CurrentLayout.setError("Invalid Password.");
                                            requestFocus(CurrentPassword);
                                            enabled();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }

    public void alertFail(){
        View alertCustomDialog = LayoutInflater.from(Security.this).inflate(R.layout.error, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Security.this);
        alertDialog.setView(alertCustomDialog);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Oops! Something went wrong.");

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void alertSuccess(){
        View alertCustomDialog = LayoutInflater.from(Security.this).inflate(R.layout.success_two, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Security.this);
        alertDialog.setView(alertCustomDialog);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView alertText = dialog.findViewById(R.id.alertText);
        alertText.setText("Password changed successfully!");

    }


    public void disabled(){
        CurrentPassword.setEnabled(false);
        NewPassword.setEnabled(false);
        ConfirmPassword.setEnabled(false);
    }

    public void clear(){
        CurrentPassword.getText().clear();
        NewPassword.getText().clear();
        ConfirmPassword.getText().clear();
    }
    public void enabled(){
        CurrentPassword.setEnabled(true);
        NewPassword.setEnabled(true);
        ConfirmPassword.setEnabled(true);
    }


    public boolean validateNewPassword(){
        String new_password = NewPassword.getText().toString().trim();
        String confirm_password = ConfirmPassword.getText().toString().trim();
        String current_password = CurrentPassword.getText().toString().trim();

        //Check if empty

        if (current_password.isEmpty()){
            CurrentLayout.setError("Field can't be empty.");
            requestFocus(CurrentPassword);
            return false;
        } else {
            CurrentLayout.setErrorEnabled(false);
        }

        if (new_password.isEmpty())
        {
            NPLayout.setError("Field can't be empty.");
            requestFocus(NewPassword);
            return false;
        } else {
            //Validate password length
            //More than 8 Characters
            if (new_password.length() < 8) {
                NPLayout.setError("Password must be more than 8 characters.");
                requestFocus(NewPassword);
                return false;
            }

            NPLayout.setErrorEnabled(false);
        }

        if (confirm_password.isEmpty()){
            CPLayout.setError("Field can't be empty.");
            requestFocus(ConfirmPassword);
            return false;
        } else {
            CPLayout.setErrorEnabled(false);
        }


        return true;
    }

    public boolean isPasswordMatch(String password, String confirmPassword){
        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirmPassword);
        if (!matcher.matches()){
            CPLayout.setError("Password & Confirm Password are not match.");
            requestFocus(ConfirmPassword);
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