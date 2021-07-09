package com.example.trueotp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button generateOTPBtn, signInBtn;
    EditText phoneNumberET, OTPET;
    String otp, phoneNumber;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        generateOTPBtn.setOnClickListener(v -> {

            phoneNumber = phoneNumberET.getText().toString();

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,                        // Phone number to be verified
                    60,                          // Timeout Duration
                    TimeUnit.SECONDS,                   // Timeout unit
                    MainActivity.this,           // Activity (for binding)
                    mCallback                           // callback on verification state changed
            );
        });

        StartFirebaseLogin();

        signInBtn.setOnClickListener(v -> {
            otp = OTPET.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
            SigninWithPhone(credential);
        });

    }

    private void findViews() {
        generateOTPBtn = findViewById(R.id.generateOTP);
        OTPET = findViewById(R.id.OTP);
        phoneNumberET = findViewById(R.id.phoneNumber);
        signInBtn = findViewById(R.id.verify);
    }

    private void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(MainActivity.this, "Verified!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(MainActivity.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(MainActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {
                        startActivity(new Intent(MainActivity.this, SignedIn.class));
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}