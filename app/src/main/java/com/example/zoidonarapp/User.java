package com.example.zoidonarapp;

public class User {
    public String user_type, firstName, lastName, middleName, emailAddress, phoneNumber, birthDate, gender;
    public int age;

    public User() {

    }

    public User(String user_type, String firstName, String lastName, String middleName, String emailAddress, String phoneNumber, String birthDate, String gender, int age) {
        this.user_type = user_type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.age = age;
    }
}
