package com.techmata.transcomfy.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.techmata.transcomfy.app.database.TransDbHelper;
import com.techmata.transcomfy.app.models.Trip;
import com.techmata.transcomfy.app.utils.ConnectionDetector;
import com.techmata.transcomfy.app.utils.MyApplication;
import com.techmata.transcomfy.app.utils.PreferenceHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sparks on 25/08/2016.
 */
public class MainFragment extends Fragment {

    FragmentManager mFragManager;

    public EditText etDate, etTime, etPick, etDrop;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private SimpleDateFormat dateFormatter,timeFormatter;
    private Button btnRequest;
    private ProgressBar barProg;
    android.app.ActionBar actionBar;

    RecyclerView rvShare;
    UsersAdapter adapterUsers;

    private TransDbHelper mDbHelper ;

    LatLng locFrom, locTo = null;
    Calendar tripTime = null;
    String startName = null, endName = null;
    HashMap<String,Integer> projects;

    ContentValues values = new ContentValues();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */

        //getContext().deleteDatabase(TransDbHelper.DATABASE_NAME );

        View v =  inflater.inflate(R.layout.fragment_main,null);

        mFragManager = getFragmentManager();

        mDbHelper = new TransDbHelper(getContext());

        dateFormatter = new SimpleDateFormat("ccc, dd, MMM, yyyy");
        timeFormatter = new SimpleDateFormat("hh:mm a");

        Calendar newCalendar = Calendar.getInstance();
        final Calendar todayDate = Calendar.getInstance();
        tripTime = todayDate;

        barProg = (ProgressBar) v.findViewById(R.id.barProgress);

        datePicker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        etDate.setText(dateFormatter.format(newDate.getTime()));
                        newDate.getTime();
                        tripTime.set(year, monthOfYear, dayOfMonth);
                    }
                }
                ,newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(todayDate.getTimeInMillis()   );

        etDate = (EditText) v.findViewById(R.id.etDate);
        etDate.setText(dateFormatter.format(todayDate.getTime()));
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
                newDate.set(Calendar.MINUTE,minute);
                etTime.setText(timeFormatter.format(newDate.getTime()));
                tripTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                tripTime.set(Calendar.MINUTE,minute);
            }
        },newCalendar.get(Calendar.HOUR_OF_DAY),newCalendar.get(Calendar.MINUTE),false);


        etTime = (EditText) v.findViewById(R.id.etTime);
        etTime.setText(timeFormatter.format(todayDate.getTime()));
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });


        CoordinatorLayout mRoot = (CoordinatorLayout) v.findViewById(R.id.snackLyt);
        final Snackbar snackbar = Snackbar
                .make(mRoot, "Connect to the internet", Snackbar.LENGTH_LONG);
        snackbar.setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                //if(!new ConnectionDetector(getActivity()).isConnectingToInternet()){
                                    snackbar.dismiss();
                                //}
                            }
                        };
                        new Thread(runnable).start();
                    }
                });


        etPick = (EditText) v.findViewById(R.id.etPickLoc);
        etPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new ConnectionDetector(getActivity()).isConnectingToInternet()){
                    snackbar.show();
                    return;
                }
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(new LatLngBounds(MyApplication.SW,MyApplication.NE));
                try {
                    startActivityForResult(builder.build(getActivity()),1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        etDrop = (EditText) v.findViewById(R.id.etDropLoc);
        etDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new ConnectionDetector(getActivity()).isConnectingToInternet()){
                    snackbar.show();
                    return;
                }
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(new LatLngBounds(MyApplication.SW,MyApplication.NE));
                try {
                    startActivityForResult(builder.build(getActivity()),2);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        btnRequest = (Button) v.findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH");

                Date tDate = tripTime.getTime();
                Integer tHours = Integer.valueOf(sdf.format(tDate));
                Log.d("myHours", String.valueOf(tHours));


                Date cDate = Calendar.getInstance().getTime();
                Integer cHours = Integer.valueOf(sdf.format(cDate));
                Log.d("myHours", String.valueOf(cHours));

                Integer diffHours = tHours - cHours;
                Log.d("myDiff", String.valueOf(diffHours));

                if(diffHours > 3 ){
                    Toast.makeText(getContext(), "Book a trip that is within 4 hours", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!new ConnectionDetector(getActivity()).isConnectingToInternet()){
                    snackbar.show();
                    return;
                }
                if(locFrom == null){
                    Toast.makeText(getContext(),"Please select pick up location",Toast.LENGTH_SHORT).show();
                }else if(locTo == null){
                    Toast.makeText(getContext(),"Please select drop off location",Toast.LENGTH_SHORT).show();
                }else {

                    getDistance(locFrom,locTo);
                    barProg.setVisibility(View.VISIBLE);
                    btnRequest.setVisibility(View.GONE);
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            //from
            if(resultCode == Activity.RESULT_OK){
                Place selectedPlace = PlacePicker.getPlace(getActivity(),data);
                startName = handlePlace(selectedPlace);
                etPick.setText(startName);
                locFrom = selectedPlace.getLatLng();
            }
        }
        if(requestCode == 2){
            //to
            if(resultCode == Activity.RESULT_OK){
                Place selectedPlace = PlacePicker.getPlace(getActivity(),data);
                locTo = selectedPlace.getLatLng();
                endName = handlePlace(selectedPlace);
                etDrop.setText(endName);
            }
        }
    }

    private String handlePlace(Place selectedPlace){
        String text;
        Log.i("myPlace", selectedPlace.toString());
        if (selectedPlace.getName() != null){
            text = selectedPlace.getName().toString();
            if (selectedPlace.getAddress() != null){
                text += ", "+selectedPlace.getAddress().toString();
            }
        }else {
            if (selectedPlace.getAddress() != null){
                text = selectedPlace.getAddress().toString();
            }else {
                text = selectedPlace.getLatLng().toString();
            }
        }
        return text;
    }

    private void RequestTrip(Calendar tripTime, final LatLng from, LatLng to,int distance){
        SimpleDateFormat mydateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat mytimeFormatter = new SimpleDateFormat("HH:mm:ss");

        Log.i("myfrom", from.toString());
        JSONObject jsonParam = new JSONObject();

        //TODO: decimal points?
        Integer cost  = distance/1000 * Trip.COSTPERKM;
        Random r = new Random();
        int tripID = r.nextInt(99 - 1) + 1;

        try {
            jsonParam.put("id",tripID);
            jsonParam.put("start_coordinate",String.valueOf(from.latitude)+","+String.valueOf(from.longitude));
            jsonParam.put("end_coordinate", String.valueOf(to.latitude)+","+String.valueOf(to.longitude));
            jsonParam.put("trip_date", mydateFormatter.format(tripTime.getTime()));
            jsonParam.put("trip_time", mytimeFormatter.format(tripTime.getTime()));
            jsonParam.put("start_location", startName);
            jsonParam.put("end_location", endName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("myparam",jsonParam.toString());
        mDbHelper.saveTrip(jsonParam,1);
        etPick.setText("");
        locFrom = null;
        etDrop.setText("");
        locTo = null;
        if(true){

            return;
        }
       /* JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                MyApplication.URL+"/trips/buses",
                jsonParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            barProg.setVisibility(View.GONE);
                            btnRequest.setVisibility(View.VISIBLE);
                            try {
                                Log.i("myresp",response.toString(2));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                                if (response.has("id")){
                                    //Toast.makeText(getContext(),"Ride Requested",Toast.LENGTH_LONG).show();
                                    etPick.setText("");
                                    locFrom = null;
                                    etDrop.setText("");
                                    locTo = null;
                                    //save trip to db
                                    *//*SaveTrip task = new SaveTrip();
                                    task.execute(response);*//*

                                } else {
                                    Log.i("myerr","invalid response");
                                    String err = "Error";
                                    if (response.has("error")){
                                        try {
                                            err = response.getString("error");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        }else{
                            Toast.makeText(getContext(),"Error, please try again.",Toast.LENGTH_LONG).show();
                            barProg.setVisibility(View.GONE);
                            btnRequest.setVisibility(View.VISIBLE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("myerr",error.toString());
                try {
                    String response = new String(error.networkResponse.data,"UTF-8");
                    Log.i("myerrRes",response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                barProg.setVisibility(View.GONE);
                btnRequest.setVisibility(View.VISIBLE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorisation","Bearer "+PreferenceHelper.getAccessToken(getContext()));
                Log.i("myHeaders", params.toString());
                return params;
            }
        };
        MyApplication myApp = new MyApplication(getContext());
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
        MyApplication.getInstance().addToRequestQueue(jsonRequest);*/
    }

    public int getDistance(LatLng origin,LatLng destination){
        final int distance = 0;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                        String.valueOf(origin.latitude)+"," +String.valueOf(origin.longitude)+
                        "&destinations="+
                        String.valueOf(destination.latitude)+","+String.valueOf(destination.longitude)+
                        "&key="+ getString(R.string.api_key_matrix),
                null,
                new Response.Listener<JSONObject>() {
                    int d;
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null){
                            Log.i("myDistance",response.toString());
                            try {
                                d = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements")
                                        .getJSONObject(0).getJSONObject("distance").getInt("value");
                                //requestTrip
                                //projectID = projects.get(spProjects.getSelectedItem());
                                RequestTrip(tripTime,locFrom,locTo,d);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(getContext(),"Error getting distance.",Toast.LENGTH_LONG).show();
                            barProg.setVisibility(View.GONE);
                            btnRequest.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("myerr",error.toString());
                try {
                    String response = new String(error.networkResponse.data,"UTF-8");
                    Log.i("myerrRes",response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                barProg.setVisibility(View.GONE);
                btnRequest.setVisibility(View.VISIBLE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                Log.i("myHeaders", params.toString());
                return params;
            }
        };
        MyApplication myApp = new MyApplication(getContext());
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
        MyApplication.getInstance().addToRequestQueue(jsonRequest);

        return distance;
    }
}
