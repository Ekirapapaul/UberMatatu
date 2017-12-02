package com.techmata.transcomfy.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.techmata.transcomfy.app.models.AppController;
import com.techmata.transcomfy.app.models.MyShortcuts;
import com.techmata.transcomfy.app.models.Post;
import com.techmata.transcomfy.app.utils.Utils;
//import com.techmata.transcomfy.app.R;

//import com.digitalmatatus.ma3tycoon.utils.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game extends MyBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final GeoPoint NAIROBI = new GeoPoint(-1.279783, 36.822023);
    private MapView mapView;
    private int numRoundTotal = 3;
    private int numRound = 1;
    private static String nGroup = "801", route1, route2, route3;
    final int REQUEST_READ_PHONE_STATE = 2;
    ArrayList<Marker> arrayList = new ArrayList<>();
    Boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_appbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(true);
//        mapView.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
      /*  MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(17);
*/
        IMapController mapViewController = mapView.getController();
        mapViewController.setZoom(17);
        mapViewController.setCenter(NAIROBI);

        route1 = getIntent().getStringExtra("route");
        route2 = getIntent().getStringExtra("route");
        route3 = getIntent().getStringExtra("route");


        //mOsmPathOverlay = new OsmPathOverlay(context);
        //mMapView.getOverlayManager().add(mOsmPathOverlay);
        /*Polyline line = new Polyline(this);
        line.setTitle("Central Park, NYC");
        line.setSubDescription(Polyline.class.getCanonicalName());
        line.setWidth(10f);
        List<GeoPoint> pts = new ArrayList<>();
        //here, we create a polygon, note that you need 5 points in order to make a closed polygon (rectangle)

        pts.add(new GeoPoint(-1.290475, 36.82801));
        pts.add(new GeoPoint(-1.296463, 36.808161));
        pts.add(new GeoPoint(-1.297568, 36.804868));
        pts.add(new GeoPoint(-1.298912, 36.798084));
        pts.add(new GeoPoint(-1.299527, 36.792314));
        line.setPoints(pts);
        line.setGeodesic(true);
        line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));*/
        //Note, the info window will not show if you set the onclick listener
        //line can also attach click listeners to the line
        /*
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(context, "Hello world!", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        mapView.getOverlayManager().add(line);*/
//        getStops("638");

//        DetermineGroup("111", "12D", "120");
        DetermineGroup(getIntent().getStringExtra("route"), getIntent().getStringExtra("route"), getIntent().getStringExtra("route"));

//        getData("70400011111");


        Button button = (Button) findViewById(R.id.yes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nGroup.isEmpty()) {
//                    save("yes", nGroup, MyShortcuts.getImei(getBaseContext()), Utils.getIPAddress(false));
                    int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Game.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                    } else {
                        //TODO
                        postdata("yes", nGroup, MyShortcuts.getImei(getBaseContext()), Utils.getIPAddress(false));
                        restartRound();
                        if (edit) {
                            edit = false;
                            Log.e("inside edit", "inside edit");
                            for (int i = 0; i < arrayList.size(); i++) {
                                Log.e("marker coordinates", arrayList.get(i).getPosition().getLatitude() + ", " + arrayList.get(i).getPosition().getLongitude());
                            }
                            postdata("no", nGroup, MyShortcuts.getImei(getBaseContext()), Utils.getIPAddress(false));
                            restartRound();
                        }


                    }

                }
            }
        });
        Button button1 = (Button) findViewById(R.id.no);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nGroup.isEmpty()) {
//                    save("yes", nGroup, MyShortcuts.getImei(getBaseContext()), Utils.getIPAddress(false));
                    int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Game.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                    } else {
                        //TODO
                        edit = true;
                        MyShortcuts.showToast("Please long press and drag the markers to the correct bus stops and click yes", getBaseContext());
//                       TODO Uncomment below for previous setting
                       /* postdata("no", nGroup, MyShortcuts.getImei(getBaseContext()), Utils.getIPAddress(false));

                        restartRound();*/


                    }

                }
            }
        });
        Button button2 = (Button) findViewById(R.id.skip);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nGroup.isEmpty()) {
//                    save("yes", nGroup, MyShortcuts.getImei(getBaseContext()), Utils.getIPAddress(false));
                    int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Game.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                    } else {
                        //TODO
                        postdata("skip", nGroup, MyShortcuts.getImei(getBaseContext()), Utils.getIPAddress(false));
                        restartRound();


                    }

                }
            }
        });
    }

    private void getData(String id, final String numGroup) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                MyShortcuts.baseURL() + "twiga/game/routes/id/" + id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                Log.e("All Data", "response from the server is: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
//                    JSONObject routes = jsonObject.getJSONObject("routes");
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    Log.e("json Array", jsonArray.toString());
                    Polyline line = new Polyline(getBaseContext());
                    line.setTitle("Ma3tycoon, Nairobi");
                    line.setSubDescription(Polyline.class.getCanonicalName());
                    line.setWidth(17f);
                    line.setColor(Color.parseColor("#00BD9E"));
                    List<GeoPoint> pts = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        //here, we create a polygon, note that you need 5 points in order to make a closed polygon (rectangle)

                        pts.add(new GeoPoint(Float.parseFloat(jsonObject1.getString("stop_lat")), Float.parseFloat(jsonObject1.getString("stop_lon"))));

                    }

                    line.setPoints(pts);
                    line.setGeodesic(true);
                    line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
                    //Note, the info window will not show if you set the onclick listener
                    //line can also attach click listeners to the line
                    line.setOnClickListener(new Polyline.OnClickListener() {
                        @Override
                        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                            Toast.makeText(getBaseContext(), eventPos.getLatitude() + ", " + eventPos.getLongitude(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
                    mapView.getOverlayManager().add(line);
                    getStops(numGroup);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error getdata", "Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Check your credentials or internet connectivity!", Toast.LENGTH_LONG).show();
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("route", "70400011111");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void DetermineGroup(String r01, String r02, String r03) {


        if (r01.contains("/")) {
            String[] paths = r01.split("/");
            r01 = paths[0] + "_" + paths[1];
            Log.e("ro", r01 + "");
        }
        final String r1 = r01;
        if (r02.contains("/")) {
            String[] paths = r02.split("/");
            r02 = paths[0] + "_" + paths[1];
            Log.e("ro", r02 + "");

        }
        final String r2 = r02;
        if (r03.contains("/")) {
            String[] paths = r03.split("/");
            r03 = paths[0] + "_" + paths[1];
            Log.e("ro", r03 + "");

        }
        final String r3 = r03;

        Log.e("url", MyShortcuts.baseURL() + "twiga/game/determineGroup/" + "route1/" + r1 + "/route2/" + r2 + "/route3/" + r3);
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                MyShortcuts.baseURL() + "twiga/game/determineGroup/" + "route1/" + r1 + "/route2/" + r2 + "/route3/" + r3, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("All Data", "response from the server is: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("details");
                    Random rand = new Random();
                    int rand1 = rand.nextInt(jsonArray.length() - 1) + 1;
                    Log.e("n", rand1 + "");
                    String center_lon = jsonArray.getJSONObject(rand1).getString("center_lon");
                    String center_lat = jsonArray.getJSONObject(rand1).getString("center_lat");
                    String numGroup = jsonArray.getJSONObject(rand1).getString("groups");
                    String route = jsonArray.getJSONObject(rand1).getString("r_route_id");
                    nGroup = numGroup;

                    IMapController mapViewController = mapView.getController();
                    GeoPoint NAIROBI = new GeoPoint(Float.parseFloat(center_lat), Float.parseFloat(center_lon));
//                  TODO Getting Route

                    mapViewController.setZoom(16);
                    mapViewController.setCenter(NAIROBI);

                    getData(route, numGroup);
//                    getStops(numGroup);


//
//
//                    Log.e("json Array",jsonArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error group", "Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Check your credentials or internet connectivity!", Toast.LENGTH_LONG).show();
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("route_name1", r1);
                params.put("route_name2", r2);
                params.put("route_name3", r3);

                return params;
            }

        };

        Log.e("str", strReq.toString());
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getStops(final String numGroup) {
        arrayList.clear();
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                MyShortcuts.baseURL() + "twiga/game/showStops/numGroup/" + numGroup, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("All Stops", "response from the server is: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
//                    Todo Label for round number
                    String lbl = "Question " + numRound + "/" + numRoundTotal;
                    setTitle(lbl);
                    numRound = numRound + 1;
//                    JSONObject routes = jsonObject.getJSONObject("routes");
                    JSONArray jsonArray = jsonObject.getJSONArray("stops");
                    ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
//                    ArrayList<Marker> overlayItemArray = new ArrayList<Marker>();

                    String question;
                    if (jsonArray.length() >= 0) {
//                       TODO Question to display to users
                        question = "Are these " + jsonArray.length() + " stops shown on Route " + jsonArray.getJSONObject(0).getString("route_short_name") + " correct?";
                        TextView textView = (TextView) findViewById(R.id.question);
                        textView.setText(question);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        float stop_lon = Float.parseFloat(jsonObject1.getString("stop_lon"));
                        float stop_lat = Float.parseFloat(jsonObject1.getString("stop_lat"));
                        String label = jsonObject1.getString("stop_name") + " " + jsonObject1.getString("route_short_name") + " : " + jsonObject1.getString("route_desc");
                        String stop_name = jsonObject1.getString("stop_name");

                        // Create an ArrayList with overlays to display objects on map
//                                overlayItemArray = new ArrayList<OverlayItem>();

                        // Create some init objects
                        OverlayItem stop = new OverlayItem(stop_name, label,
                                new GeoPoint(stop_lat, stop_lon));

                        Log.e("EXAMPLES", label + stop_lat + " " + stop_lon);

                        overlayItemArray.add(stop);



                   /*     CustomMarker marker = new CustomMarker(stop_name, label,
                                new GeoPoint(stop_lat, stop_lon));
                        marker.set*/


                        Marker marker = new Marker(mapView);//you can also pass "this" as argument I believe
                        marker.setPosition(new GeoPoint(stop_lat, stop_lon));
                        marker.setTitle(stop_name);
                        marker.setSubDescription(label);

//                     TODO   Only set draggable if user clicked no
                        marker.setDraggable(true);
                       /* marker.name =stop_name;
                        marker.desc = label;*/
//                        marker.setInfoWindow();
                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                marker.showInfoWindow();
                                return true;

                            }
                        });
                        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDrag(Marker marker) {

                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {

                            }

                            @Override
                            public void onMarkerDragStart(Marker marker) {
                               /* marker.getPosition().getLatitude();
                                marker.getPosition().getLongitude();
                                marker.setTitle();
                                marker.getInfoWindow().getView().getTag();*/

                            }
                        });
                       /* marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker item, MapView arg1) {
                                item.showInfoWindow();

                                    item.closeInfoWindow();
                                return true;
                            }
                        });*/
//                        overlayItemArray.add(marker);


                        arrayList.add(marker);
                        mapView.getOverlays().add(marker);

                        // Add the init objects to the ArrayList overlayItemArray

                        /*overlayItemArray.add(new OverlayItem(stop_name, label,
                                new GeoPoint(-1.36294, 36.65618)));*/

                    }
                    Log.e("total", jsonArray.length() + " ");

                    // Add the Array to the IconOverlay
                    ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getBaseContext(), overlayItemArray, null);
//                    ItemizedOverlay<Marker>

                    // Add the overlay to the MapView
//                    mapView.getOverlays().add(itemizedIconOverlay);
//                    mapView.getOverlay().ad

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error stops", "Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Check your credentials or internet connectivity!", Toast.LENGTH_LONG).show();
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("numGroup", numGroup);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void restartRound() {
        if (numRound <= numRoundTotal) {
            mapView.getOverlayManager().clear();
//            DetermineGroup(MyShortcuts.getDefaults("route1",getBaseContext()), MyShortcuts.getDefaults("route2",getBaseContext()), MyShortcuts.getDefaults("route3",getBaseContext()));
            DetermineGroup(route1, route2, route3);
        } else {
            MyShortcuts.showToast("Thanks for your valuable input! ", getBaseContext());
           /* Intent intent = new Intent(getBaseContext(), TriviaQuestions.class);
            startActivity(intent);*/
        }

    }


    //    feedback,numGroup,login(username)
//    Saving the feedback gotten from the app
    private void save(final String feedback, final String numGroup, final String deviceId, final String ip_address) {

        JSONObject json = new JSONObject();
        try {
            json.put("feedback", feedback);
            json.put("numGroup", numGroup);
            json.put("userDevice", deviceId);
            json.put("ipadd", ip_address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Post.PostData("/game/sendFeedback", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON response", response.toString());

            }
        });

    }

    private void postdata(final String feedback, final String numGroup, final String deviceId, final String ip_address) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                MyShortcuts.baseURL() + "twiga/game/sendFeedback", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("All Data", "response from the server is: " + response.toString());
//                hideDialog();

//                Log.e("Url is",  MyShortcuts.getDefaults("url",getBaseContext()) + "twiga/auth/login?" + "username=" + username + "&password=" + password);

                try {
                    JSONObject jObj = new JSONObject(response);

                   /* Intent intent = new Intent(getBaseContext(), Game.class);
                    startActivity(intent);*/
                    /*String success = jObj.getString("success");
                    String session = "";
                    if (success.equals("true")) {
                        session = jObj.getString("user_id");

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("user_id", session);
                        startActivity(intent);
                    } else {
                        MyShortcuts.showToast("Wrong credentials, please try again", getBaseContext());
                    }
*/

//                    String success = jObj.getString("success");


                } catch (JSONException e) {
                    MyShortcuts.showToast("Wrong credentials, please try again", getBaseContext());

                    // JSON error
//                   loginUser(username,password);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error", "Error: " + error.getMessage());
//                Log.e("Url is", MyShortcuts.baseURL() + "cargo_handling/api/login/?" + "username=" + username + "&password=" + password);

//                Toast.makeText(getApplicationContext(),
//                        "Check your credentials or internet connectivity!", Toast.LENGTH_LONG).show();
//                loginUser(username,password);
//                hideDialog();
            }
        }) {
           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                setRetryPolicy(new DefaultRetryPolicy(5* DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
                setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
                headers.put("Content-Type", "application/json; charset=utf-8");
                String creds = String.format("%s:%s",username,password);
                Log.e("pass",password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }*/

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

//                try {


                /*params.put("stop_from", "dss");
                params.put("stop_to", "sds");
                params.put("amount", "dssd");
                params.put("route", " fs");
                params.put("weather", "fddf");
                params.put("traffic", "dsddf");
                params.put("demand", "dffd");
                params.put("rush_hour", "fdvs");
                params.put("peak", "cvccvc");
                params.put("user_id","11");
                params.put("travel_time",editText.getText().toString());*/

                params.put("feedback", feedback);
                params.put("numGroup", numGroup);
                params.put("userDevice", deviceId);
                params.put("ipadd", ip_address);
                params.put("t_added", "12:00");
                params.put("t_round", "2");
                params.put("t_load", "load");
                params.put("login", "guest");


               /* params.put("stop_from", getIntent().getStringExtra("stops_from")+"");
                params.put("stop_to", getIntent().getStringExtra("stops_to")+"");
                params.put("amount", getIntent().getStringExtra("amount")+"");
                params.put("route", getIntent().getStringExtra("stops_to")+"");
                params.put("weather", weather);
                params.put("traffic", traffic);
                params.put("demand", demand);
                params.put("rush_hour", rush);
                params.put("peak", peak);
                params.put("user_id", MyShortcuts.getDefaults("user_id", getBaseContext()));
                params.put("travel_time", editText.getText().toString());*/
                /*} catch (NullPointerException e) {
                    Log.e("error", e.toString());
                }
                Log.e("data is", getIntent().getStringExtra("stops_from") + ", " +
                        getIntent().getStringExtra("stops_to") + ", " + getIntent().getStringExtra("amount") + ", " +
                        weather + ", " + traffic
                        + ", " + demand + ", " + rush + ", " + peak + ", " + editText.getText().toString() + ", " + MyShortcuts.getDefaults("user_id", getBaseContext()));

*/
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                } else {
//                    MyShortcuts.showToast("This will enable us identify you uniquely", getBaseContext());
                    showDialog();
                }
                break;

            default:
                break;
        }
    }


    public void showDialog() {

        Log.e(" setting up", "up");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Game.this);

        alertDialogBuilder.setTitle("Click Accept");
        alertDialogBuilder.setMessage("This will help us identify you uniquely so as to determine the winner. Click accept in the permissions pop up to continue playing the game");


        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Game.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                } else {
                    //TODO
                    restartRound();


                }

            }


        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Intent intent = new Intent(getBaseContext(), GameConfiguration.class);
                startActivity(intent);*/
                dialog.cancel();

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

    }


}
