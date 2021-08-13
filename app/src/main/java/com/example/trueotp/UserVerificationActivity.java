package com.example.trueotp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UserVerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verification);

        findViewById(R.id.btnStart).setOnClickListener(startClickListener);
        mAuth = FirebaseAuth.getInstance();

        initTruecallerSDK();
    }

    private final ITrueCallback sdkCallback = new ITrueCallback() {
        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
//            Toast.makeText(UserVerificationActivity.this.getApplicationContext(),
//                    "Verified Truecaller User: " + trueProfile.firstName,
//                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserVerificationActivity.this,SignedIn.class));
        }
//        public void updateUI(FirebaseUser account){
//
//            if(account != null){
//                Toast.makeText(UserVerificationActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(UserVerificationActivity.this,SignedIn.class));
//            }else {
//                Toast.makeText(UserVerificationActivity.this,"couldnt sign in",Toast.LENGTH_LONG).show();
//            }
//        }



            @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
            Toast.makeText(UserVerificationActivity.this.getApplicationContext(), "Failed to retrieve profile: " + trueError.getErrorType(), Toast
                    .LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationRequired(final TrueError trueError) {
            Toast.makeText(UserVerificationActivity.this.getApplicationContext(),
                    "Verification Required",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void initTruecallerSDK() {
        TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(this, sdkCallback)
                .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
                .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
                .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
                .build();
        TruecallerSDK.init(trueScope);

        findViewById(R.id.btnStart).setVisibility(TruecallerSDK.getInstance().isUsable() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TruecallerSDK.getInstance().onActivityResultObtained(this, requestCode, resultCode, data);
    }

    private final View.OnClickListener startClickListener = view -> {
        try {
            TruecallerSDK.getInstance().getUserProfile(UserVerificationActivity.this);
        } catch (Exception e) {
            Toast.makeText(UserVerificationActivity.this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TruecallerSDK.clear();
    }
}