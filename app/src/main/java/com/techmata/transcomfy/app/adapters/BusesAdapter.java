package com.techmata.transcomfy.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techmata.transcomfy.app.BusesActivity;
import com.techmata.transcomfy.app.R;
import com.techmata.transcomfy.app.TripActivity;
import com.techmata.transcomfy.app.models.Bus;
import com.techmata.transcomfy.app.utils.MyApplication;
import com.techmata.transcomfy.app.utils.PreferenceHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sparks on 26/03/2016.
 */

public class BusesAdapter extends RecyclerView.Adapter<BusesAdapter.ViewHolderBuses> {


    private final Context mContext;

    //contains the list of trips
    public ArrayList<Bus> mBuses = new ArrayList<Bus>();
    private LayoutInflater mInflater;
    String timeStamp;
    Date date;

    public BusesAdapter(Context context, ArrayList<Bus> trips) {
        this.mContext = context;
        this.mBuses = trips;
        mInflater = LayoutInflater.from(context);
    }


    public void setTrips(ArrayList<Bus> buses) {
        this.mBuses = buses;
        //update the adapter to reflect the new set buses
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderBuses onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_bus, parent, false);
        return new ViewHolderBuses(view,mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolderBuses holder, int position) {
        Bus currentBus = mBuses.get(position);
        holder.txtBusPlate.setText(currentBus.getNumberPlate());
        holder.txtRoute.setText(currentBus.getRouteName());
        holder.txtFare.setText("KSH 50");
        holder.txtStartTime.setText("Today 8:30 PM");
    }

    @Override
    public int getItemCount() {
        return mBuses.size();
    }

    class ViewHolderBuses extends RecyclerView.ViewHolder {

        TextView txtBusPlate ,txtRoute, txtStartTime, txtFare;
        Button btnBookBus;

        public ViewHolderBuses(View itemView, final Context context) {
            super(itemView);
            txtBusPlate = (TextView) itemView.findViewById(R.id.txtBusPlate);
            txtRoute = (TextView) itemView.findViewById(R.id.txtRoute);
            txtStartTime = (TextView) itemView.findViewById(R.id.txtStartTime);
            txtFare = (TextView) itemView.findViewById(R.id.txtFare);
            btnBookBus = itemView.findViewById(R.id.btnBookBus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context,"ID "+String.valueOf(mBuses.get(getAdapterPosition()).getId()),Toast.LENGTH_SHORT ).show();
                   // Intent intent = new Intent(context, TripActivity.class);
                    //intent.putExtra("tripID", mBuses.get(getAdapterPosition()).getId());
                    //context.startActivity(intent);
                }
            });

            btnBookBus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                            MyApplication.URL+"/trips/book",
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {



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
                            //Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }){

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String,String>();
                            params.put("Content-Type", "application/json");
                            params.put("Accept", "application/json");
                            params.put("Authorisation","Bearer " + PreferenceHelper.getAccessToken(btnBookBus.getContext()));
                            Log.i("myHeaders", params.toString());
                            return params;
                        }
                    };
                    MyApplication myApp = new MyApplication(btnBookBus.getContext());
                    jsonRequest.setRetryPolicy(new DefaultRetryPolicy());
                    MyApplication.getInstance().addToRequestQueue(jsonRequest);
                }
            });


        }
    }
}

