package com.techmata.transcomfy.app.utils;

import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyApplication extends Application {

    public static String CLIENT_SECRET  = "7d2689ade5885f5f59caad220123097d726e9407a3b1d490381a75e5b98ccd74";
    public static String CLIENT_ID  = "f2e2ee094f51a74a7031b1e7f2558d286c647316406e251ac3cc14ad13427509";
    public static String URL1  = "http://192.168.0.10/transcomfy/api";
    public static String URL  = "http://techmata.co.ke/transcomfy/api";
    public static String URL0  = "http://imanicabs.co.ke/taxiDispatch/api";
    //public static String API_KEY = "2222";
    public static String API_KEY = "1111";
    public static final String TAG = MyApplication.class.getSimpleName();
    private static RequestQueue mRequestQueue;
    private static MyApplication mInstance;
    private static Context context;
    public static LatLng SW = new LatLng(-3,35);
    public static LatLng NE = new LatLng(1,38);


    public MyApplication(Context contx){
        context = contx;
        mInstance = this;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this.context);
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public static LatLng strToLatLng(String latclong){
        String[] values = latclong.split(",");
        Double lat = Double.parseDouble(values[0]);
        Double lon = Double.parseDouble(values[1]);
        Log.i("myLatLon",String.valueOf(lat)+","+String.valueOf(lon));
        LatLng coords = new LatLng(lat,lon);
        return coords;
    }
    public static String getPlaceName(Context mContext,final LatLng coords) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());

        String address = null;
        try {
            addresses = geocoder.getFromLocation(coords.latitude, coords.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
