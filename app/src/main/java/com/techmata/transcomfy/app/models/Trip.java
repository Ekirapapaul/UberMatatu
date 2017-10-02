package com.techmata.transcomfy.app.models;

import android.graphics.Color;
import com.techmata.transcomfy.app.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sparks on 08/09/2016.
 */
public class Trip {
    Integer id;
    String startMileage, endMileage;
    String tripDate, tripTime, date, startTime, stopTime;
    JSONObject vehicleDriver, tripCreator;
    JSONArray group;
    String startCoord, stopCoord, startName, endName;
    Integer approval;
    public  static  Integer COSTPERKM = 70;
    public static String STATUS[] = {
            "BOOKED",
            "APPROVED",
            "ALLOCATED",
            "CANCELLED",
            "COMPLETED",
            "TRAVELLING",
            "DENIED"
    };
    //TODO: Use colours in resources
    public static int cSTATUS[] = {
            Color.LTGRAY,
            Color.BLUE,
            Color.GREEN,
            Color.RED,
            R.color.dark_green,
            Color.CYAN,
            Color.RED
    };

    SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
   /* Date date = sdfDateTime.parse(trip.getString("date"));
    Date tripDate = sdfDate.parse(trip.getString("trip_date"));
    Date tripTime = sdfTime.parse(trip.getString("trip_time"));*/

    public Trip(Integer id, String startMileage, String endMileage, String tripDate, String tripTime, String date, String startTime, String stopTime, JSONObject vehicleDriver, JSONObject tripCreator, String startCoord, String stopCoord, Integer approval, String startName, String endName,JSONArray group) {
        this.id = id;
        this.startMileage = startMileage;
        this.endMileage = endMileage;
        this.tripDate = tripDate;
        this.tripTime = tripTime;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.vehicleDriver = vehicleDriver;
        this.tripCreator = tripCreator;
        this.startCoord = startCoord;
        this.stopCoord = stopCoord;
        this.startName = startName;
        this.endName = endName;
        this.approval = approval;
        this.group = group;
    }

    public Trip(Integer id, String startMileage, String endMileage, String tripDate, String tripTime, String date, String startTime, String stopTime, String startCoord, String stopCoord, Integer approval, String startName, String endName) {
        this.id = id;
        this.startMileage = startMileage;
        this.endMileage = endMileage;
        this.tripDate = tripDate;
        this.tripTime = tripTime;
        this.date = date;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.startCoord = startCoord;
        this.stopCoord = stopCoord;
        this.approval = approval;
        this.startName = startName;
        this.endName = endName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartMileage() {
        return startMileage;
    }

    public void setStartMileage(String startMileage) {
        this.startMileage = startMileage;
    }

    public String getEndMileage() {
        return endMileage;
    }

    public void setEndMileage(String endMileage) {
        this.endMileage = endMileage;
    }

    public String getTripDate() {
        return tripDate;
    }
    public String getTripDate(SimpleDateFormat sdf) {
        return  reFormatDate(tripDate,sdfDate,sdf);
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getTripTime() {
        return tripTime;
    }
    public String getTripTime(SimpleDateFormat sdf) {
        return  reFormatDate(tripTime,sdfTime,sdf);
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public JSONObject getVehicleDriver() {
        return vehicleDriver;
    }

    public void setVehicleDriver(JSONObject vehicleDriver) {
        this.vehicleDriver = vehicleDriver;
    }

    public JSONObject getTripCreator() {
        return tripCreator;
    }

    public void setTripCreator(JSONObject tripCreator) {
        this.tripCreator = tripCreator;
    }

    public String getStartCoord() {
        return startCoord;
    }

    public void setStartCoord(String startCoord) {
        this.startCoord = startCoord;
    }

    public String getStopCoord() {
        return stopCoord;
    }

    public void setStopCoord(String stopCoord) {
        this.stopCoord = stopCoord;
    }

    public Integer getApproval() {
        return approval;
    }

    public void setApproval(Integer approval) {
        this.approval = approval;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public JSONArray getGroup() {
        return group;
    }

    public void setGroup(JSONArray group) {
        this.group = group;
    }

    private String reFormatDate(String date, SimpleDateFormat sdf1, SimpleDateFormat sdf2){
        try {
            Date mDate = sdf1.parse(date);
            return sdf2.format(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }
}
