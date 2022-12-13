package com.example.zoidonarapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Update {
    public String firstName, lastName, middleName, emailAddress, phoneNumber, birthDate;
    public int age;

    public Update() {

    }

    public Update(String firstName, String lastName, String middleName, String emailAddress, String phoneNumber, String birthDate, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.age = age;
    }

    @Exclude
    public Map<String, Object> toUpdate(){
        HashMap<String, Object> input = new HashMap<>();
        input.put("firstName", firstName);
        input.put("middleName", middleName);
        input.put("lastName", lastName);
        input.put("emailAddress", emailAddress);
        input.put("phoneNumber", phoneNumber);
        input.put("birthDate", birthDate);
        input.put("age", age);

        return input;
    }
}
