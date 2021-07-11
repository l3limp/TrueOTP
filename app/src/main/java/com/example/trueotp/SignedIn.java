package com.example.trueotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SignedIn extends AppCompatActivity {

    Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        signOut = findViewById(R.id.signOutButton);

        signOut.setOnClickListener(v-> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SignedIn.this, MainActivity.class));
            finish();
        });

    }
}