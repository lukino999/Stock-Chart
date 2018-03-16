package com.example.luca.stockcharts_volley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String SHARED_PREF_API_KEY = "apiKey";

    private LinkedHashMap<String, String> timeSeriesHash;
    private LinkedHashMap<String, String> symbolsHash = new LinkedHashMap<>();

    // when apiKey is still demo the urlBase is:
    private String urlBase = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=demo";

    // it will hold the apiKey to be used throughout this activity
    private String apiKey;

    private EditText apiKeyEditText;
    private SharedPreferences sharedPref;
    private Spinner timeSeries;
    private SearchView searchField;
    private Spinner symbolSpinner;
    // TimeSeriesSpinnerMap class contains the key values pairs to fill the spinner.
    private TimeSeriesSpinnerMap timeSeriesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 15/03/2018 customise keyboard action button

        // instantiates and assign sharedPref
        sharedPref = getPreferences(Context.MODE_PRIVATE);

        // gets apiKey. If null, defaults to "demo"
        apiKey = sharedPref.getString(SHARED_PREF_API_KEY, "demo");

        // initialize apiKeyEditText
        apiKeyEditText = findViewById(R.id.apiKey_editText);

        // initialize timeSeries spinner
        timeSeries = findViewById(R.id.time_series);

        // initialise timeSeriesMap
        timeSeriesMap = new TimeSeriesSpinnerMap();

        // if key is not demo, unlock full features
        if (apiKey != "demo") {
            unlockFullFeatures();
        }
    }

    private void unlockFullFeatures() {

        // change getChartButton text from "Get demo chart" to "Get chart"
        Button getChartButton = findViewById(R.id.getChartButton);
        getChartButton.setText("Get chart");

        // remove the apiKey_editText and the checkApiKeyButton
        apiKeyEditText.setVisibility(View.INVISIBLE);
        findViewById(R.id.checkApiKeyButton).setVisibility(View.INVISIBLE);

        // show "Symbols" spinner
        showSymbolsSpinner();

        // show "Timeframes" spinner
        showTimeSeries();

        return;
    }

    private void showSymbolsSpinner() {

        // read nasdaq.csv and build symbolsHash
        getSymbolList();

        // initialise
        searchField = findViewById(R.id.search_symbol);
        symbolSpinner = findViewById(R.id.symbol_spinner);

        // set symbol search and spinner as visible
        searchField.setVisibility(View.VISIBLE);
        symbolSpinner.setVisibility(View.VISIBLE);

        // instantiate list to be passed to the adapter
        List<String> symbolArray = new ArrayList<>();

        // iterate through hash and add to list
        Iterator iterator = symbolsHash.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
            symbolArray.add(mapEntry.getKey());
        }

        // instantiate adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, symbolArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        symbolSpinner.setAdapter(arrayAdapter);
    }

    private void getSymbolList() {

        // get inputStream from /res/raw/nasdaq.csv
        InputStream inputStream = getResources().openRawResource(R.raw.nasdaq);

        // get stream reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                String[] value = line.split(",");
                symbolsHash.put(value[0], value[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void showTimeSeries() {

        // make the timeSeries spinner visible
        timeSeries.setVisibility(View.VISIBLE);

        // get the timeseries hash map
        timeSeriesHash = timeSeriesMap.getTimeSeries();

        // instantiate the list to be passed to the adapter
        List<String> timeSeriesArray = new ArrayList<>();

        // iterate through timeSeriesHash and add each key to the list
        Iterator iterator = timeSeriesHash.entrySet().iterator();
        while (iterator.hasNext()) {
            //Map.Entry mapEntry = (Map.Entry) iterator.next();
            Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) iterator.next();
            timeSeriesArray.add(mapEntry.getKey().toString());
        }

        // instantiate the adapter
        ArrayAdapter<String> timeSeriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeSeriesArray);
        timeSeriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSeries.setAdapter(timeSeriesAdapter);

    }


    public void getChartButtonClick(View view) {

        // intent to open GetChart activity
        Intent i = new Intent(this, GetChart.class);

        // conditional whether to use demo query
        if (apiKey == "demo"){
            i.putExtra("url", "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=demo");
        } else {
            //calls getUrl() which builds the query based on the spinners selections
            String url = getUrl();
            i.putExtra("url", url);
        }

        // calls the GetChart activity
        startActivity(i);
    }

    private String getUrl() {

        // base url query
        String url = "https://www.alphavantage.co/query?";

        // get the function
        String function = timeSeriesHash.get(timeSeries.getSelectedItem().toString());

        // get the symbol
        String symbol = symbolSpinner.getSelectedItem().toString();

        // put it together
        url += function + "&symbol="+ symbol + "&apikey=" + apiKey;

        return url;
    }


    public void checkAPIkeyButton(View view) {

        // if isAPIkeyValid() is successful it will unlockFullFeatures() from there
        String userKey = apiKeyEditText.getText().toString();
        isAPIkeyValid(userKey);

    }


    private void isAPIkeyValid(final String key) {

        /*
        check apiKey validity:
        query AAPL (Apple) using the API key provided
        If the response contains Meta Data, it is valid
         */

        // https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=demo
        Log.i("key", key);
        String testURL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=AAPL&apikey=" + key;

        // call the query above and check whether it returns "Metadata" or "Information"
        // instantiate and initialize volley requestQueue
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        // instantiate jsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, testURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Check API key response", response.toString() );
                boolean hasMetadata = response.has("Meta Data");
                Log.i("response.has(Meta Data)", String.valueOf(hasMetadata));

                // If response has "Meta Data", call the unlock full features
                if (hasMetadata) {
                    // assign the entered key to apiKey
                    apiKey = key;

                    // save it on sharedPref
                    SharedPreferences.Editor sharePredEdit = sharedPref.edit();
                    sharePredEdit.putString(SHARED_PREF_API_KEY, apiKey);
                    sharePredEdit.commit();
                    // debug: check whether it did save it
                    Log.i("SharedPref", String.valueOf(sharedPref.getString(SHARED_PREF_API_KEY, "demo")));

                    // unlock full features
                    Toast.makeText(getApplicationContext(), "Full features unlocked", Toast.LENGTH_SHORT ).show();

                    unlockFullFeatures();
                } else {
                    // notify user the key is not valid
                    Toast.makeText(getApplicationContext(), "Invalid API key", Toast.LENGTH_SHORT ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("getData", "Something went wrong");
                Toast.makeText(getApplicationContext(), "Unable to perform request", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }


}

