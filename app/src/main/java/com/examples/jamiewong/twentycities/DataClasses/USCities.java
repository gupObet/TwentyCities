package com.examples.jamiewong.twentycities.DataClasses;

/**
 * Created by jamiewong on 11/17/16.
 */
public class USCities {

    /* example
    "city": "Edna",
    "state": "TX",
    "pop": 5499,
    "lat": 28.97859,
    "lon": -96.64609
     */

    private String city;
    private String state;
    private int pop;
    private double lat;
    private double lon;

    //this field is added later, not from US.json
    private float disFromEntPoint;

    private float constMetric;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setDisFromEntPoint(float disFromEntPoint) {
        this.disFromEntPoint = disFromEntPoint;
    }

    public float getDisFromEntPoint() {
        return disFromEntPoint;
    }

    public void setConstMetric(float constMetric) {
        this.constMetric = constMetric;
    }

    public float getConstMetric() {
        return constMetric;
    }
}
