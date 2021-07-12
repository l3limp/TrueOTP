package com.example.trueotp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPFragment extends Fragment {

    private FirebaseAuth mAuth;
    EditText otp;
    View root;
    public static PhoneAuthProvider.ForceResendingToken token;

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

        Bundle bundle = this.getArguments();
        root.findViewById(R.id.idBtnVerify).setOnClickListener(v -> {
            if (!otp.getText().toString().isEmpty()) {
                assert bundle != null;
                String verID = bundle.getString("verificationID");
                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(verID,
                                otp.getText().toString());

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
        TextView codeNotRecieved = root.findViewById(R.id.resendOTP);
        codeNotRecieved.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                resendVerificationCode();
                Toast.makeText(requireActivity(),
                        "Resent OTP", Toast.LENGTH_SHORT).show();
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                codeNotRecieved.setTextColor(Color.LTGRAY);
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
}


