package com.techmata.transcomfy.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.techmata.transcomfy.app.database.TransDbHelper;
import com.techmata.transcomfy.app.models.Trip;

/**
 * Created by Sparks on 17/08/2016.
 */
public class EditTripActivity extends AppCompatActivity {
    Trip trip;
    Integer tripID;

    TransDbHelper mDbHelper ;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_edit);

        tripID = getIntent().getExtras().getInt("tripID");
        Log.i("myTripID", tripID.toString());
        if (tripID == 0){
            moveTaskToBack(true);
        }

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        MainFragment mFrag = new MainFragment();
        mFragmentTransaction.replace(R.id.containerView, mFrag,"FRAG_MAIN").commit();

       // mFrag.btnRequest.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            //TODO: Delete in API
            mDbHelper.deleteTrip(trip.getId(),true);
           //moveTaskToBack(true);
            finish();
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
