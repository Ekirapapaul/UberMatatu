package com.techmata.transcomfy.app.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.techmata.transcomfy.app.MainActivity;
import com.techmata.transcomfy.app.R;

/**
 * Created by Sparks on 28/03/2016.
 */
public class AccountActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSharedPreferences = getSharedPreferences("access_token", Context.MODE_PRIVATE);
        Intent intent;
        if (mSharedPreferences.getString("access_token",null)!= null){
            intent = new Intent(AccountActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_account);
            Button signin = (Button)findViewById(R.id.btn_sign_in);
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AccountActivity.this, AuthActivity.class);
                    intent.putExtra("signin", "signin");
                    startActivity(intent);
                }
            });

            Button register = (Button)findViewById(R.id.btn_sign_up);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AccountActivity.this, AuthActivity.class);
                    intent.putExtra("signup", "signup");
                    startActivity(intent);
                }
            });
        }
        super.onCreate(savedInstanceState);
    }
}
