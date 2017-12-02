package com.techmata.transcomfy.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.techmata.transcomfy.app.adapters.BusesAdapter;
import com.techmata.transcomfy.app.models.Bus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sparks on 17/08/2016.
 */
public class BusesActivity extends AppCompatActivity {
    RecyclerView rvBuses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses);

        ActionBar tlb = getSupportActionBar();
        if (tlb != null) {
            tlb.setDisplayHomeAsUpEnabled(true);
            tlb.setHomeButtonEnabled(true);
            tlb.setTitle("Book Bus");
        }

        rvBuses = findViewById(R.id.rvBuses);

        String buses_data = getIntent().getExtras().getString("buses_data");
        ArrayList<Bus> mBuses = new ArrayList<>();

        try {
            JSONArray buses = (new JSONObject(buses_data)).getJSONArray("buses");

            for (int c = 0; c < buses.length(); c ++){
                JSONObject bus = buses.getJSONObject(c);
                mBuses.add(new Bus(
                        bus.getInt("id"),
                        bus.getInt("capacity"),
                        bus.getString("plate"),
                        bus.getString("route_name")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        BusesAdapter mBusesAdapter = new BusesAdapter(BusesActivity.this, mBuses);
        LinearLayoutManager layoutManager =new LinearLayoutManager(BusesActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvBuses.setLayoutManager(layoutManager);
        rvBuses.setAdapter(mBusesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
