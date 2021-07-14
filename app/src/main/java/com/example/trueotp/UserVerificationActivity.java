package com.example.trueotp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class UserVerificationActivity extends AppCompatActivity {

    private final ITrueCallback sdkCallback = new ITrueCallback() {
        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
            Toast.makeText(UserVerificationActivity.this.getApplicationContext(),
                    "Verified Truecaller User: " + trueProfile.firstName,
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserVerificationActivity.this, SignedIn.class));
        }

        @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
            Toast.makeText(UserVerificationActivity.this.getApplicationContext(), "onFailureProfileShared: " + trueError.getErrorType(), Toast
                    .LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationRequired(final TrueError trueError) {
            Toast.makeText(UserVerificationActivity.this.getApplicationContext(),
                    "Verification Required",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private final View.OnClickListener startClickListener = view -> {
        try {
            TruecallerSDK.getInstance().getUserProfile(UserVerificationActivity.this);
        } catch (Exception e) {
            Toast.makeText(UserVerificationActivity.this.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verification);

        findViewById(R.id.btnStart).setOnClickListener(startClickListener);

        initTruecallerSDK();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TruecallerSDK.clear();
    }
}