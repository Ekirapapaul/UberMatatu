package com.techmata.transcomfy.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.techmata.transcomfy.app.database.TransDbHelper;
import com.techmata.transcomfy.app.models.Trip;
import com.techmata.transcomfy.app.utils.MyApplication;
import com.techmata.transcomfy.app.utils.PreferenceHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sparks on 17/08/2016.
 */
public class TripActivity extends AppCompatActivity {
    TextView txtDate,txtTime, txtFrom, txtTo, txtStatus;
    TextView txtVehicle,txtDriver, txtDriverPhone;
    ListView lvGroup;
    Integer tripID;
    Button btnSStart, btnSStop;
    EditText etStart, etStop;
    ProgressBar pgStart, pgStop;

    LinearLayout lytVehicle, lytTrip,lytGroup;
    Trip trip;
    TransDbHelper mDbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        ActionBar tlb = getSupportActionBar();
        if (tlb != null) {
            tlb.setDisplayHomeAsUpEnabled(true);
            tlb.setHomeButtonEnabled(true);
            tlb.setTitle("Trip description");
        }

        tripID = getIntent().getExtras().getInt("tripID");
        Log.i("myTripID", tripID.toString());
        if (tripID == 0){
            moveTaskToBack(true);
        }

        mDbHelper = new TransDbHelper(TripActivity.this);
        trip = mDbHelper.getTrip(tripID);
        //Log.i("myTrip", trip.toString());

        /*offerImage = (ImageView) findViewById(R.id.offerImage);
        offerImage.setImageResource(trip.getImage_id());*/

        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText(trip.getTripDate(new SimpleDateFormat("EEEE dd MMM yyyy", Locale.ENGLISH)));

        txtTime = (TextView) findViewById(R.id.txtTime);
        txtTime.setText(trip.getTripTime(new SimpleDateFormat("hh:mm a", Locale.ENGLISH)));

        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtFrom.setText(trip.getStartName());

        txtTo = (TextView) findViewById(R.id.txtTo);
        txtTo.setText(trip.getEndName());

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtStatus.setText(Trip.STATUS[trip.getApproval()]);
        txtStatus.setTextColor(Trip.cSTATUS[trip.getApproval()]);

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(String.valueOf(trip.getId()) + Calendar.getInstance().getTimeInMillis(), BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.ivQR)).setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }


        //TODO: if allocated
        Integer approval = trip.getApproval();
        //Toast.makeText(TripActivity.this,"approval = "+approval,Toast.LENGTH_SHORT).show();


        //When to show vehicle and mileage info
        if (( approval == 2 ||  approval == 4 ||  approval == 5 )&& trip.getVehicleDriver() != null) {
            //Show vehicle-driver info
            //Toast.makeText(TripActivity.this,"allocated",Toast.LENGTH_SHORT).show();
            lytVehicle = (LinearLayout) findViewById(R.id.lytVehicle);
            lytVehicle.setVisibility(View.VISIBLE);

            //TODO confirm JSON names
            JSONObject vData = trip.getVehicleDriver();


            txtDriver = (TextView) findViewById(R.id.txtDriver);
            try {
                txtDriver.setText(vData.getString("fName") + " " + vData.getString("lName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            txtVehicle = (TextView) findViewById(R.id.txtVehicle);
            try {
                txtVehicle.setText(vData.getString("plate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            txtDriverPhone = (TextView) findViewById(R.id.txtPhoneNo);
            try {
                txtDriverPhone.setText("0" + vData.getString("phone_no"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //TODO  Add vehicle make
            txtDriverPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone_no = txtDriverPhone.getText().toString();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone_no));
                    startActivity(callIntent);
                }
            });

            //Show trip mileage layout
            lytTrip = (LinearLayout) findViewById(R.id.lytTrip);
            lytTrip.setVisibility(View.VISIBLE);

            etStart = (EditText) findViewById(R.id.etStart);
            btnSStart = (Button) findViewById(R.id.btnRSubmit);
            //if (trip.getStartMileage() != null){
            if (approval == 4 || approval == 5){
                //if traveling or completed
                setStartMileage();
            }else{
                pgStart = (ProgressBar) findViewById(R.id.pgStart);
                btnSStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if(etStart.getText().length() == 0){
                        Toast.makeText(TripActivity.this,"Value cannot be null",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //TODO set mileage locally
                    btnSStart.setVisibility(View.GONE);
                    pgStart.setVisibility(View.VISIBLE);
                    setMileage(0,Integer.parseInt(etStart.getText().toString()));
                    }
                });
            }

            etStop = (EditText) findViewById(R.id.etStop);
            btnSStop = (Button) findViewById(R.id.btnTSubmit);
            //if (trip.getEndMileage() != null){
            if (approval == 4){
                //if completed
                setStopMileage();
            }else{
                pgStop = (ProgressBar) findViewById(R.id.pgStop);
                btnSStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if(trip.getStartMileage() == null){
                        Toast.makeText(TripActivity.this,"Start Mileage has to be set first",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(etStop.getText().length() == 0){
                        Toast.makeText(TripActivity.this,"Value cannot be null",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //TODO: set mileage locally
                    btnSStop.setVisibility(View.GONE);
                    pgStop.setVisibility(View.VISIBLE);
                    setMileage(1,Integer.parseInt(etStop.getText().toString()));
                    }
                });
            }
        }



       /* JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,"https://maps.googleapis.com/maps/api/geocode/json?latlng="+trip.getStartCoord()+"&key="+getString(R.string.api_key),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                        Log.i("myPlaceName",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String response = new String(error.networkResponse.data,"UTF-8");
                    Log.i("myerrRes",response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                //params.put("Accept", "application/vnd.yielloh.v1");
                params.put("Accept", "application/json");
                //params.put("Authorization", "Bearer "+ PreferenceHelper.getAccessToken(mContext));
                return params;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication myApp = new MyApplication(TripActivity.this);
        MyApplication.getInstance().addToRequestQueue(jsonRequest);*/

    }
    private void setStartMileage(){
        String text = "";
        if(trip.getStartMileage() != null){
            text = trip.getStartMileage();
        }
        etStart.setText(text);
        etStart.setFocusable(false);
        etStart.setEnabled(false);
        btnSStart.setVisibility(View.GONE);
    }
    private void setStopMileage(){
        String text = "";
        if(trip.getEndMileage() != null){
            text = trip.getEndMileage();
        }
        etStop.setText(text);
        etStop.setFocusable(false);
        etStop.setEnabled(false);
        btnSStop.setVisibility(View.GONE);
    }
    private void pgHide(Integer option){
        if(option == 0){
            btnSStart.setVisibility(View.VISIBLE);
            pgStart.setVisibility(View.GONE);
        }else if(option == 1) {
            btnSStop.setVisibility(View.VISIBLE);
            pgStop.setVisibility(View.GONE);
        }
    }
    private void setMileage(final int option, int value){
        String ss;
        if(option == 0){
            ss = "start";
        }else if(option == 1){
            ss = "stop";
        }else {
            return;
        }
                JSONObject jParams = new JSONObject();
        try {
            jParams.put("id",tripID);
            if (option == 0){
                jParams.put("start_mileage",value);
            }else  if (option == 1){
                jParams.put("end_mileage",value);
                jParams.put("actual_fare",70);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("mySParams",jParams.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT,
                MyApplication.URL+"/trips/"+ss,
                jParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            Log.i("myRes", response.toString());
                            if(!response.toString().contains("error")){
                                pgHide(option);
                                if(option == 0){
                                    trip.setStartMileage(etStart.getText().toString());
                                    setStartMileage();
                                }else if(option == 1) {
                                    trip.setEndMileage(etStop.getText().toString());
                                    setStopMileage();
                                }
                                TransDbHelper mDB = new TransDbHelper(TripActivity.this);
                                mDB.deleteTrip(trip.getId(),false);
                                mDB.suaveTrip(response);
                                trip = mDB.getTrip(trip.getId());
                                //TODO Refresh all?
                                txtStatus.setText(Trip.STATUS[trip.getApproval()]);
                                txtStatus.setTextColor(Trip.cSTATUS[trip.getApproval()]);
                            }else {
                                Toast.makeText(TripActivity.this,"Error updating trip.",Toast.LENGTH_LONG).show();
                                pgHide(option);
                            }
                        }else{
                            Toast.makeText(TripActivity.this,"Error updating trip.",Toast.LENGTH_LONG).show();
                            pgHide(option);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pgHide(option);
                Log.i("myerr",error.toString());
                try {
                    String response = new String(error.networkResponse.data,"UTF-8");
                    Log.i("myerrRes",response);
                } catch (NullPointerException nE){
                    nE.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Toast.makeText(TripActivity.this,"Error",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorisation","Bearer "+ PreferenceHelper.getAccessToken(TripActivity.this));
                Log.i("myHeaders", params.toString());
                return params;
            }
        };
        MyApplication myApp = new MyApplication(TripActivity.this);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
        MyApplication.getInstance().addToRequestQueue(jsonRequest);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //TODO: If trip is not yet approved show edit
        if(trip.getApproval() == 0){
            getMenuInflater().inflate(R.menu.menu_trip, menu);
        }
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
        }else if(id == R.id.action_edit){
            Intent intent = new Intent(TripActivity.this, EditTripActivity.class);
            intent.putExtra("tripID", tripID);
            TripActivity.this.startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
