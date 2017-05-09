package com.examples.jamiewong.twentycities;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.examples.jamiewong.twentycities.DataClasses.USCities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**Challenge problem 2 -- return 20 twenties, chosen based on distance to entered lat, lng and
 * population of the city, given by a file US.txt
 * Created by jamiewong 11/17/2016
 */

public class MainActivity extends AppCompatActivity {

    private final String MAIN_ACT = getClass().getSimpleName();
    private EditText etLat;
    private EditText etLng;
    private ListView listViewCR;
    private ArrayList<USCities> usCitiesArrayList;
    private double entLat;
    private double entLng;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setResourceViews();
        convertUSjsonToGson();
    }

    private void setResourceViews() {

        etLat = (EditText) findViewById(R.id.etLat);
        etLng = (EditText) findViewById(R.id.etLng);
        listViewCR = (ListView) findViewById(R.id.lvCityResults);

    }

    /**
     * US.json file was converted from US.txt file and placed in assets folder
     * InputStreamReader is used to open the json file and further read by the JsonReader
     * Gson is used to parse the json file to an arrayList of objects of type USCities
     */
    private void convertUSjsonToGson() {

        InputStreamReader ims = null;
        JsonReader jsonReader = null;
        Gson gson = new Gson();

        AssetManager assetManager = getAssets();

        try {
            ims = new InputStreamReader(assetManager.open("US.json"), "UTF-8");
            jsonReader = new JsonReader(ims);

            final Type USCITIESTYPE = new TypeToken<ArrayList<USCities>>() {}.getType();
            usCitiesArrayList = gson.fromJson(jsonReader, USCITIESTYPE);

        } catch (IOException e) {
            usCitiesArrayList = new ArrayList<USCities>(); // empty and non-null
            e.printStackTrace();
        }
    }

    /**
     * Uses the distanceTo api to calculate the distance between two geopoints.  The enteredPoint is
     * the lat lng entered by the user, and endPoint is a lat lng from the list of cities in US.json
     * file.  After distance is calculated, it is stored in the object.
     *
     * Then a metric constraint is calculated using distance/(pop + 1), and stored in the object.
     * Distance is on top so I don't have to check for distance = zero if the same lat, lng.  This
     * metric is used to determine which cities are chosen.  The smallest values of this constraint
     * metric (constMetric) will be chosen.  Turns out it works very well.
     *
     * @param entLat -- usered entered latitude
     * @param entLng -- usered entered longitude
     */
    private void calcDistToAllPoints(double entLat, double entLng) {
        //34.1072305(lat), -118.0578456(lng)
        Location enteredPoint = new Location("locationCur");
        enteredPoint.setLatitude(entLat);
        enteredPoint.setLongitude(entLng);

        Location endPoint = new Location("locationEnd");

        for (int i = 1; i < usCitiesArrayList.size(); i++) {
            double endLat, endLng;
            float distance, pop, constraintMetric;

            endLat = usCitiesArrayList.get(i).getLat();
            endLng = usCitiesArrayList.get(i).getLon();

            endPoint.setLatitude(endLat);
            endPoint.setLongitude(endLng);

            distance = (enteredPoint.distanceTo(endPoint)) / 1000;  //in km

            //Log.d(MAIN_ACT, "i=" + i + ", distance = " + distance + "\n");
            usCitiesArrayList.get(i).setDisFromEntPoint(distance);

            pop = usCitiesArrayList.get(i).getPop();

            if (pop < 0) {
                Log.w(MAIN_ACT, "Found city (" + usCitiesArrayList.get(i).getCity() +
                        ", " + usCitiesArrayList.get(i).getState() + ") with negative population (" +
                        usCitiesArrayList.get(i).getPop());
                continue;
            }

            constraintMetric = distance / (pop + 1);
            usCitiesArrayList.get(i).setConstMetric(constraintMetric);
        }
    }

    /**
     * Sort by field constraintMetric in arrayList of USCities in ascending order
     */
    private void sortMetricField() {
        Collections.sort(usCitiesArrayList, new Comparator<USCities>() {
            @Override
            public int compare(USCities obj1, USCities obj2) {
                return Float.valueOf(obj1.getConstMetric()).compareTo(obj2.getConstMetric());
            }
        });
    }

    private void printSortedUSCities() {

        Log.d(MAIN_ACT, "printing first 20: \n");

        for (int i = 1; i < 20; i++) {
            Log.d(MAIN_ACT, "i = " + i + "\n");
            Log.d(MAIN_ACT, usCitiesArrayList.get(i).getCity() + "\n");
            Log.d(MAIN_ACT, usCitiesArrayList.get(i).getState() + "\n");
            Log.d(MAIN_ACT, usCitiesArrayList.get(i).getPop() + "\n");
            Log.d(MAIN_ACT, usCitiesArrayList.get(i).getLat() + "\n");
            Log.d(MAIN_ACT, usCitiesArrayList.get(i).getLon() + "\n");
            Log.d(MAIN_ACT, usCitiesArrayList.get(i).getDisFromEntPoint() + "\n");
            Log.d(MAIN_ACT, usCitiesArrayList.get(i).getConstMetric() + "\n");
            Log.d(MAIN_ACT, "---------\n");
        }
    }

    /**
     * Creates an arrayList firstTwenty with the first twenty cities from usCitiesArrayList
     * @return firstTwentyArrayList of type USCities
     */
    private ArrayList<String> createListOfFirstTwenty() {

        ArrayList<String> firstTwenty = new ArrayList<>();

        for (int i = 1; i < 21; i++) {
            firstTwenty.add(usCitiesArrayList.get(i).getCity());
        }
        return firstTwenty;
    }

    /**
     * Display the twenty cities that has smallest constraint metric
     * Uses a simple ArrayAdapter to show in listview, listViewCR
     */
    private void displayInListView() {

        adapter = new ArrayAdapter<String>(this, R.layout.listview_city_row, R.id.tvCityName,
                createListOfFirstTwenty());

        listViewCR.setAdapter(adapter);

        bringKeyboardDown();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * When submit is pressed, it first gets the entered lat, lng and then calculate distance to all
     * cities in the US.json file.  Then it sorts the arraylist usCitiesArrayList by the field
     * constMetric, followed by dropping the first twenty cities in a new arrayList firstTwenty and
     * finally display the firstTwenty in a simple listview.
     * @param view -- button submit
     */
    public void submit(View view) {

        if(!etLat.getText().toString().trim().isEmpty() &&
                !etLng.getText().toString().trim().isEmpty()) {

            entLat = Double.parseDouble(etLat.getText().toString());
            entLng = Double.parseDouble(etLng.getText().toString());

            Log.d(MAIN_ACT, "entered Lat: " + entLat + "\n");
            Log.d(MAIN_ACT, "entered Lng: " + entLng + "\n");

            calcDistToAllPoints(entLat, entLng);
            sortMetricField();
            printSortedUSCities();
            createListOfFirstTwenty();
            displayInListView();
        }
    }

    private void bringKeyboardDown() {

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etLat.getWindowToken(), 0);

    }

    public void clear(View view) {

        etLat.getText().clear();
        etLng.getText().clear();

        if(adapter!=null) {
            adapter.clear();
        }
        etLat.requestFocus();
    }
}
