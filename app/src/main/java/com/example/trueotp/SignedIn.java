package com.example.trueotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class SignedIn extends AppCompatActivity {

    Button signOut;
    VideoView vid;
    MediaController mediaController;
    FloatingActionButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        vid = findViewById(R.id.videoView);
        signOut = findViewById(R.id.signOutButton);
        FAB = findViewById(R.id.play);

        vid.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.confirm);
        setMediaController();

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        FAB.setOnClickListener(V-> {
            if(vid.isPlaying()) {
                vid.pause();
                FAB.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            else if(!vid.isPlaying()){
                vid.start();
                FAB.setImageResource(R.drawable.ic_baseline_pause_24);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            }
        });
        signOut.setOnClickListener(v-> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SignedIn.this, MainActivity.class));
            finish();
        });
    }
    void setMediaController() {
        mediaController = new MediaController(this);
        mediaController.setAnchorView(vid);
        vid.setMediaController(mediaController);
        vid.start();
    }
}