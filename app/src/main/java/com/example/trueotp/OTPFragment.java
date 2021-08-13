package com.example.trueotp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.truecaller.android.sdk.TruecallerSDK;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPFragment extends Fragment {

    private FirebaseAuth mAuth;
    EditText otp;
    View root;
    public static PhoneAuthProvider.ForceResendingToken token;
//        private static final int REQ_USER_CONSENT = 200;
//    SMSBroadcastReceiver smsBroadcastReceiver;
//    String otpFromFirebase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_otp, container, false);
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState) {
        super.onViewCreated(view, savedInstancesState);

        otp = root.findViewById(R.id.idEdtOtp);
        mAuth = FirebaseAuth.getInstance();
//        startSmartUserConsent();

//        assert getArguments() != null;
//        String OTPtext = getArguments().getString("OTPtext");
//        otp.setText(OTPtext);

//        otp.setText(otpFromFirebase);

        Bundle bundle = this.getArguments();
        root.findViewById(R.id.idBtnVerify).setOnClickListener(v -> {
            if (!otp.getText().toString().isEmpty()) {
                assert bundle != null;
                String verID = bundle.getString("verificationID");
                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(verID,
                                otp.getText().toString());

                Toast.makeText(getContext(), credential.getSmsCode(), Toast.LENGTH_SHORT).show();
                signInWithCredential(credential);

            } else
                Toast.makeText(requireActivity(),
                        "Please enter OTP", Toast.LENGTH_LONG).show();
        });

        setCodeNotReceived();
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) { }

        @SuppressLint("SetTextI18n")
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) { }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    void setCodeNotReceived() {
        TextView codeNotReceived = root.findViewById(R.id.resendOTP);
        codeNotReceived.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                resendVerificationCode();
                Toast.makeText(requireActivity(),
                        "Resent OTP", Toast.LENGTH_SHORT).show();
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                codeNotReceived.setTextColor(Color.LTGRAY);
                return true;
            }
            return false;
        });
    }

    private void resendVerificationCode() {
        assert getArguments() != null;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Objects.requireNonNull(requireArguments()
                        .getString("phoneNumber")),        // Phone number to verify
                30,                       // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                requireActivity(),               // Activity (for callback binding)
                mCallbacks,                      // OnVerificationStateChangedCallbacks
                MainActivity.token);      // ForceResendingToken from callbacks
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                        Intent i = new Intent(getActivity(), SignedIn.class);
                        startActivity(i);
                });
    }

//        private void startSmartUserConsent() {
//        SmsRetrieverClient client = SmsRetriever.getClient(requireActivity());
//        client.startSmsUserConsent(null);
//    }
//
//    private void registerBroadcastReceiver() {
//        smsBroadcastReceiver = new SMSBroadcastReceiver();
//        smsBroadcastReceiver.smsBroadcastReceiverListener = new SMSBroadcastReceiver.SMSBroadcastReceiverListener() {
//            @Override
//            public void onSuccess(Intent intent) {
//                requireActivity().startActivityForResult(intent, REQ_USER_CONSENT);
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//        };
//        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
//        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(smsBroadcastReceiver, intentFilter);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == REQ_USER_CONSENT) {
//            if((resultCode== MainActivity.RESULT_OK) && (data!=null)) {
//                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
//                getOtpFromMessage(message);
//            }
//        }
//    }
//
//    private void getOtpFromMessage(String message) {
//        otpFromFirebase = message.substring(0, 6);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        registerBroadcastReceiver();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(smsBroadcastReceiver);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TruecallerSDK.clear();
    }
}


