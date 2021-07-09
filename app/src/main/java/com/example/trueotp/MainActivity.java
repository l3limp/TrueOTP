package com.example.trueotp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button generateOTPBtn, signInBtn;
    EditText phoneNumber, OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

    }

    private void findViews() {
        generateOTPBtn = findViewById(R.id.generateOTP);
        OTP = findViewById(R.id.OTP);
        phoneNumber = findViewById(R.id.phoneNumber);
        signInBtn = findViewById(R.id.verify);
    }

}