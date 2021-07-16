package com.example.trueotp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private EditText edtPhone;
    public static PhoneAuthProvider.ForceResendingToken token;
    private static final int REQ_USER_CONSENT = 200;
    SMSBroadcastReceiver smsBroadcastReceiver;
    String otpFromFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        startSmartUserConsent();

        edtPhone = findViewById(R.id.idEdtPhoneNumber);
        Button generateOTPBtn = findViewById(R.id.idBtnGetOtp);
        Button verifyPhNum = findViewById(R.id.phNumVerify);

        if(otpFromFirebase!=null){
            Bundle otpBundle = new Bundle();
            otpBundle.putString("OTPtext", otpFromFirebase);
            OTPFragment fragobj = new OTPFragment();
            fragobj.setArguments(otpBundle);
        }

        generateOTPBtn.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                Toast.makeText(MainActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            } else {
                String phoneNum = "+91" + edtPhone.getText().toString();
                sendVerificationCode(phoneNum);
            }

        });
        verifyPhNum.setOnClickListener(V-> {
                startActivity(new Intent(MainActivity.this, UserVerificationActivity.class));
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

        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            token = forceResendingToken;

            edtPhone.setVisibility(View.GONE);
            findViewById(R.id.imageView).setVisibility(View.GONE);
            findViewById(R.id.idBtnGetOtp).setVisibility(View.GONE);
            findViewById(R.id.textView).setVisibility(View.GONE);
            findViewById(R.id.phNumVerify).setVisibility(View.GONE);

            OTPFragment fragment = new OTPFragment();
            Bundle args = new Bundle();
            args.putString("phoneNumber", edtPhone.getText().toString());
            args.putString("verificationID", s);
            fragment.setArguments(args);
            fragment.setEnterTransition(new Slide(Gravity.END).setDuration(200));

            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.constraintLayout, fragment)
                            .commit(), 100);
        }
    };

    private void startSmartUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SMSBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener = new SMSBroadcastReceiver.SMSBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, REQ_USER_CONSENT);
            }

            @Override
            public void onFailure() {

            }
        };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_USER_CONSENT) {
            if((resultCode== MainActivity.RESULT_OK) && (data!=null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        otpFromFirebase = message.substring(0, 6);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }
}
