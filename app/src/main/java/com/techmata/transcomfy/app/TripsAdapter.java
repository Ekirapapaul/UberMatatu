package com.techmata.transcomfy.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.techmata.transcomfy.app.models.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sparks on 26/03/2016.
 */

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolderTrips> {


    private final Context mContext;

    //contains the list of trips
    public ArrayList<Trip> mTrips = new ArrayList<Trip>();
    private LayoutInflater mInflater;
    String timeStamp;
    Date date;

    public TripsAdapter(Context context, ArrayList<Trip> trips) {
        this.mContext = context;
        this.mTrips = trips;
        mInflater = LayoutInflater.from(context);
    }


    public void setTrips(ArrayList<Trip> trips) {
        this.mTrips = trips;
        //update the adapter to reflect the new set trips
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderTrips onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trip_item, parent, false);
        return new ViewHolderTrips(view,mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolderTrips holder, int position) {
        Trip currentTrip = mTrips.get(position);
        holder.txtDate.setText("Trip on "+currentTrip.getTripDate(new SimpleDateFormat("EEEE dd MMM yyyy", Locale.ENGLISH)));
        holder.txtTime.setText(currentTrip.getTripTime(new SimpleDateFormat("hh:mm a", Locale.ENGLISH)));
        holder.txtFrom.setText(currentTrip.getStartName());
        holder.txtTo.setText(currentTrip.getEndName());
        holder.txtStatus.setText(Trip.STATUS[currentTrip.getApproval()]);
        holder.txtStatus.setTextColor(Trip.cSTATUS[currentTrip.getApproval()]);
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    class ViewHolderTrips extends RecyclerView.ViewHolder {

        TextView txtDate ,txtTime, txtFrom, txtTo,txtStatus;

        public ViewHolderTrips(View itemView, final Context context) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtFrom = (TextView) itemView.findViewById(R.id.txtFrom);
            txtTo = (TextView) itemView.findViewById(R.id.txtTo);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context,"ID "+String.valueOf(mTrips.get(getAdapterPosition()).getId()),Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent(context, TripActivity.class);
                    intent.putExtra("tripID", mTrips.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}

