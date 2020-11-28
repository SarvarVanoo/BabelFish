package com.example.textextraction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    //Declare variables

    private static int SPLASH_DURATION_IN_MILLISEC = 4000;
    //Animation variables
    Animation topAnimation, bottomAnimation;

    ImageView splashLogo;
    TextView appName, appMotto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        topAnimation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.bottom_animation);
        splashLogo =(ImageView)findViewById(R.id.babel_fish_splash_image);
        appName = (TextView)findViewById(R.id.babel_fish_splash_text);
        appMotto = (TextView)findViewById(R.id.babel_fish_splash_text_motto);

        // Link Animations
        splashLogo.setAnimation(topAnimation);
        appName.setAnimation(bottomAnimation);
        appMotto.setAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DURATION_IN_MILLISEC);
    }
}
