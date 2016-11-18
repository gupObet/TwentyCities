package com.examples.jamiewong.twentycities;

import android.content.res.AssetManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
//import android.util.JsonReader;
import com.google.gson.stream.JsonReader;


import com.examples.jamiewong.twentycities.DataClasses.USCities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    //Todo: removed static keyword so can use getClass, instead of this.getClass()
    private final String MAIN_ACT = getClass().getSimpleName();
    private EditText etLat;
    private EditText etLng;
    private ListView listViewCR;
    //Todo: check if I need to new here or in onCreate, make this a static field, instead of passing around
    private ArrayList<USCities> usCitiesArrayList = new ArrayList<>();
    private double entLat;
    private double entLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setResourceViews();

        //first, convert US.json to Gson arraylist
        convertUSjsonToGson();
    }

    private void setResourceViews() {

        etLat = (EditText) findViewById(R.id.etLat);
        etLng = (EditText) findViewById(R.id.etLng);
        listViewCR = (ListView) findViewById(R.id.lvCityResults);

    }

    private void convertUSjsonToGson() {

        InputStreamReader ims = null;
        JsonReader jsonReader = null;
        Gson gson = new Gson();

        AssetManager assetManager = getAssets();

        try {
            //Todo: double check if I need UTF-8
            ims = new InputStreamReader(assetManager.open("US.json"), "UTF-8");
            jsonReader = new JsonReader(ims);

            final Type USCITIESTYPE = new TypeToken<ArrayList<USCities>>() {
            }.getType();
            usCitiesArrayList = gson.fromJson(jsonReader, USCITIESTYPE);
            //printUSCities(usCitiesArrayList);
            //calcDistance(usCitiesArrayList);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void calcDistToAllPoints(double entLat, double entLng) {

        //Todo: should I initialize all these to 0
        double endLat = 0, endLng = 0;
        float distance = 0, pop = 0, constraintMetric = 0;

        //use distanceTo
        //34.1072305(lat), -118.0578456(lng)
        Location enteredPoint = new Location("locationCur");
        enteredPoint.setLatitude(entLat);
        enteredPoint.setLongitude(entLng);

        Location endPoint = new Location("locationEnd");

        for (int i = 1; i < usCitiesArrayList.size(); i++) {  //just testing 50 for now

            endLat = usCitiesArrayList.get(i).getLat();
            endLng = usCitiesArrayList.get(i).getLon();

            endPoint.setLatitude(endLat);
            endPoint.setLongitude(endLng);

            //Todo: does this return exception?
            distance = (enteredPoint.distanceTo(endPoint)) / 1000;  //in km

            //Log.d(MAIN_ACT, "i=" + i + ", distance = " + distance);
            //Log.d(MAIN_ACT, "\n");
            usCitiesArrayList.get(i).setDisFromEntPoint(distance);

            pop = usCitiesArrayList.get(i).getPop();

            if (distance != 0) {
                constraintMetric = pop / distance;
                usCitiesArrayList.get(i).setConstMetric(constraintMetric);
            }
        }

        //Todo: should I sort here or sort at calling function submit

    }

    /**
     * sort by field constraintMetric in object USCities in desc order
     */
    private void sortMetricField() {
        Collections.sort(usCitiesArrayList, new Comparator<USCities>() {
            @Override
            public int compare(USCities obj1, USCities obj2) {
                return Float.valueOf(obj2.getConstMetric()).compareTo(obj1.getConstMetric());
            }
        });
    }

    private void displayInListView() {

    }

    private void printUSCities() {

        Log.d(MAIN_ACT, "printing first 20: \n");
        for (int i = 1; i < 20/*usCitiesArrayList.size()*/; i++) {
            Log.d(MAIN_ACT, "first ten: " + usCitiesArrayList.get(i).getCity() + "\n");
            Log.d(MAIN_ACT, "first ten: " + usCitiesArrayList.get(i).getState() + "\n");
            Log.d(MAIN_ACT, "first ten: " + usCitiesArrayList.get(i).getPop() + "\n");
            Log.d(MAIN_ACT, "first ten: " + usCitiesArrayList.get(i).getLat() + "\n");
            Log.d(MAIN_ACT, "first ten: " + usCitiesArrayList.get(i).getLon() + "\n");
            Log.d(MAIN_ACT, "first ten: " + usCitiesArrayList.get(i).getDisFromEntPoint() + "\n");
            Log.d(MAIN_ACT, "first ten: " + usCitiesArrayList.get(i).getConstMetric() + "\n");


            Log.d(MAIN_ACT, "---------\n");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void submit(View view) {

        entLat = Double.parseDouble(etLat.getText().toString());
        entLng = Double.parseDouble(etLng.getText().toString());

        Log.d(MAIN_ACT, "entered Lat: " + entLat + "\n");
        Log.d(MAIN_ACT, "entered Lng: " + entLng + "\n");

        calcDistToAllPoints(entLat, entLng);
        sortMetricField();
        printUSCities();
        displayInListView();
    }

    public void clear(View view) {

    }
}
