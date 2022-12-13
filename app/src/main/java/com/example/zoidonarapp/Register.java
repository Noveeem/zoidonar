package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register extends AppCompatActivity implements View.OnClickListener {

    String[] genderList = {"Male", "Female", "Prefer not say"};

    TextInputLayout[] layouts;
    TextInputEditText[] editTexts;
    TextInputEditText middleName;
    TextInputLayout Glayout, MNLayout;
    AutoCompleteTextView Gender;
    ArrayAdapter<String> adapterItems;
    Button btnContinue;

    private String finalMiddlename;
    private int age;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnContinue = findViewById(R.id.btnContinue);
        Gender = findViewById(R.id.Gender);
        adapterItems = new ArrayAdapter<String>(this, R.layout.gender_list, genderList);
        Gender.setAdapter(adapterItems);
        Glayout = (TextInputLayout) findViewById(R.id.GLayout);
        MNLayout = (TextInputLayout) findViewById(R.id.MNLayout);
        middleName = (TextInputEditText) findViewById(R.id.middleName);




        layouts = new TextInputLayout[]
                {
                        (TextInputLayout) findViewById(R.id.FNLayout),
                        (TextInputLayout) findViewById(R.id.LNLayout),
                        (TextInputLayout) findViewById(R.id.MLayout),
                        (TextInputLayout) findViewById(R.id.DOBLayout),
                };

        editTexts = new TextInputEditText[]{
                (TextInputEditText) findViewById(R.id.firstName),
                (TextInputEditText) findViewById(R.id.lastName),
                (TextInputEditText) findViewById(R.id.phoneNumber),
                (TextInputEditText) findViewById(R.id.dateBirth)
        };



        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        editTexts[3].setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        if (currentUser !=null)
        {
            finish();
            return;
        }
    }
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnContinue:
                if(!isFieldEmpty() | !nameLength())
                {
                    return;
                } else {
                    if (!checkAge() | !selectGender())
                    {return;}
                }

                String[] input = {
                  editTexts[0].getText().toString(),
                  editTexts[1].getText().toString(),
                  editTexts[2].getText().toString(),
                  editTexts[3].getText().toString()
                };

                if(middleName.getText().toString().isEmpty())
                {
                    finalMiddlename = "";
                } else
                {
                    finalMiddlename = middleName.getText().toString();
                }

                String g = Gender.getText().toString();
                String a = Integer.toString(age);
                Intent i = new Intent(this, final_register.class);
                i.putExtra("firstName", input[0]);
                i.putExtra("lastName", input[1]);
                i.putExtra("middleName", finalMiddlename);
                i.putExtra("phoneNumber", input[2]);
                i.putExtra("birthDate", input[3]);
                i.putExtra("gender", g);
                i.putExtra("age", a);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;
            case R.id.dateBirth:
                //Birth Date
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String birth = month+"/"+dayOfMonth+"/"+year;
                        editTexts[3].setText(birth);

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH, day);
                        age = calculateAge(c.getTimeInMillis());

                    }
                }, year, month, day);
                dialog.show();
                break;
        }
    }


    public boolean selectGender(){

        String select = Gender.getText().toString();
        if(select.isEmpty())
        {
            Glayout.setError("Please select gender");
            requestFocus(Gender);
            return false;
        } else
        {
            Glayout.setErrorEnabled(false);
        }

        return true;
    }


    public boolean isFieldEmpty(){

        for (int i = 0; i < 4; i++) {
            if(editTexts[i].getText().toString().isEmpty())
            {
                layouts[i].setError("Field can't be empty.");
                requestFocus(editTexts[i]);
                return false;
            } else {
                layouts[i].setErrorEnabled(false);
            }
        }

        return true;
    }

    public boolean nameLength(){
        String fname = editTexts[0].getText().toString().trim();
        String lname = editTexts[1].getText().toString().trim();
        if(fname.length() < 3)
        {
            layouts[0].setError("First name is too short.");
            requestFocus(editTexts[0]);
            return false;
        } else {
            if(lname.length() < 2)
            {
                layouts[1].setError("First name is too short.");
                requestFocus(editTexts[1]);
                return false;
            } else
            {
                layouts[1].setErrorEnabled(false);
            }
            layouts[0].setErrorEnabled(false);
        }
        return true;
    }

    public boolean checkAge(){
        if(age < 16)
        {
            layouts[3].setError("Below 16 is not allowed");
            requestFocus(editTexts[3]);
            return false;
        } else
        {
            layouts[3].setErrorEnabled(false);
        }

        return true;
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

    public void requestFocus(View view){
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}