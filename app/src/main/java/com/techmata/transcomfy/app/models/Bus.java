package com.techmata.transcomfy.app.models;

/**
 * Created by Sparks on 08/09/2016.
 */
public class Bus {
    Integer bus_id;
    Integer capacity;
    String numberPlate, routeName;

    public Bus(Integer bus_id, Integer capacity, String numberPlate, String routeName) {
        this.bus_id = bus_id;
        this.capacity = capacity;
        this.numberPlate = numberPlate;
        this.routeName = routeName;
    }

    public Integer getBus_id() {
        return bus_id;
    }

    public void setBus_id(Integer bus_id) {
        this.bus_id = bus_id;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
