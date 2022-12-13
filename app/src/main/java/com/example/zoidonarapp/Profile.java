package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {

    TextInputLayout[] layouts;
    TextInputEditText[] editText;
    TextInputEditText txtMName;
    TextInputLayout MNameLayout;
    TextView txtFullName;
    Button btnUpdate;

    private int age;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtMName = (TextInputEditText) findViewById(R.id.txtMName);
        MNameLayout = (TextInputLayout) findViewById(R.id.MNameLayout);

        layouts = new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.FNameLayout),
                (TextInputLayout) findViewById(R.id.LNameLayout),
                (TextInputLayout) findViewById(R.id.EmailLayout),
                (TextInputLayout) findViewById(R.id.PNumberLayout),
                (TextInputLayout) findViewById(R.id.DBirthLayout)
        };

        editText = new TextInputEditText[]{
                (TextInputEditText) findViewById(R.id.txtFName),
                (TextInputEditText) findViewById(R.id.txtLName),
                (TextInputEditText) findViewById(R.id.txtEmail),
                (TextInputEditText) findViewById(R.id.txtPNumber),
                (TextInputEditText) findViewById(R.id.txtDob),
        };

        btnUpdate = findViewById(R.id.btnUpdate);
        txtFullName = findViewById(R.id.txtFullName);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("users").child(currentUser.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if(currentUser != null)
                                {
                                    txtFullName.setText(user.firstName + " " + user.middleName + " " + user.lastName);
                                    editText[0].setText(user.firstName);
                                    txtMName.setText(user.middleName);
                                    editText[1].setText(user.lastName);
                                    age = user.age;
                                    editText[2].setText(user.emailAddress);
                                    editText[3].setText(user.phoneNumber);
                                    editText[4].setText(user.birthDate);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Profile.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

        disabled();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFieldEmpty() | !nameLength() | !isvalidAddress())
                {
                    return;
                } else
                {
                    if(!checkAge())
                    {
                        return;
                    }
                }
                updateChanges();
            }
        });
    }

    public void updateChanges(){
        String firstName = editText[0].getText().toString().trim();
        String lastName = editText[1].getText().toString().trim();
        String emailAddress = editText[2].getText().toString().trim();
        String phoneNumber = editText[3].getText().toString().trim();
        String dateBirth = editText[4].getText().toString();
        String middleName = txtMName.getText().toString();

        Update update = new Update(firstName, lastName, middleName, emailAddress, phoneNumber, dateBirth, age);
        Map<String, Object> ups = update.toUpdate();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(currentUser.getUid());

        currentUser.updateEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            reference.updateChildren(ups)
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(Profile.this, "Update Changes Successfully.", Toast.LENGTH_SHORT).show();
                                                disabled();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void selectDate(){

        //Birth Date
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        editText[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] currentDate = editText[4].getText().toString().trim().split("/");
                int currentMonth = Integer.valueOf(currentDate[0]);
                int currentDay = Integer.valueOf(currentDate[1]);
                int currentYear = Integer.valueOf(currentDate[2]);

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = month+"/"+dayOfMonth+"/"+year;
                        editText[4].setText(date);

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH, day);
                        age = calculateAge(c.getTimeInMillis());

                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(Profile.this, dateSetListener, currentYear, currentDay, currentMonth);
                datePickerDialog.show();
            }
        });
    }

    public boolean checkAge(){
        if(age < 16)
        {
            layouts[4].setError("Below 16 is not allowed");
            requestFocus(editText[4]);
            return false;
        } else
        {
            layouts[3].setErrorEnabled(false);
        }

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case 16908332:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
                break;
            case 2131296892:
                enabled();
                selectDate();
                break;
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.tabs, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int calculateAge(long date){
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if(today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH))
        {
            age--;
        }
        return age;
    }

    public void disabled()
    {
        MNameLayout.setEnabled(false);
        btnUpdate.setVisibility(View.GONE);
        for (int i = 0; i < layouts.length; i++) {
            layouts[i].setEnabled(false);
        }
    }

    public void enabled()
    {
        MNameLayout.setEnabled(true);
        btnUpdate.setVisibility(View.VISIBLE);
        for (int i = 0; i < layouts.length; i++) {
            layouts[i].setEnabled(true);
        }
    }
    public boolean isFieldEmpty(){
        for (int i = 0; i < 5; i++) {
            if(editText[i].getText().toString().isEmpty())
            {
                layouts[i].setError("Field can't be empty.");
                requestFocus(editText[i]);
                return false;
            } else
            {
                layouts[i].setErrorEnabled(false);
            }
        }
        return true;
    }

    public boolean nameLength(){
        String fname = editText[0].getText().toString().trim();
        String lname = editText[1].getText().toString().trim();
        if(fname.length() < 3)
        {
            layouts[0].setError("First name is too short.");
            requestFocus(editText[0]);
            return false;
        } else {
            if(lname.length() < 2)
            {
                layouts[1].setError("First name is too short.");
                requestFocus(editText[1]);
                return false;
            } else
            {
                layouts[1].setErrorEnabled(false);
            }
            layouts[0].setErrorEnabled(false);
        }
        return true;
    }
    public boolean isvalidAddress(){
        String email = editText[2].getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            layouts[2].setError("Please provide valid email address.");
            requestFocus(editText[2]);
            return false;
        } else
        {
            layouts[2].setErrorEnabled(false);
        }
        return true;
    }


    public void requestFocus(View view){
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}