package com.techmata.transcomfy.app;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;


//METHOD USED FOR INACTIVITY LOGOUT
public class MyBaseActivity extends AppCompatActivity {
    Date curDate, resumeDate;
    private static long start = 0, end = 0;
    @SuppressLint("HandlerLeak")
    public static Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {

        }
    };


    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            //Get the Resume Time & get difference in Time for Logout
            resumeDate = new Date();
            Log.i("RootActivity:onRun()", "******resumeDate=******" + resumeDate);
            long diff = resumeDate.getTime() - curDate.getTime();
            long secInt = diff / 1000 % 60; //conversion of milliseconds into human readable form
            Log.i("RootActivity:onRun()", "******sectInt=******" + secInt);
            if (secInt > 100) {// SET EXIT SCREEN INTERVAL LOGOUT
//                IdleLogout();
            }
        }
    };

    private void IdleLogout() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    //METHOD TO CALL ON RESETDISCONNECT WHEN USER ACTIVITY RESUMES
    public void resetDisconnectTimer() {
        MyBaseActivity.disconnectHandler.removeCallbacks(disconnectCallback);
        // TODO Repeat the task after a specified time delay
        MyBaseActivity.disconnectHandler.postDelayed(disconnectCallback, 10000);//10seconds
    }

    //METHOD TO CALL ON STOPDISCONNECT WHEN USER PRESS BACK BUTTON
    public void stopDisconnectTimer() {
        // TODO Stop the repeated task
        MyBaseActivity.disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Timer needs to be stopped when user manually pressed BACK button
        end = System.currentTimeMillis();
        long duration = end - start;
//        TODO DB enabling - Also save the activity name
//        MyShortcuts.saveEnd(getBaseContext());
        start = 0;
        long minutesDuration = (duration / 1000) / 60;

        Log.e("RootActivity:()onStop()", "******curDate=******" + duration);


        stopDisconnectTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setloginButton();
//        MainActivity.PROPERTY=0;
        curDate = new Date();
        //        TODO DB enabling
//        MyShortcuts.save(getBaseContext());
        start = System.currentTimeMillis();
        Log.e("RootActivity:onResume()", "******curDate=******" + curDate);
        resetDisconnectTimer();
    }


}


