package com.example.studentregisterwithfirbase;

public class ReadWriteDetails {

    public  String fullName, dob, gender, mobile;

    public ReadWriteDetails(String textDoB, String textGender, String textMobile) {

        this.dob = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
