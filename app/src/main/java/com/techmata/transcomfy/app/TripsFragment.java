package com.techmata.transcomfy.app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.techmata.transcomfy.app.database.TransDbHelper;
import com.techmata.transcomfy.app.models.Trip;
import com.techmata.transcomfy.app.utils.ServiceCallback;

import java.util.ArrayList;


/**
 * Created by Sparks on 25/08/2016.
 */
public class TripsFragment extends Fragment {

    public static ArrayList<Trip> listTrips = new ArrayList<Trip>();
    public static ArrayList<Trip> nListTrips = new ArrayList<Trip>();

    Trip[] trips;

    public LinearLayoutManager layoutManager;
    private TripsAdapter mTripsAdapter;

    private TransDbHelper myDB;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View v =  inflater.inflate(R.layout.fragment_trips,null);

        myDB = new TransDbHelper(getContext());


        listTrips.clear();
        trips = myDB.getTrips();
        for (int c = 0; c < trips.length ; c++){
            listTrips.add(trips[c]);
        }

        final RecyclerView tripList = (RecyclerView) v.findViewById(R.id.tripsList);
        mTripsAdapter = new TripsAdapter(getActivity(), listTrips);
        layoutManager =new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tripList.setLayoutManager(layoutManager);
        tripList.setAdapter(mTripsAdapter);

        //TODO: Update trip info in DB from server
        myDB.updateTrips(new ServiceCallback() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getContext(),"Callback",Toast.LENGTH_SHORT).show();
                nListTrips.clear();
                trips = myDB.getTrips();
                Log.i("myNewTrips",String.valueOf(trips.toString()));
                for (int c = 0; c < trips.length ; c++){
                    nListTrips.add(trips[c]);
                }
                //tripList.setAdapter(mTripsAdapter);
                mTripsAdapter.setTrips(nListTrips);
                Log.i("myCount",String.valueOf(mTripsAdapter.getItemCount()));
                mSwipeRefreshLayout.setEnabled(true);
            }
        });



        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeTrips);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setEnabled(false);
                Thread thread=  new Thread(){
                    @Override
                    public void run(){
                        myDB.updateTrips(new ServiceCallback() {
                            @Override
                            public void onSuccess() {
                                //Toast.makeText(getContext(),"Callback Swipe",Toast.LENGTH_SHORT).show();
                                nListTrips.clear();
                                trips = myDB.getTrips();
                                for (int c = 0; c < trips.length ; c++){
                                    nListTrips.add(trips[c]);
                                }
                                //tripList.setAdapter(mTripsAdapter);
                                mTripsAdapter.setTrips(nListTrips);
                                Log.i("myCount",String.valueOf(mTripsAdapter.getItemCount()));
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        mSwipeRefreshLayout.setEnabled(true);
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                };
                thread.start();
            }
        });



        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        nListTrips.clear();
        trips = myDB.getTrips();
        for (int c = 0; c < trips.length ; c++){
            nListTrips.add(trips[c]);
        }
        //tripList.setAdapter(mTripsAdapter);
        mTripsAdapter.setTrips(nListTrips);
    }

}
