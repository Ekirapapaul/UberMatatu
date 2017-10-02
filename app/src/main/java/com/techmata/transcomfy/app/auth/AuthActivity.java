package com.techmata.transcomfy.app.auth;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.techmata.transcomfy.app.R;

/**
 * Created by Sparks on 28/03/2016.
 */
public class AuthActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authentication);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getIntent().hasExtra("signin")) {
            transaction.replace(R.id.frame_layout,new SignInFragment()).commit();
        } else {
            transaction.replace(R.id.frame_layout,new SignUpFragment()).commit();
        }
    }
}
