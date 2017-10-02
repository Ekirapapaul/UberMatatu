package com.techmata.transcomfy.app.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techmata.transcomfy.app.MainActivity;
import com.techmata.transcomfy.app.TripActivity;
import com.techmata.transcomfy.app.database.TransContract.Trips;
import com.techmata.transcomfy.app.database.TransContract.UserDetails;
import com.techmata.transcomfy.app.database.TransContract.UserProjects;
import com.techmata.transcomfy.app.models.Trip;
import com.techmata.transcomfy.app.utils.MyApplication;
import com.techmata.transcomfy.app.utils.PreferenceHelper;
import com.techmata.transcomfy.app.utils.ServiceCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SparkSoft on 18/03/2015.
 */
public class TransDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "techmata.transcomfy.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static Boolean CHANGE_FLAG = false;
    private final Context mContext;
    SQLiteDatabase db;

    ContentValues values = new ContentValues();

    public TransDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UserDetails.TABLE_NAME+ " (" +
                UserDetails.COLUMN_NAME_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                UserDetails.COLUMN_NAME_DETAIL_NAME+ TEXT_TYPE + COMMA_SEP +
                UserDetails.COLUMN_NAME_DETAIL_VALUE + TEXT_TYPE +
                " )");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UserProjects.TABLE_NAME+ " (" +
                UserProjects.COLUMN_NAME_PROJECT_ID + " INTEGER PRIMARY KEY," +
                UserProjects.COLUMN_NAME_PROJECT_NAME + TEXT_TYPE +
                " )");
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + Trips.TABLE_NAME+ " (" +
                Trips.COLUMN_NAME_TRIP_ID+ " INTEGER PRIMARY KEY ," +
                Trips.COLUMN_NAME_START_MILEAGE+" INTEGER "+ COMMA_SEP +
                Trips.COLUMN_NAME_STOP_MILEAGE+" INTEGER "+ COMMA_SEP +
                Trips.COLUMN_NAME_TRIP_DATE+" DATE "+ COMMA_SEP +
                Trips.COLUMN_NAME_TRIP_TIME+" DATETIME "+ COMMA_SEP +
                Trips.COLUMN_NAME_DATE+" DATETIME "+ COMMA_SEP +
                Trips.COLUMN_NAME_VEHICLE_DRIVER+" INTEGER "+ COMMA_SEP +
                Trips.COLUMN_NAME_START_TIME+" DATETIME "+ COMMA_SEP +
                Trips.COLUMN_NAME_STOP_TIME+" DATETIME "+ COMMA_SEP +
                Trips.COLUMN_NAME_TRIP_CREATOR+" INTEGER "+ COMMA_SEP +
                Trips.COLUMN_NAME_START_COORD+" TEXT "+ COMMA_SEP +
                Trips.COLUMN_NAME_START_NAME+" TEXT "+ COMMA_SEP +
                Trips.COLUMN_NAME_END_COORD+" TEXT "+ COMMA_SEP +
                Trips.COLUMN_NAME_END_NAME+" TEXT "+ COMMA_SEP +
                Trips.COLUMN_NAME_GROUP+" TEXT "+ COMMA_SEP +
                Trips.COLUMN_NAME_APPROVAL+" INTEGER "+
                " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int getTripCount(){
        String countQuery = "SELECT  * FROM " + Trips.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    public Trip[] getTrips(){
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ Trips.TABLE_NAME +" ORDER BY "+Trips.COLUMN_NAME_TRIP_ID
                +" DESC",null);
        Trip[] trips = new Trip[cursor.getCount()];
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
                final Integer id = cursor.getInt(cursor.getColumnIndex(Trips.COLUMN_NAME_TRIP_ID)) ;
                final String startMileage = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_MILEAGE));
                final String endMileage = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_STOP_MILEAGE));
                final String tripdate = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_TRIP_DATE));
                final String tripTime = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_TRIP_TIME));
                final String date = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_DATE));
                final String startTime = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_TIME));
                final String stoptime = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_STOP_TIME));
                final String startCoord = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_COORD));
                String startName = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_NAME));
                final String stopCoord = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_END_COORD));
                String stopName = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_END_NAME));
                final Integer approval = cursor.getInt(cursor.getColumnIndex(Trips.COLUMN_NAME_APPROVAL));

                //Get place names
                if(startName == null){
                    startName = startCoord;
                }
                if(stopName == null){
                    stopName = stopCoord;
                }

                trips[cursor.getPosition()] = new Trip(id,startMileage,endMileage,tripdate,tripTime,date,startTime,stoptime,startCoord,stopCoord,approval,startName,stopName);
                cursor.moveToNext();
            }
        }else{
/*            mContext.getParerunOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mContext,"No Data",Toast.LENGTH_SHORT).show();
                }
            });*/
        }

        return trips;
    }

    public Trip getTrip(int tripID){
        db = this.getReadableDatabase();
        Trip trip = null;
        //Log.i("mytripID", String.valueOf(tripID));
        Cursor cursor = db.rawQuery(
                "SELECT * FROM "+Trips.TABLE_NAME+" WHERE "+Trips.COLUMN_NAME_TRIP_ID+" = "+tripID, null);
        try {
            cursor.moveToFirst();
            final Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_TRIP_ID)));
            final String startMileage = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_MILEAGE));
            final String endMileage = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_STOP_MILEAGE));
            final String tripdate = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_TRIP_DATE));
            final String tripTime = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_TRIP_TIME));
            final String date = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_DATE));
            final String startTime = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_TIME));
            final String stoptime = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_STOP_TIME));
            final String vDriver = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_VEHICLE_DRIVER));
            final String tCreator = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_TRIP_CREATOR));
            final String startCoord = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_COORD));
            String startName = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_START_NAME));
            final String stopCoord = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_END_COORD));
            String stopName = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_END_NAME));
            final Integer approval = cursor.getInt(cursor.getColumnIndex(Trips.COLUMN_NAME_APPROVAL));
            final String group = cursor.getString(cursor.getColumnIndex(Trips.COLUMN_NAME_GROUP));
            JSONObject vehicleDriver = null;
            if(vDriver != null){
                try {
                    vehicleDriver = new JSONObject(vDriver);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject tripCreator = null;
            if(tCreator != null){
                try {
                    tripCreator = new JSONObject(tCreator);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("myendMileage", String.valueOf(endMileage));


            JSONArray mGroup = null;
            if(group != null){
                try {
                    mGroup = new JSONArray(group);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("mGroup", group.toString());
            }else {
                Log.i("mGroup", "null");
            }

            //Get place names
            if(startName == null){
                startName = startCoord;
            }
            if(stopName == null){
                stopName = stopCoord;
            }

            trip = new Trip(id,startMileage,endMileage,tripdate,tripTime,date,startTime,stoptime,vehicleDriver,tripCreator,startCoord,stopCoord,approval,startName,stopName,mGroup);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return trip;
    }
    public boolean deleteTrip(int tripID, boolean api){
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ Trips.TABLE_NAME + " WHERE " + Trips.COLUMN_NAME_TRIP_ID +" = "+tripID);
        db.close();
        if(api){
            final SQLiteDatabase mDb = this.getWritableDatabase();
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE,
                    MyApplication.URL+"/trips/"+String.valueOf(tripID),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response != null){
                                Log.i("myRes", response.toString());

                            }else{
                                Toast.makeText(mContext,"Error updating projects.",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("Content-Type", "application/json");
                    params.put("Accept", "application/json");
                    params.put("Authorisation","Bearer "+PreferenceHelper.getAccessToken(mContext));
                    Log.i("myHeaders", params.toString());
                    return params;
                }
            };
            MyApplication myApp = new MyApplication(mContext);
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
            MyApplication.getInstance().addToRequestQueue(jsonRequest);

        }
        return true;
    }

    public void saveTrip(JSONObject tripData,int action){
        new storeTrip(action).execute(tripData);
    }
    public void saveTrip(JSONObject tripData){
        //db = this.getWritableDatabase();
        new storeTrip().execute(tripData);
    }

    private class storeTrip extends AsyncTask<JSONObject, Void, Void> {
        Integer tripID;
        Integer action = 0;

        public storeTrip(){
        }
        public storeTrip(int action){
            this.action = action;
        }

        protected Void doInBackground(final JSONObject... data) {

            SQLiteDatabase mDB = new TransDbHelper(mContext).getWritableDatabase();
            JSONObject trip = data[0];
            String mNull = null;
            values = new ContentValues();
            try {
                /*SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyy-MM-dd");
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                Date date = sdfDateTime.parse(trip.getString("date"));
                Date tripDate = sdfDate.parse(trip.getString("trip_date"));
                Date tripTime = sdfTime.parse(trip.getString("trip_time"));*/

                tripID = trip.getInt("id");
                values.put(TransContract.Trips.COLUMN_NAME_TRIP_ID, tripID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

                //Log.i("gotTrip",trip.getString("end"))

            valuesAdd(TransContract.Trips.COLUMN_NAME_TRIP_DATE, trip, "trip_date", 1);
            valuesAdd(TransContract.Trips.COLUMN_NAME_TRIP_TIME, trip, "trip_time", 1);
            //valuesAdd(TransContract.Trips.COLUMN_NAME_TRIP_CREATOR, trip,"trip_creator",1);
            values.put(TransContract.Trips.COLUMN_NAME_TRIP_CREATOR, mNull);
            values.put(TransContract.Trips.COLUMN_NAME_DATE, mNull);

            valuesAdd(TransContract.Trips.COLUMN_NAME_START_COORD, trip, "start_coordinate", 1);
            valuesAdd(TransContract.Trips.COLUMN_NAME_END_COORD, trip, "end_coordinate", 1);
           // valuesAdd(TransContract.Trips.COLUMN_NAME_APPROVAL, trip, "approval", 0);
            values.put(TransContract.Trips.COLUMN_NAME_APPROVAL, 0);

            //valuesAdd(TransContract.Trips.COLUMN_NAME_VEHICLE_DRIVER, trip, "vehicle_driver", 1);
            //valuesAdd(TransContract.Trips.COLUMN_NAME_START_TIME, trip, "start_time", 1);
           // valuesAdd(TransContract.Trips.COLUMN_NAME_START_MILEAGE, trip, "start_mileage", 1);
            //valuesAdd(TransContract.Trips.COLUMN_NAME_STOP_MILEAGE, trip, "end_mileage", 1);
           // valuesAdd(Trips.COLUMN_NAME_START_NAME, trip, "start_location", 1);
            //valuesAdd(TransContract.Trips.COLUMN_NAME_STOP_TIME, trip, "stop_time", 1);
           // valuesAdd(Trips.COLUMN_NAME_END_NAME, trip, "end_location", 1);
           // valuesAdd(TransContract.Trips.COLUMN_NAME_GROUP, trip, "group", 3);

            mDB.insert(
                    TransContract.Trips.TABLE_NAME, null,
                    values);
            mDB.close();
            values = null;


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(this.action == 1){
                Intent intent = new Intent(mContext, TripActivity.class);
                intent.putExtra("tripID", tripID);
                mContext.startActivity(intent);
            }
        }
    }
    public void suaveTrip(JSONObject trip) {
        Integer tripID;

        SQLiteDatabase mDB = new TransDbHelper(mContext).getWritableDatabase();
        String mNull = null;
        values = new ContentValues();
        try {
            /*SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            Date date = sdfDateTime.parse(trip.getString("date"));
            Date tripDate = sdfDate.parse(trip.getString("trip_date"));
            Date tripTime = sdfTime.parse(trip.getString("trip_time"));*/

            tripID = trip.getInt("id");
            values.put(TransContract.Trips.COLUMN_NAME_TRIP_ID, tripID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.i("gotTrip",trip.getString("end"))

        valuesAdd(TransContract.Trips.COLUMN_NAME_TRIP_DATE, trip, "trip_date", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_TRIP_TIME, trip, "trip_time", 1);
        //valuesAdd(TransContract.Trips.COLUMN_NAME_TRIP_CREATOR, trip,"trip_creator",1);
        values.put(TransContract.Trips.COLUMN_NAME_TRIP_CREATOR, mNull);
        valuesAdd(TransContract.Trips.COLUMN_NAME_DATE, trip, "date", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_START_COORD, trip, "start_coordinate", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_END_COORD, trip, "end_coordinate", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_APPROVAL, trip, "approval", 0);
        valuesAdd(TransContract.Trips.COLUMN_NAME_VEHICLE_DRIVER, trip, "vehicle_driver", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_START_TIME, trip, "start_time", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_START_MILEAGE, trip, "start_mileage", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_STOP_MILEAGE, trip, "end_mileage", 1);
        valuesAdd(Trips.COLUMN_NAME_START_NAME, trip, "start_location", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_STOP_TIME, trip, "stop_time", 1);
        valuesAdd(Trips.COLUMN_NAME_END_NAME, trip, "end_location", 1);
        valuesAdd(TransContract.Trips.COLUMN_NAME_GROUP, trip, "group", 3);

        mDB.insert(
                TransContract.Trips.TABLE_NAME, null,
                values);
        mDB.close();
        values = null;

    }

    public void updateTrips(final ServiceCallback callback){
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,
                MyApplication.URL+"/trips/mytrips",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null){
                            Log.i("myRes", response.toString());
                            //Drop table
                            //db.execSQL("DROP TABLE IF EXISTS "+ Trips.TABLE_NAME);
                            for(int c = 0;c < response.length(); c++){
                                try {
                                    JSONObject trip = response.getJSONObject(c);
                                    deleteTrip(trip.getInt("id"),false);
                                    suaveTrip(trip);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            callback.onSuccess();
                        }else{
                            Toast.makeText(mContext,"Error updating trips.",Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("myerr",error.toString());
                try {
                    String response = new String(error.networkResponse.data,"UTF-8");
                    Log.i("myerrRes",response);
                } catch (NullPointerException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorisation","Bearer "+PreferenceHelper.getAccessToken(mContext));
                Log.i("myHeaders", params.toString());
                return params;
            }
        };
        MyApplication myApp = new MyApplication(mContext);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
        MyApplication.getInstance().addToRequestQueue(jsonRequest);
    }
    private void valuesAdd(String dbName, JSONObject data,String dataName, Integer DATA_TYPE){
            if(!data.isNull(dataName)) {
                if(DATA_TYPE == 0){
                    //Integer
                    try {
                        values.put(dbName,data.getInt(dataName));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(DATA_TYPE == 1){
                    //String
                    try {
                        values.put(dbName,data.getString(dataName));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else if(DATA_TYPE == 3){
                    //JSON Array
                    try {
                        values.put(dbName, String.valueOf(data.getJSONArray(dataName)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                String n = null;
                values.put(dbName,n);
            }
        }

    public int getProjectsCount(){
        String countQuery = "SELECT  * FROM " + UserProjects.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    public void fetchProjects(final Integer callback){
        final SQLiteDatabase mDb = this.getWritableDatabase();
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,
                MyApplication.URL+"/projects/me",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null){
                            Log.i("myRes", response.toString());
                            //Drop table
                            //db.execSQL("DROP TABLE IF EXISTS "+ Trips.TABLE_NAME);
                            for(int c = 0;c < response.length(); c++){
                                try {
                                    JSONObject project = response.getJSONObject(c);
                                    //deleteTrip(trip.getInt("id"));
                                    new saveProject().execute(project);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(callback == 1){
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ((Activity) mContext).finish();
                                mContext.startActivity(intent);
                            }
                        }else{
                            Toast.makeText(mContext,"Error updating projects.",Toast.LENGTH_LONG).show();
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
                Toast.makeText(mContext,"Error",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorisation","Bearer "+PreferenceHelper.getAccessToken(mContext));
                Log.i("myHeaders", params.toString());
                return params;
            }
        };
        MyApplication myApp = new MyApplication(mContext);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
        MyApplication.getInstance().addToRequestQueue(jsonRequest);
    }
    public HashMap<String,Integer> getProjects(){
        HashMap<String,Integer> projects = new HashMap<>();

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TransContract.UserProjects.TABLE_NAME ,null);
        if(getProjectsCount() > 0 ){
            if(cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    final Integer id = cursor.getInt(cursor.getColumnIndex(UserProjects.COLUMN_NAME_PROJECT_ID)) ;
                    final String projectName = cursor.getString(cursor.getColumnIndex(UserProjects.COLUMN_NAME_PROJECT_NAME));
                    projects.put(projectName,id);
                    cursor.moveToNext();
                }
            }
        }
        return projects;
    }

    private class saveProject extends AsyncTask<JSONObject, Void, Void> {
        Integer projectID;

        protected Void doInBackground(final JSONObject... data) {
            JSONObject project = data[0];
            String mNull = null;
            values = new ContentValues();
            try {
                projectID = project.getInt("id");
                values.put(UserProjects.COLUMN_NAME_PROJECT_ID, projectID);
                values.put(UserProjects.COLUMN_NAME_PROJECT_NAME, project.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TransDbHelper transDbHelper = new TransDbHelper(mContext);
            SQLiteDatabase mDB = transDbHelper.getWritableDatabase();
            mDB.insert(
                    TransContract.UserProjects.TABLE_NAME, null,
                    values);
            mDB.close();
            values = null;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
