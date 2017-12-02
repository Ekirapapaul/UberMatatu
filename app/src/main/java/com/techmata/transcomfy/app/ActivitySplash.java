package com.techmata.transcomfy.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.techmata.transcomfy.app.auth.AccountActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ActivitySplash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.Fade;
        setContentView(R.layout.activity_splash);


        AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                    Intent i = new Intent();
                    i.setClass(ActivitySplash.this, AccountActivity.class);
                    startActivity(i);
                }
            }
        };

        splashTread.start();

        //check user login status
        /*SharedPreferences mSharedPreferences = getSharedPreferences("access_token", Context.MODE_PRIVATE);
        Intent intent;
        if (mSharedPreferences.getString("access_token",null)!= null){
            intent = new Intent(ActivitySplash.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            splashTread.start();
        }*/
    }
}
