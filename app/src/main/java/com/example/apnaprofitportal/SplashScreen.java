package com.example.apnaprofitportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    ImageView logo, splashImg;
    TextView appName;
    LottieAnimationView lottieAnimationView;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_splash_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        logo = findViewById(R.id.splash_logo);
        splashImg = findViewById(R.id.splash_img);
        lottieAnimationView = findViewById(R.id.lottie);

//        splashImg.animate().translationY(-1600).setDuration(1000).setStartDelay(1000);
//        logo.animate().translationY(-1600).setDuration(1000).setStartDelay(1000);
//        lottieAnimationView.animate().translationY(-1600).setDuration(1000).setStartDelay(1000);
        new Handler().postDelayed(runnable, 1000);
    }
}