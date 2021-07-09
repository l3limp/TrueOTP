package com.example.trueotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignedIn extends AppCompatActivity {

    TextView phoneNumber;
    Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        setPhoneNumber();
        findViews();

        signOut.setOnClickListener(v-> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SignedIn.this, MainActivity.class));
            finish();
        });

    }

    private void findViews() {
        phoneNumber = findViewById(R.id.phoneNumberSignedIn);
        signOut = findViewById(R.id.signOutButton);
    }
    
    private void setPhoneNumber() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try{
            assert user != null;
            phoneNumber.setText(user.getPhoneNumber());
        }
        catch (Exception e) {
            Toast.makeText(SignedIn.this, "Could Not Retrieve Phone Number", Toast.LENGTH_SHORT).show();
        }
    }
}