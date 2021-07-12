package com.example.trueotp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private EditText edtPhone;
    private String verificationId;
    public static PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        edtPhone = findViewById(R.id.idEdtPhoneNumber);
        Button generateOTPBtn = findViewById(R.id.idBtnGetOtp);

        generateOTPBtn.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                Toast.makeText(MainActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            } else {
                String phoneNum = "+91" + edtPhone.getText().toString();
                sendVerificationCode(phoneNum);
            }

        });
    }

    private void sendVerificationCode(String phNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phNumber,
                60,
                TimeUnit.SECONDS,
                MainActivity.this,
                mCallBack
        );
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if (e instanceof FirebaseAuthInvalidCredentialsException)
                Toast.makeText(MainActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
            else if (e instanceof FirebaseNetworkException)
                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(MainActivity.this, "OTP Failed", Toast.LENGTH_SHORT).show();
            }
        }

        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            token = forceResendingToken;

            edtPhone.setVisibility(View.GONE);
            findViewById(R.id.imageView).setVisibility(View.GONE);
            findViewById(R.id.idBtnGetOtp).setVisibility(View.GONE);

            OTPFragment fragment = new OTPFragment();
            Bundle args = new Bundle();
            args.putString("phoneNumber", edtPhone.getText().toString());
            args.putString("verificationID", verificationId);
            fragment.setArguments(args);
            fragment.setEnterTransition(new Slide(Gravity.END).setDuration(200));


            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.constraintLayout, fragment)
                            .commit(), 100);
        }
    };

}
