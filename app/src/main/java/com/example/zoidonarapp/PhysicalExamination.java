package com.example.zoidonarapp;

public class PhysicalExamination {
    public String Body_Weight, Blood_Pressure, Pulse_Rate, Temperature, General_Appearance, HEENT, Hearts_and_Lungs, Remarks, Blood_Type, Hemoglobin;

    public PhysicalExamination() {

    }

    public PhysicalExamination(String body_Weight, String blood_Pressure, String pulse_Rate, String temperature, String general_Appearance, String HEENT, String hearts_and_Lungs, String remarks, String blood_Type, String hemoglobin) {        Body_Weight = body_Weight;
        Blood_Pressure = blood_Pressure;
        Pulse_Rate = pulse_Rate;
        Temperature = temperature;
        General_Appearance = general_Appearance;
        this.HEENT = HEENT;
        Hearts_and_Lungs = hearts_and_Lungs;
        Remarks = remarks;
        Blood_Type = blood_Type;
        Hemoglobin = hemoglobin;
    }
}
